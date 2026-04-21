package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1.model.quality;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDAdditionalPropertiesRes;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Maps ODCS / descriptor quality fields to {@code _contract.*} additional property keys (odcs31 mapping).
 * <p>
 * In ODCS 3.1, comparison semantics are expressed with keys such as {@code mustBe}, {@code mustBeBetween},
 * {@code mustBeGreaterThan}, etc. — not a separate {@code operator} field. The UI-facing
 * {@code _contract.operator} value is derived from whichever of those keys is present; see
 * <a href="https://bitol-io.github.io/open-data-contract-standard/v3.1.0/data-quality/">ODCS Data Quality</a>.
 */
public final class ContractAdditionalPropertiesBuilder {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ContractAdditionalPropertiesBuilder() {
    }

    public static void appendContractProperties(Quality quality, List<BDAdditionalPropertiesRes> target) {
        inferOdcsOperator(quality).ifPresent(inferred -> {
            putIfText(target, "_contract.operator", inferred.getOperatorId());
            putIfText(target, "_contract.bounds", inferred.getBoundsJson());
        });
        putIfText(target, "_contract.metric", quality.getMetric());
        putIfText(target, "_contract.unit", quality.getUnit());
        putIfText(target, "_contract.ruleType", quality.getType());
        putIfText(target, "_contract.query", quality.getQuery());
        putIfText(target, "_contract.engine", quality.getEngine());
        if (quality.getArguments() != null) {
            target.add(new BDAdditionalPropertiesRes("_contract.arguments", jsonNodeToStoredString(quality.getArguments())));
        }
        if (quality.getImplementation() != null) {
            target.add(new BDAdditionalPropertiesRes("_contract.implementation", jsonNodeToStoredString(quality.getImplementation())));
        }
    }

    /**
     * Derives {@code _contract.operator} and {@code _contract.bounds} from ODCS constraint keys on the rule.
     */
    static Optional<InferredOdcsOperator> inferOdcsOperator(Quality q) {
        if (nonEmptyArray(q.getMustBeBetween())) {
            return Optional.of(new InferredOdcsOperator("mustBeBetween", jsonNodeToStoredString(q.getMustBeBetween())));
        }
        if (nonEmptyArray(q.getMustNotBeBetween())) {
            return Optional.of(new InferredOdcsOperator("mustNotBeBetween", jsonNodeToStoredString(q.getMustNotBeBetween())));
        }
        if (q.getMustBe() != null) {
            return Optional.of(new InferredOdcsOperator("mustBe", String.valueOf(q.getMustBe())));
        }
        if (q.getMustNotBe() != null) {
            return Optional.of(new InferredOdcsOperator("mustNotBe", String.valueOf(q.getMustNotBe())));
        }
        if (q.getMustBeGreaterThan() != null) {
            return Optional.of(new InferredOdcsOperator("mustBeGreaterThan", String.valueOf(q.getMustBeGreaterThan())));
        }
        if (q.getMustBeLessThan() != null) {
            return Optional.of(new InferredOdcsOperator("mustBeLessThan", String.valueOf(q.getMustBeLessThan())));
        }
        if (q.getMustBeGreaterOrEqualTo() != null && q.getMustBeLessOrEqualTo() != null) {
            return Optional.of(new InferredOdcsOperator("mustBeBetween", boundsArrayJson(q.getMustBeGreaterOrEqualTo(), q.getMustBeLessOrEqualTo())));
        }
        if (q.getMustBeGreaterOrEqualTo() != null) {
            return Optional.of(new InferredOdcsOperator("mustBeGreaterOrEqualTo", String.valueOf(q.getMustBeGreaterOrEqualTo())));
        }
        if (q.getMustBeLessOrEqualTo() != null) {
            return Optional.of(new InferredOdcsOperator("mustBeLessOrEqualTo", String.valueOf(q.getMustBeLessOrEqualTo())));
        }
        return Optional.empty();
    }

    /**
     * Pair of derived UI operator id (ODCS key name) and serialized bounds payload.
     */
    static final class InferredOdcsOperator {
        private final String operatorId;
        private final String boundsJson;

        InferredOdcsOperator(String operatorId, String boundsJson) {
            this.operatorId = operatorId;
            this.boundsJson = boundsJson;
        }

        String getOperatorId() {
            return operatorId;
        }

        String getBoundsJson() {
            return boundsJson;
        }
    }

    private static String boundsArrayJson(float a, float b) {
        try {
            return MAPPER.writeValueAsString(Arrays.asList(a, b));
        } catch (JsonProcessingException e) {
            return "[" + a + "," + b + "]";
        }
    }

    private static boolean nonEmptyArray(JsonNode node) {
        return node != null && node.isArray() && node.size() > 0;
    }

    private static void putIfText(List<BDAdditionalPropertiesRes> target, String key, String value) {
        if (StringUtils.hasText(value)) {
            target.add(new BDAdditionalPropertiesRes(key, value));
        }
    }

    private static String jsonNodeToStoredString(JsonNode node) {
        if (node == null || node.isNull()) {
            return "";
        }
        if (node.isTextual()) {
            return node.asText();
        }
        return node.toString();
    }
}
