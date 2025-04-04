package org.opendatamesh.platform.up.metaservice.blindata.validator.resources;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;

public class OdmValidatorPolicyEvaluationResultRes {
    @Schema(description = "Policy Evaluation ID to reconcile the evaluation result with the triggering request")
    private Long policyEvaluationId;

    @Schema(description = "Synthetic results stating if the document is valid or not against the provided policy")
    private Boolean evaluationResult;

    @Schema(description = "Extended result of the evaluation")
    private OutputObject outputObject;

    public Long getPolicyEvaluationId() {
        return policyEvaluationId;
    }

    public void setPolicyEvaluationId(Long policyEvaluationId) {
        this.policyEvaluationId = policyEvaluationId;
    }

    public Boolean getEvaluationResult() {
        return evaluationResult;
    }

    public void setEvaluationResult(Boolean evaluationResult) {
        this.evaluationResult = evaluationResult;
    }

    public OutputObject getOutputObject() {
        return outputObject;
    }

    public void setOutputObject(OutputObject outputObject) {
        this.outputObject = outputObject;
    }

    public static class OutputObject {
        private String message;
        private JsonNode rawError;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public JsonNode getRawError() {
            return rawError;
        }

        public void setRawError(JsonNode rawError) {
            this.rawError = rawError;
        }
    }

}
