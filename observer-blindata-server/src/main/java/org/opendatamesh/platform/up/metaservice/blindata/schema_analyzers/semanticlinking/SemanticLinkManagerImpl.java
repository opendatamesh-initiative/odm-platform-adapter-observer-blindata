package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDSemanticLinkingClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.*;
import org.opendatamesh.platform.up.metaservice.blindata.resources.exceptions.BlindataClientException;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.util.*;

import static org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;

class SemanticLinkManagerImpl implements SemanticLinkManager {

    private final BDSemanticLinkingClient client;

    public SemanticLinkManagerImpl(BDSemanticLinkingClient client) {
        this.client = client;
    }

    @Override
    public void enrichWithSemanticContext(BDPhysicalEntityRes physicalEntity, Map<String, Object> sContext) {
        Set<String> additionalConcepts = new HashSet<>();
        enrichPhysicalFieldsWithSemanticLinks(sContext, physicalEntity, additionalConcepts);
        linkPhysicalEntityToDataCategory(sContext, physicalEntity, additionalConcepts);
    }

    private void enrichPhysicalFieldsWithSemanticLinks(Map<String, Object> sContext, BDPhysicalEntityRes physicalEntity, Set<String> additionalConcepts) {
        withErrorHandling(() -> {
            final Map<String, BDSemanticLink> semanticLinksByPhysicalFieldName = getSemanticLinksByPhysicalFieldName(physicalEntity.getPhysicalFields(), sContext, additionalConcepts);
            semanticLinksByPhysicalFieldName.forEach((physicalFieldName, semanticLink) -> {
                LogicalFieldSemanticLinkRes semanticLinkObject = client.getSemanticLinkElements(
                        semanticLink.getSemanticLinkString(),
                        semanticLink.getDefaultNamespaceIdentifier()
                );
                if (semanticLinkObject == null) {
                    getUseCaseLogger().warn("It is not possible to resolve the semantic elements (concepts and attributes) contained in the semantic link path.");
                } else {
                    addSemanticLinkToPhysicalField(physicalEntity.getPhysicalFields(), physicalFieldName, semanticLinkObject);
                }
            });
        });
    }

    private void linkPhysicalEntityToDataCategory(Map<String, Object> sContext, BDPhysicalEntityRes physicalEntity, Set<String> additionalConcepts) {
        withErrorHandling(() -> {
            String defaultNamespaceIdentifier = Optional.ofNullable(((String) sContext.get("s-base")))
                    .orElse("");
            if (!StringUtils.hasText(defaultNamespaceIdentifier)) {
                getUseCaseLogger().warn("Namespace Identifier not present when linking physical entity to concept");
                return;
            }
            Optional<BDLogicalNamespaceRes> rootNamespace = client.getLogicalNamespaceByIdentifier(defaultNamespaceIdentifier);
            if (rootNamespace.isEmpty()) {
                getUseCaseLogger().warn(String.format("Namespace: %s not found when linking physical entity to concept", defaultNamespaceIdentifier));
                return;
            }
            String dataCategoryName = Optional.ofNullable(((String) sContext.get("s-type")))
                    .orElse("")
                    .replaceAll("[\\[\\]]", "");
            if (!StringUtils.hasText(dataCategoryName)) {
                getUseCaseLogger().warn("Concept Name not present when linking physical entity to concept");
                return;
            }
            Set<BDDataCategoryRes> concepts = new HashSet<>();
            additionalConcepts.add(dataCategoryName);
            for (String concept : additionalConcepts) {
                Optional<BDDataCategoryRes> dataCategoryRes = client.getDataCategoryByNameAndNamespaceUuid(concept, rootNamespace.get().getUuid());
                if (dataCategoryRes.isEmpty()) {
                    getUseCaseLogger().warn(String.format("Concept: %s not found when linking it to Physical Entity: %s .", concept, physicalEntity.getName()));
                    return;
                }
                concepts.add(dataCategoryRes.get());
            }
            physicalEntity.setDataCategories(concepts);
        });
    }

    private void addSemanticLinkToPhysicalField(Set<BDPhysicalFieldRes> physicalFieldResList, String pfName, LogicalFieldSemanticLinkRes semanticLinkRes) {
        physicalFieldResList.stream()
                .filter(field -> pfName.equals(field.getName()))
                .findFirst()
                .ifPresent(physicalField -> physicalField.setLogicalFields(Lists.newArrayList(semanticLinkRes)));
    }

    private Map<String, BDSemanticLink> getSemanticLinksByPhysicalFieldName(Set<BDPhysicalFieldRes> physicalFieldResList, Map<String, Object> sContext, Set<String> additionalConcepts) {
        Map<String, BDSemanticLink> semanticLinkMapByPhysicalFieldName = new TreeMap<>();
        String defaultNamespaceIdentifier = (String) sContext.get("s-base");
        String rootCategoryName = (String) sContext.get("s-type");

        for (BDPhysicalFieldRes physicalFieldRes : physicalFieldResList) {
            String fieldName = physicalFieldRes.getName();
            Object fieldContext = sContext.get(fieldName);
            if (fieldContext != null) {
                extractSemanticLinks(fieldName, fieldContext, defaultNamespaceIdentifier, rootCategoryName, "", semanticLinkMapByPhysicalFieldName, additionalConcepts);
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
            Map<String, BDSemanticLink> semanticLinkMap,
            Set<String> additionalConcepts) {

        if (fieldContext instanceof String) {
            String semanticPath = parentPath.isEmpty() ? (String) fieldContext : parentPath + "." + fieldContext;

            // Determine if the path is absolute by checking if it starts with a concept (enclosed in square brackets)
            boolean isAbsolute = semanticPath.matches("^\\[[^\\]]+\\].*");
            if (isAbsolute) {
                // Extract the starting concept and add it to the set
                String startingConcept = semanticPath.substring(0, semanticPath.indexOf("]") + 1).replaceAll("[\\[\\]]", "");
                additionalConcepts.add(startingConcept);
            } else {
                semanticPath = rootCategory + "." + semanticPath;
            }

            BDSemanticLink semanticLink = new BDSemanticLink();
            semanticLink.setDefaultNamespaceIdentifier(namespace);
            semanticLink.setSemanticLinkString(semanticPath);
            semanticLinkMap.put(fieldName, semanticLink);
        } else if (fieldContext instanceof Map) {
            Map<String, Object> nestedContext = (Map<String, Object>) fieldContext;
            String nestedType = (String) nestedContext.get("s-type");
            for (Map.Entry<String, Object> entry : nestedContext.entrySet()) {
                if (!"s-type".equals(entry.getKey())) {
                    String nestedFieldName = entry.getKey();
                    Object nestedFieldValue = entry.getValue();
                    String newParentPath = parentPath.isEmpty() ? nestedType : parentPath + "." + nestedType;
                    extractSemanticLinks(fieldName + "." + nestedFieldName, nestedFieldValue, namespace, rootCategory, newParentPath, semanticLinkMap, additionalConcepts);
                }
            }
        }
    }

    private void withErrorHandling(Runnable runnable) {
        try {
            runnable.run();
        } catch (BlindataClientException e) {
            if (e.getCode() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                throw e;
            } else {
                getUseCaseLogger().warn(e.getMessage(), e);
            }
        }
    }
}
