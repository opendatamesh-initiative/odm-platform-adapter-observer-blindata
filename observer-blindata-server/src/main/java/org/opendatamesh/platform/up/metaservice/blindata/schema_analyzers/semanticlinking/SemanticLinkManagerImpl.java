package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDSemanticLinkingClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.*;
import org.opendatamesh.platform.up.metaservice.blindata.resources.exceptions.BlindataClientException;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseIncorrectInputException;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

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


                LogicalFieldSemanticLinkRes semanticLinkObject = Optional.ofNullable(client.getSemanticLinkElements(
                        semanticLink.getSemanticLinkString(),
                        semanticLink.getDefaultNamespaceIdentifier()
                )).orElseThrow(() -> new UseCaseIncorrectInputException("It is not possible to resolve the semantic elements (concepts and attributes) contained in the semantic link path."));

                addSemanticLinkToPhysicalField(physicalEntity.getPhysicalFields(), physicalFieldName, semanticLinkObject);
            });
        });
    }

    @Override
    public void linkPhysicalEntityToDataCategory(Map<String, Object> sContext, BDPhysicalEntityRes physicalEntity) {
        withErrorHandling(() -> {
            String defaultNamespaceIdentifier = Optional.ofNullable(((String) sContext.get("s-base")))
                    .orElseThrow()
                    .replaceAll("[\\[\\]]", "");

            BDLogicalNamespaceRes rootNamespace = client.getLogicalNamespaceByIdentifier(defaultNamespaceIdentifier)
                    .orElseThrow(() -> new UseCaseIncorrectInputException(String.format("Namespace: %s not found when linking physical entity to concept", defaultNamespaceIdentifier)));

            String dataCategoryName = Optional.ofNullable(((String) sContext.get("s-type")))
                    .orElseThrow()
                    .replaceAll("[\\[\\]]", "");

            final BDDataCategoryRes dataCategoryRes = client.getDataCategoryByNameAndNamespaceUuid(dataCategoryName, rootNamespace.getUuid())
                    .orElseThrow(() -> new UseCaseIncorrectInputException(String.format("Concept: %s not found when linking it to Physical Entity: %s .", dataCategoryName, physicalEntity.getName())));

            physicalEntity.setDataCategories(Sets.newHashSet(dataCategoryRes));
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
            if (e.getCode() != HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                throw new UseCaseIncorrectInputException(e.getMessage(), e);
            } else {
                throw e;
            }
        }
    }
}
