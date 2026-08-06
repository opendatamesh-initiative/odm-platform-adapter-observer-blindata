package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.probes_upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDAdditionalPropertiesRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalEntityShortRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalFieldRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ContractRuleEnvelopeBuilder {

    public static final String SCHEMA = "blindata.qualityProbe.contractRule.v1";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ContractRuleEnvelopeBuilder() {
    }

    public static Map<String, Object> buildQueryBody(QualityCheck qualityCheck) {
        PhysicalBinding physicalBinding = buildPhysicalBinding(qualityCheck);
        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("schema", SCHEMA);
        envelope.put("rule", buildRule(qualityCheck));
        envelope.put("physicalBinding", physicalBindingToMap(physicalBinding));
        return envelope;
    }

    public static Map<String, Object> buildQueryBody(Map<String, Object> contractRule, PhysicalBinding physicalBinding) {
        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("schema", SCHEMA);
        envelope.put("rule", contractRule);
        envelope.put("physicalBinding", physicalBindingToMap(physicalBinding));
        return envelope;
    }

    public static Map<String, Object> buildRule(QualityCheck qualityCheck) {
        Map<String, Object> rule = new LinkedHashMap<>();
        String ruleType = getAdditionalProperty(qualityCheck, "_contract.ruleType");
        if (StringUtils.hasText(ruleType)) {
            rule.put("type", ruleType);
        }
        putIfText(rule, "metric", getAdditionalProperty(qualityCheck, "_contract.metric"));
        putIfText(rule, "query", getAdditionalProperty(qualityCheck, "_contract.query"));
        putIfText(rule, "unit", getAdditionalProperty(qualityCheck, "_contract.unit"));

        String arguments = getAdditionalProperty(qualityCheck, "_contract.arguments");
        if (StringUtils.hasText(arguments)) {
            rule.put("arguments", parseJsonValue(arguments));
        }

        String operator = getAdditionalProperty(qualityCheck, "_contract.operator");
        String bounds = getAdditionalProperty(qualityCheck, "_contract.bounds");
        if (StringUtils.hasText(operator)) {
            rule.put(operator, parseBounds(bounds));
        }
        return rule;
    }

    /**
     * Expects a quality check as declared in the schema, carrying a single physical entity or field. Checks merged by
     * {@code refName} can hold objects the rule does not run on.
     */
    public static PhysicalBinding buildPhysicalBinding(QualityCheck qualityCheck) {
        if (!CollectionUtils.isEmpty(qualityCheck.getPhysicalFields())) {
            BDPhysicalFieldRes field = qualityCheck.getPhysicalFields().get(0);
            PhysicalBinding binding = new PhysicalBinding();
            BDPhysicalEntityShortRes entity = field.getPhysicalEntity();
            if (entity != null) {
                binding.setSchema(entity.getSchema());
                binding.setObject(entity.getName());
            }
            binding.setProperty(field.getName());
            return binding;
        }
        if (!CollectionUtils.isEmpty(qualityCheck.getPhysicalEntities())) {
            BDPhysicalEntityRes entity = qualityCheck.getPhysicalEntities().get(0);
            PhysicalBinding binding = new PhysicalBinding();
            binding.setSchema(entity.getSchema());
            binding.setObject(entity.getName());
            return binding;
        }
        return new PhysicalBinding();
    }

    private static Map<String, Object> physicalBindingToMap(PhysicalBinding physicalBinding) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (physicalBinding == null) {
            return map;
        }
        putIfText(map, "schema", physicalBinding.getSchema());
        putIfText(map, "object", physicalBinding.getObject());
        putIfText(map, "property", physicalBinding.getProperty());
        return map;
    }

    private static String getAdditionalProperty(QualityCheck qualityCheck, String key) {
        List<BDAdditionalPropertiesRes> additionalProperties = qualityCheck.getAdditionalProperties();
        if (CollectionUtils.isEmpty(additionalProperties)) {
            return null;
        }
        return additionalProperties.stream()
                .filter(prop -> key.equals(prop.getName()))
                .map(BDAdditionalPropertiesRes::getValue)
                .findFirst()
                .orElse(null);
    }

    private static Object parseBounds(String bounds) {
        if (!StringUtils.hasText(bounds)) {
            return bounds;
        }
        String trimmed = bounds.trim();
        if (trimmed.startsWith("[")) {
            try {
                JsonNode node = MAPPER.readTree(trimmed);
                if (node.isArray()) {
                    return MAPPER.convertValue(node, List.class);
                }
            } catch (JsonProcessingException ignored) {
                // fall through to raw string
            }
        }
        return bounds;
    }

    private static Object parseJsonValue(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }
        String trimmed = value.trim();
        if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
            try {
                return MAPPER.readValue(trimmed, Object.class);
            } catch (JsonProcessingException ignored) {
                // fall through to raw string
            }
        }
        return value;
    }

    private static void putIfText(Map<String, Object> target, String key, String value) {
        if (StringUtils.hasText(value)) {
            target.put(key, value);
        }
    }
}
