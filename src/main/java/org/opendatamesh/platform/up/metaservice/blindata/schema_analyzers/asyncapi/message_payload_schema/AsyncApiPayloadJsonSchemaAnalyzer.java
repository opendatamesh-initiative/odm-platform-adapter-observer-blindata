package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.message_payload_schema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDAdditionalPropertiesRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalFieldRes;

import java.util.*;

import static org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;

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
                    List<BDAdditionalPropertiesRes> additionalPropertiesRes = buildBlindataPhysicalFieldAdditionalProperties(
                            childProperty, parentProperty, childPropertyName
                    );
                    bdPhysicalField.setAdditionalProperties(additionalPropertiesRes);
                    bdPhysicalFields.add(bdPhysicalField);
                }
            } while (!propertiesToVisit.isEmpty());
            return bdPhysicalFields;
        } catch (JsonProcessingException e) {
            getUseCaseLogger().warn(e.getMessage(), e);
            return bdPhysicalFields;
        }
    }

    private List<BDAdditionalPropertiesRes> buildBlindataPhysicalFieldAdditionalProperties(JsonProperty childProperty, JsonProperty parentProperty, String childPropertyName) {
        List<BDAdditionalPropertiesRes> additionalPropertiesRes = new ArrayList<>();
        for (Map.Entry<String, JsonNode> additionalAttribute : childProperty.getAdditionalAttributes().entrySet()) {
            addAdditionalPropertyValue(additionalPropertiesRes, additionalAttribute.getKey(), additionalAttribute.getValue());
        }
        if (parentProperty.getRequired().contains(childPropertyName)) {
            additionalPropertiesRes.add(new BDAdditionalPropertiesRes("REQUIRED", "true"));
        }
        return additionalPropertiesRes;
    }

    private void addAdditionalPropertyValue(List<BDAdditionalPropertiesRes> additionalProperties, String propName, JsonNode value) {
        if (value.isArray()) {
            // Create a separate additional property for each array element
            value.forEach(element -> {
                String elementValue = element.isTextual() ? element.asText() : element.toString();
                additionalProperties.add(new BDAdditionalPropertiesRes(propName, elementValue));
            });
        } else {
            // Handle single values as before
            additionalProperties.add(new BDAdditionalPropertiesRes(
                    propName,
                    value.isTextual() ? value.asText() : value.toString()
            ));
        }
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
