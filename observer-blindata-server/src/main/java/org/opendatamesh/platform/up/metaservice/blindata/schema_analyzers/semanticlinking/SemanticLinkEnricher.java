package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDPhysicalFieldRes;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1.BDSemanticLink;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class SemanticLinkEnricher {

    public Map<String, BDSemanticLink> enrichWithSemanticLinks(Set<BDPhysicalFieldRes> physicalFieldResList, Map<String, Object> sContext) {
        Map<String, BDSemanticLink> semanticLinkMapByPhysicalFieldName = new TreeMap<>();
        String defaultNamespaceIdentifier = (String) sContext.get("s-base");
        String rootCategoryName = (String) sContext.get("s-type");

        // Ricorsione per estrarre semantic links
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
            // Caso base: campo semplice
            String fieldPath = parentPath.isEmpty() ? (String) fieldContext : parentPath + "." + fieldContext;
            BDSemanticLink semanticLink = createSemanticLink(namespace, rootCategory, fieldPath);
            semanticLinkMap.put(fieldName, semanticLink);
        } else if (fieldContext instanceof Map) {
            // Caso ricorsivo: mappa annidata
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
