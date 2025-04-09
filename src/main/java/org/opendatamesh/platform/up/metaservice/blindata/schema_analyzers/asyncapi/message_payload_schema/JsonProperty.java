package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.message_payload_schema;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonProperty {
    private String javaType;
    private JsonNode type;
    private String description;
    private String format;
    private Map<String, JsonProperty> properties = new HashMap<>();
    private List<String> required = new ArrayList<>();
    private Boolean additionalProperties;
    private Map<String, JsonNode> additionalAttributes = new HashMap<>();

    @JsonAnySetter
    public void add(String property, JsonNode value) {
        this.additionalAttributes.putIfAbsent(property, value);
    }

    public JsonNode getType() {
        return type;
    }

    public void setType(JsonNode type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Map<String, JsonProperty> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, JsonProperty> properties) {
        this.properties = properties;
    }

    public List<String> getRequired() {
        return required;
    }

    public void setRequired(List<String> required) {
        this.required = required;
    }

    public Boolean getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Boolean additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public Map<String, JsonNode> getAdditionalAttributes() {
        return additionalAttributes;
    }

    public void setAdditionalAttributes(Map<String, JsonNode> additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }
}
