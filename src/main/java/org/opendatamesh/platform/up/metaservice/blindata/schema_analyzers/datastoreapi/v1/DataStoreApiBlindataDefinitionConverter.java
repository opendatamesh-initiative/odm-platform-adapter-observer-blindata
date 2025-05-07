package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.dpds.datastoreapi.v1.extensions.DataStoreApiStandardDefinitionConverter;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1.model.DataStoreApiBlindataDefinition;

public class DataStoreApiBlindataDefinitionConverter implements DataStoreApiStandardDefinitionConverter<DataStoreApiBlindataDefinition> {
    private static final String SPECIFICATION = "json-schema";
    private static final String VERSION = "^1(\\.\\d+){0,2}$";

    @Override
    public boolean supports(String specification, String specificationVersion) {
        return SPECIFICATION.equalsIgnoreCase(specification) && specificationVersion.matches(VERSION);
    }

    @Override
    public DataStoreApiBlindataDefinition deserialize(ObjectMapper defaultObjectMapper, JsonNode jsonNode) throws JacksonException {
        return defaultObjectMapper.treeToValue(jsonNode, DataStoreApiBlindataDefinition.class);
    }

    @Override
    public JsonNode serialize(ObjectMapper defaultObjectMapper, DataStoreApiBlindataDefinition dataStoreApiBlindataDefinition) throws JacksonException {
        return defaultObjectMapper.valueToTree(dataStoreApiBlindataDefinition);
    }
}
