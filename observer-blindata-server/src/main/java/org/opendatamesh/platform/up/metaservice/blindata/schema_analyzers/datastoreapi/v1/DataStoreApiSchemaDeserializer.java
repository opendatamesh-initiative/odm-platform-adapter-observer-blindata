package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

class DataStoreApiSchemaDeserializer extends JsonDeserializer<DataStoreApiSchema> {
    @Override
    public DataStoreApiSchema deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException{
        TreeNode node = jp.readValueAsTree();
        if (node.get("content") != null && node.get("content").get("entities") != null) {
            return jp.getCodec().treeToValue(node, DataStoreApiSchemaMultipleEntity.class);
        } else {
            return jp.getCodec().treeToValue(node, DataStoreApiSchemaEntity.class);
        }
    }
}
