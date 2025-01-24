package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDSemanticLinkingClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.*;
import org.opendatamesh.platform.up.metaservice.blindata.resources.exceptions.BlindataClientException;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseRecoverableException;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import static org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseRecoverableExceptionContext.getExceptionHandler;

class SemanticLinkManagerImpl implements SemanticLinkManager {

    private final BDSemanticLinkingClient client;

    public SemanticLinkManagerImpl(BDSemanticLinkingClient client) {
        this.client = client;
    }

    @Override
    public void enrichPhysicalFieldsWithSemanticLinks(Map<String, Object> sContext, BDPhysicalEntityRes physicalEntity) {
        withErrorHandling(() -> {
            final Map<String, BDSemanticLink> semanticLinksByPhysicalFieldName = getSemanticLinksByPhysicalFieldName(physicalEntity.getPhysicalFields(), sContext);
            semanticLinksByPhysicalFieldName.forEach((physicalFieldName, semanticLink) -> {
                LogicalFieldSemanticLinkRes semanticLinkObject = client.getSemanticLinkElements(
                        semanticLink.getSemanticLinkString(),
                        semanticLink.getDefaultNamespaceIdentifier()
                );
                if (semanticLinkObject == null) {
                    getExceptionHandler().warn(new UseCaseRecoverableException("It is not possible to resolve the semantic elements (concepts and attributes) contained in the semantic link path."));
                } else {
                    addSemanticLinkToPhysicalField(physicalEntity.getPhysicalFields(), physicalFieldName, semanticLinkObject);
                }
            });
        });
    }

    @Override
    public void linkPhysicalEntityToDataCategory(Map<String, Object> sContext, BDPhysicalEntityRes physicalEntity) {
        withErrorHandling(() -> {
            String defaultNamespaceIdentifier = Optional.ofNullable(((String) sContext.get("s-base")))
                    .orElse("")
                    .replaceAll("[\\[\\]]", "");
            if (!StringUtils.hasText(defaultNamespaceIdentifier)) {
                getExceptionHandler().warn(new UseCaseRecoverableException("Namespace Identifier not present when linking physical entity to concept"));
                return;
            }
            Optional<BDLogicalNamespaceRes> rootNamespace = client.getLogicalNamespaceByIdentifier(defaultNamespaceIdentifier);
            if (rootNamespace.isEmpty()) {
                getExceptionHandler().warn(new UseCaseRecoverableException(String.format("Namespace: %s not found when linking physical entity to concept", defaultNamespaceIdentifier)));
                return;
            }
            String dataCategoryName = Optional.ofNullable(((String) sContext.get("s-type")))
                    .orElse("")
                    .replaceAll("[\\[\\]]", "");
            if (!StringUtils.hasText(dataCategoryName)) {
                getExceptionHandler().warn(new UseCaseRecoverableException("Concept Name not present when linking physical entity to concept"));
                return;
            }
            Optional<BDDataCategoryRes> dataCategoryRes = client.getDataCategoryByNameAndNamespaceUuid(dataCategoryName, rootNamespace.get().getUuid());
            if (dataCategoryRes.isEmpty()) {
                getExceptionHandler().warn(new UseCaseRecoverableException(String.format("Concept: %s not found when linking it to Physical Entity: %s .", dataCategoryName, physicalEntity.getName())));
                return;
            }
            physicalEntity.setDataCategories(Sets.newHashSet(dataCategoryRes.get()));
        });
    }

    private void addSemanticLinkToPhysicalField(Set<BDPhysicalFieldRes> physicalFieldResList, String pfName, LogicalFieldSemanticLinkRes semanticLinkRes) {
        physicalFieldResList.stream()
                .filter(field -> pfName.equals(field.getName()))
                .findFirst()
                .ifPresent(physicalField -> physicalField.setLogicalFields(Lists.newArrayList(semanticLinkRes)));
    }

    private Map<String, BDSemanticLink> getSemanticLinksByPhysicalFieldName(Set<BDPhysicalFieldRes> physicalFieldResList, Map<String, Object> sContext) {
        Map<String, BDSemanticLink> semanticLinkMapByPhysicalFieldName = new TreeMap<>();
        String defaultNamespaceIdentifier = (String) sContext.get("s-base");
        String rootCategoryName = (String) sContext.get("s-type");

        for (BDPhysicalFieldRes physicalFieldRes : physicalFieldResList) {
            String fieldName = physicalFieldRes.getName();
            Object fieldContext = sContext.get(fieldName);
            if (fieldContext != null) {
                extractSemanticLinks(fieldName, fieldContext, defaultNamespaceIdentifier, rootCategoryName, "", semanticLinkMapByPhysicalFieldName);
            }
        }
        return semanticLinkMapByPhysicalFieldName;
    }

    private void extractSemanticLinks(
            String fieldName,
            Object fieldContext,
            String namespace,
            String rootCategory,
            String parentPath,
            Map<String, BDSemanticLink> semanticLinkMap) {

        if (fieldContext instanceof String) {
            String fieldPath = parentPath.isEmpty() ? (String) fieldContext : parentPath + "." + fieldContext;
            BDSemanticLink semanticLink = createSemanticLink(namespace, rootCategory, fieldPath);
            semanticLinkMap.put(fieldName, semanticLink);
        } else if (fieldContext instanceof Map) {
            Map<String, Object> nestedContext = (Map<String, Object>) fieldContext;
            String nestedType = (String) nestedContext.get("s-type");
            for (Map.Entry<String, Object> entry : nestedContext.entrySet()) {
                if (!"s-type".equals(entry.getKey())) {
                    String nestedFieldName = entry.getKey();
                    Object nestedFieldValue = entry.getValue();
                    String newParentPath = parentPath.isEmpty() ? nestedType : parentPath + "." + nestedType;
                    extractSemanticLinks(fieldName + "." + nestedFieldName, nestedFieldValue, namespace, rootCategory, newParentPath, semanticLinkMap);
                }
            }
        }
    }


    private BDSemanticLink createSemanticLink(String namespace, String rootCategory, String fieldPath) {
        BDSemanticLink semanticLink = new BDSemanticLink();
        semanticLink.setDefaultNamespaceIdentifier(namespace);
        semanticLink.setSemanticLinkString(rootCategory + "." + fieldPath);
        return semanticLink;
    }

    private void withErrorHandling(Runnable runnable) {
        try {
            runnable.run();
        } catch (BlindataClientException e) {
            if (e.getCode() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                throw e;
            } else {
                getExceptionHandler().warn(new UseCaseRecoverableException(e.getMessage(), e));
            }
        }
    }
}
