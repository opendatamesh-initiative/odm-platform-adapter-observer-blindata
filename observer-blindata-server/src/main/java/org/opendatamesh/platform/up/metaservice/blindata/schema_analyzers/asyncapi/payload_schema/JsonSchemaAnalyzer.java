package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.payload_schema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.AdditionalPropertiesRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDPhysicalFieldRes;

import java.util.*;

@Slf4j
class JsonSchemaAnalyzer implements AsyncApiPayloadSchemaAnalyzer {
    @Override
    public List<BDPhysicalFieldRes> payloadSchemaToBlindataPhysicalFields(String rawSchema, String rootName) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<BDPhysicalFieldRes> bdPhysicalFields = new ArrayList<>();
        try {
            int ordinalPosition = 1;
            Set<JsonProperty> visitedProperties = new HashSet<>();
            JsonProperty root = objectMapper.readValue(rawSchema, JsonProperty.class);
            List<JsonProperty> propertiesToVisit = Lists.newArrayList(root);
            Map<JsonProperty, String> propertiesNames = new HashMap<>();
            propertiesNames.put(root, rootName);
           do {
               JsonProperty parentProperty = propertiesToVisit.remove(0);
               visitedProperties.add(parentProperty);
               for (Map.Entry<String, JsonProperty> entry : parentProperty.getProperties().entrySet()) {
                   String childPropertyName = entry.getKey();
                   JsonProperty childProperty = entry.getValue();
                   if (visitedProperties.contains(childProperty)) {
                       continue;
                   }
                   propertiesToVisit.add(childProperty);
                   BDPhysicalFieldRes bdPhysicalFieldRes = new BDPhysicalFieldRes();
                   bdPhysicalFieldRes.setName(propertiesNames.get(parentProperty) + "." + childPropertyName);
                   propertiesNames.putIfAbsent(childProperty, bdPhysicalFieldRes.getName());
                   bdPhysicalFieldRes.setType(childProperty.getJavaType() != null ? childProperty.getJavaType() : childProperty.getType());
                   bdPhysicalFieldRes.setDescription(childProperty.getDescription());
                   bdPhysicalFieldRes.setOrdinalPosition(ordinalPosition);
                   ordinalPosition++;

                   List<AdditionalPropertiesRes> additionalPropertiesRes = new ArrayList<>();
                   for (Map.Entry<String, String > additionalAttribute: childProperty.getAdditionalAttributes().entrySet()){
                       additionalPropertiesRes.add(new AdditionalPropertiesRes(additionalAttribute.getKey(), additionalAttribute.getValue()));
                   }
                   if(parentProperty.getRequired().contains(childPropertyName)){
                       additionalPropertiesRes.add(new AdditionalPropertiesRes("REQUIRED", "true"));
                   }
                   bdPhysicalFieldRes.setAdditionalProperties(additionalPropertiesRes);
                   bdPhysicalFields.add(bdPhysicalFieldRes);
               }
           } while (!propertiesToVisit.isEmpty());
            return bdPhysicalFields;
        } catch (JsonProcessingException e) {
            log.warn(e.getMessage(), e);
            return bdPhysicalFields;
        }
    }
}
