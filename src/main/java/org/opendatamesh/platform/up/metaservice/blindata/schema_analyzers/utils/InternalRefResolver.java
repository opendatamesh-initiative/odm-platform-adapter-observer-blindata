package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.utils;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class InternalRefResolver {
    private static final Logger log = LoggerFactory.getLogger(InternalRefResolver.class);

    public static void resolveRefs(JsonNode node, JsonNode rootNode) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            if (objectNode.has("$ref")) {
                // Resolve $ref
                String refPath = objectNode.get("$ref").asText();
                refPath = removeLeadingHashtag(refPath);
                refPath = convertDotNotationToSlash(refPath);
                JsonPointer pointer = JsonPointer.compile(refPath);

                JsonNode refValue = rootNode.at(pointer);
                if (!refValue.isObject()) {
                    log.warn("Impossible to resolve reference for {}", pointer);
                } else {
                    objectNode.setAll((ObjectNode) refValue);
                    objectNode.remove("$ref");
                }
            } else {
                // Recursive call for nested objects
                node.fields().forEachRemaining(entry -> resolveRefs(entry.getValue(), rootNode));
            }
        } else if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            for (int i = 0; i < arrayNode.size(); i++) {
                resolveRefs(arrayNode.get(i), rootNode);
            }
        }
    }

    private static String convertDotNotationToSlash(String refPath) {
        if (refPath.contains(".")) {
            return "/" + refPath.replace(".", "/");
        }
        return refPath;
    }

    private static String removeLeadingHashtag(String refPath) {
        if (refPath.startsWith("#")) {
            return refPath.substring(1); // Remove leading #
        }
        return refPath;
    }
}
