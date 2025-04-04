package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

class DataStoreApiSchemaDeserializer extends JsonDeserializer<DataStoreApiSchema> {
    @Override
    public DataStoreApiSchema deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
        TreeNode node = jp.readValueAsTree();
        return jp.getCodec().treeToValue(node, DataStoreApiSchemaResource.class);
    }

}
