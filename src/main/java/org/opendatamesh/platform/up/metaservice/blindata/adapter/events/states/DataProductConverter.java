package org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.dpds.model.DataProductVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class DataProductConverter {
    private static final Logger log = LoggerFactory.getLogger(DataProductConverter.class);
    private static final ObjectMapper mapper = new ObjectMapper();

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

            // First, recurse into all children so that deepest rawContent gets processed first.
            Iterator<JsonNode> elements = objectNode.elements();
            while (elements.hasNext()) {
                flattenJsonNode(elements.next());
            }

            // Then, handle rawContent for the current node.
            if (objectNode.has("rawContent") && !objectNode.get("rawContent").isNull()) {
                JsonNode rawContentNode = objectNode.get("rawContent");
                if (rawContentNode.isTextual()) {
                    String rawContentStr = rawContentNode.asText().trim();
                    if (!rawContentStr.isEmpty()) {
                        JsonNode parsedRawContent = mapper.readTree(rawContentStr);
                        // Remove rawContent field from current node.
                        objectNode.remove("rawContent");
                        if (parsedRawContent.isObject()) {
                            // Merge parsedRawContent into the current node using deepMerge.
                            deepMerge(objectNode, (ObjectNode) parsedRawContent);
                        }
                    }
                }
            }
        } else if (node.isArray()) {
            // For arrays, process each element.
            for (JsonNode element : node) {
                flattenJsonNode(element);
            }
        }
        // Other node types remain unchanged.
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
