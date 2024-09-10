package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.message_payload_schema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.AdditionalPropertiesRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDPhysicalFieldRes;

import java.util.*;

@Slf4j
class AsyncApiPayloadJsonSchemaAnalyzer implements AsyncApiPayloadSchemaAnalyzer {
    private final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public List<BDPhysicalFieldRes> payloadSchemaToBlindataPhysicalFields(String rawSchema, String rootName) {
        List<BDPhysicalFieldRes> bdPhysicalFields = new ArrayList<>();
        int ordinalPosition = 1;
        Set<JsonProperty> visitedProperties = new HashSet<>();
        try {
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

                    //Avoiding infinite loops
                    if (visitedProperties.contains(childProperty)) {
                        continue;
                    }
                    propertiesToVisit.add(childProperty);

                    String fullName = propertiesNames.get(parentProperty) + "." + childPropertyName;
                    BDPhysicalFieldRes bdPhysicalField = buildBlindataPhysicalField(
                            fullName,
                            childProperty,
                            ordinalPosition
                    );
                    propertiesNames.putIfAbsent(childProperty, fullName);
                    ordinalPosition++;
                    List<AdditionalPropertiesRes> additionalPropertiesRes = buildBlindataPhysicalFieldAdditionalProperties(
                            childProperty, parentProperty, childPropertyName
                    );
                    bdPhysicalField.setAdditionalProperties(additionalPropertiesRes);
                    bdPhysicalFields.add(bdPhysicalField);
                }
            } while (!propertiesToVisit.isEmpty());
            return bdPhysicalFields;
        } catch (JsonProcessingException e) {
            log.warn(e.getMessage(), e);
            return bdPhysicalFields;
        }
    }

    private List<AdditionalPropertiesRes> buildBlindataPhysicalFieldAdditionalProperties(JsonProperty childProperty, JsonProperty parentProperty, String childPropertyName) {
        List<AdditionalPropertiesRes> additionalPropertiesRes = new ArrayList<>();
        for (Map.Entry<String, JsonNode> additionalAttribute : childProperty.getAdditionalAttributes().entrySet()) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes(additionalAttribute.getKey(), additionalAttribute.getValue().toString()));
        }
        if (parentProperty.getRequired().contains(childPropertyName)) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("REQUIRED", "true"));
        }
        return additionalPropertiesRes;
    }

    private BDPhysicalFieldRes buildBlindataPhysicalField(String name, JsonProperty childProperty, int ordinalPosition) {
        String type = childProperty.getJavaType();
        type = type != null ? type : childProperty.getType().textValue();
        type = type != null ? type : childProperty.getType().toString();

        BDPhysicalFieldRes bdPhysicalFieldRes = new BDPhysicalFieldRes();
        bdPhysicalFieldRes.setName(name);
        bdPhysicalFieldRes.setType(type);
        bdPhysicalFieldRes.setDescription(childProperty.getDescription());
        bdPhysicalFieldRes.setOrdinalPosition(ordinalPosition);
        return bdPhysicalFieldRes;
    }
}
