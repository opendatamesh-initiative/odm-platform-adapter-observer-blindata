package org.opendatamesh.platform.up.metaservice.blindata.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.dpds.model.DataProductVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * Utility class for converting the legacy {@code DataProductDescriptorDPDS} model
 * into the updated {@code DataProductDescriptor} model.
 * <p>
 * Key conversion logics include:
 * <ul>
 *   <li><strong>rawContent field:</strong> Properties from this field are migrated to
 *       the {@code additionalProperties} field in the new model by default.</li>
 *   <li><strong>LifecycleInfo:</strong> The legacy structure is incompatible with the
 *       Open Data Mesh specification and requires adaptation.</li>
 * </ul>
 */

public abstract class DataProductConverter {
    private static final Logger log = LoggerFactory.getLogger(DataProductConverter.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Converts a legacy serialized DataProductVersion JSON string into a new {@link DataProductVersion} object.
     * <p>
     * This method flattens embedded {@code rawContent} structures and refactors the {@code lifecycleInfo}
     * to conform to the Open Data Mesh specification.
     *
     * @param oldVersion The JSON string representing the legacy DataProductVersion object.
     * @return A {@link DataProductVersion} instance compatible with the updated model.
     * @throws RuntimeException if JSON parsing or mapping fails.
     */
    public static DataProductVersion oldToNewVersion(String oldVersion) {
        try {
            JsonNode raw = mapper.readTree(oldVersion);
            flattenJsonNode(raw);
            convertLifecycleInfo(raw);
            return mapper.treeToValue(raw, DataProductVersion.class);
        } catch (JsonProcessingException e) {
            log.warn("Error when converting old DataProductVersion object to new one: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    private static void flattenJsonNode(JsonNode node) throws JsonProcessingException {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;

            // 1) Handle rawContent first (so merged content will be visited)
            if (objectNode.has("rawContent") && !objectNode.get("rawContent").isNull()) {
                JsonNode rawContentNode = objectNode.get("rawContent");

                // If it's a textual JSON, parse it; if it's already an object, use it directly.
                JsonNode parsedRawContent = null;
                if (rawContentNode.isTextual()) {
                    String rawContentStr = rawContentNode.asText().trim();
                    if (!rawContentStr.isEmpty()) {
                        parsedRawContent = mapper.readTree(rawContentStr);
                    }
                } else if (rawContentNode.isObject() || rawContentNode.isArray()) {
                    parsedRawContent = rawContentNode;
                }

                if (parsedRawContent != null) {
                    // remove the original rawContent string/object from current node
                    objectNode.remove("rawContent");

                    // if parsed content is an object, merge it into the current node
                    if (parsedRawContent.isObject()) {
                        deepMerge(objectNode, (ObjectNode) parsedRawContent);
                    } else {
                        // If parsedRawContent is an array or primitive, preserve it under the same key
                        objectNode.set("rawContent", parsedRawContent);
                    }
                }
            }

            // 2) Recurse into children using a snapshot of values to avoid modification issues
            List<JsonNode> children = new ArrayList<>();
            objectNode.fields().forEachRemaining(entry -> children.add(entry.getValue()));
            for (JsonNode child : children) {
                flattenJsonNode(child);
            }

        } else if (node.isArray()) {
            for (JsonNode element : node) {
                flattenJsonNode(element);
            }
        }
        // other node types stay as they are
    }


    /**
     * Recursively merges all fields from the source node into the target node.
     * If both target and source have an object for the same key, merge them recursively.
     * Otherwise, the source value will override the target value.
     */
    private static void deepMerge(ObjectNode target, ObjectNode source) {
        Iterator<Map.Entry<String, JsonNode>> fields = source.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String fieldName = entry.getKey();
            JsonNode sourceValue = entry.getValue();
            if (target.has(fieldName)) {
                JsonNode targetValue = target.get(fieldName);
                // If both are objects, merge recursively.
                if (targetValue.isObject() && sourceValue.isObject()) {
                    deepMerge((ObjectNode) targetValue, (ObjectNode) sourceValue);
                } else {
                    // Otherwise, the source value takes precedence.
                    target.set(fieldName, sourceValue);
                }
            } else {
                target.set(fieldName, sourceValue);
            }
        }
    }


    private static void convertLifecycleInfo(JsonNode rootNode) {
        // Navigate to the lifecycleInfo node inside internalComponents
        JsonNode internalComponents = rootNode.path("internalComponents");
        JsonNode lifecycleInfo = internalComponents.path("lifecycleInfo");
        JsonNode tasksInfo = lifecycleInfo.path("tasksInfo");

        // Exit early if tasksInfo is not an array.
        if (!tasksInfo.isArray()) {
            return;
        }

        // Use LinkedHashMap to preserve the insertion order of stage names.
        Map<String, ArrayNode> stageToTasksMap = new LinkedHashMap<>();

        // Iterate over the tasks and group them by stageName.
        for (JsonNode task : tasksInfo) {
            String stageName = task.path("stageName").asText(null);
            if (stageName == null) {
                // Skip tasks that do not have a valid stageName.
                continue;
            }
            stageToTasksMap.computeIfAbsent(stageName, key -> mapper.createArrayNode()).add(task);
        }

        // Update the lifecycleInfo node: remove tasksInfo and stageNames,
        // then insert the grouped tasks while maintaining the key order.
        if (lifecycleInfo instanceof ObjectNode) {
            ObjectNode lifecycleObj = (ObjectNode) lifecycleInfo;
            lifecycleObj.remove("tasksInfo");
            lifecycleObj.remove("stageNames");

            // Add each stage key and its tasks in the order they were first encountered.
            stageToTasksMap.forEach(lifecycleObj::set);
        }
    }

}
