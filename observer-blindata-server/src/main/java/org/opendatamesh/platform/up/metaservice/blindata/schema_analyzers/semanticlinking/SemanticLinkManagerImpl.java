package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDSemanticLinkingClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataCategoryRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDPhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDPhysicalFieldRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.LogicalFieldSemanticLinkRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.exceptions.BlindataClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class SemanticLinkManagerImpl implements SemanticLinkManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final BDSemanticLinkingClient client;

    public SemanticLinkManagerImpl(BDSemanticLinkingClient client) {
        this.client = client;
    }

    public void enrichPhysicalFieldsWithSemanticLinks(Map<String, Object> sContext, BDPhysicalEntityRes physicalEntity) {
        if (sContext != null && physicalEntity != null) {
            final Map<String, BDSemanticLink> semanticLinksByPhysicalFieldName = getSemanticLinksByPhysicalFieldName(physicalEntity.getPhysicalFields(), sContext);
            for (Map.Entry<String, BDSemanticLink> entry : semanticLinksByPhysicalFieldName.entrySet()) {
                String pfName = entry.getKey();
                BDSemanticLink semanticLink = entry.getValue();
                LogicalFieldSemanticLinkRes extractedSemanticLinkFromBlindata = resolveSemanticLinkElements(semanticLink);
                if (extractedSemanticLinkFromBlindata != null) {
                    enrichPhysicalFieldAndEntityWithExtractedSemanticLink(physicalEntity.getPhysicalFields(), pfName, extractedSemanticLinkFromBlindata, physicalEntity);
                }
            }
        }
    }

    @Override
    public void linkPhysicalEntityToDataCategory(Map<String, Object> sContext, BDPhysicalEntityRes physicalEntity) {
        if (sContext != null) {
            String defaultNamespaceIdentifier = (String) sContext.get("s-base");
            String rootCategoryName = (String) sContext.get("s-type");
            final BDDataCategoryRes dataCategoryRes = client.getDataCategoryByNameAndNamespace(rootCategoryName.replaceAll("[\\[\\]]", ""), defaultNamespaceIdentifier).get();
            Set<BDDataCategoryRes> dataCategoryResSet = new HashSet<>();
            dataCategoryResSet.add(dataCategoryRes);
            physicalEntity.setDataCategories(dataCategoryResSet);
        }
    }

    private void enrichPhysicalFieldAndEntityWithExtractedSemanticLink(Set<BDPhysicalFieldRes> physicalFieldResList, String pfName, LogicalFieldSemanticLinkRes semanticLinkRes, BDPhysicalEntityRes physicalEntity) {
        BDPhysicalFieldRes correspondingPhysicalField = physicalFieldResList.stream()
                .filter(field -> pfName.equals(field.getName()))
                .findFirst()
                .orElse(null);
        if (correspondingPhysicalField != null) {
            List<LogicalFieldSemanticLinkRes> logicalFieldSemanticLinkResList = new ArrayList<>();
            logicalFieldSemanticLinkResList.add(semanticLinkRes);
            correspondingPhysicalField.setLogicalFields(logicalFieldSemanticLinkResList);
        }
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

    public LogicalFieldSemanticLinkRes resolveSemanticLinkElements(BDSemanticLink semanticLink) {
        try {
            return client.getSemanticLinkElements(
                    semanticLink.getSemanticLinkString(),
                    semanticLink.getDefaultNamespaceIdentifier()
            );
        } catch (BlindataClientException e) {
            logger.error("Error in SemanticLinkManager: BlindataClientException occurred. API Message: {}", e.getMessage());
            return null;
        }
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
}
