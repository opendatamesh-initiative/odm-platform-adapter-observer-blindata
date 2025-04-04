package org.opendatamesh.platform.up.metaservice.blindata.validator.resources;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyResource;

public class OdmValidatorPolicyEvaluationRequestRes {
    @Schema(description = "Policy Evaluation ID to reconcile the evaluation result with the triggering request")
    private Long policyEvaluationId;

    @Schema(description = "JSON representation of the policy to evaluate against")
    private OdmPolicyResource policy;

    @Schema(description = "JSON representation of the object to be evaluated")
    private JsonNode objectToEvaluate;

    public OdmValidatorPolicyEvaluationRequestRes() {
    }

    public Long getPolicyEvaluationId() {
        return policyEvaluationId;
    }

    public void setPolicyEvaluationId(Long policyEvaluationId) {
        this.policyEvaluationId = policyEvaluationId;
    }

    public OdmPolicyResource getPolicy() {
        return policy;
    }

    public void setPolicy(OdmPolicyResource policy) {
        this.policy = policy;
    }

    public JsonNode getObjectToEvaluate() {
        return objectToEvaluate;
    }

    public void setObjectToEvaluate(JsonNode objectToEvaluate) {
        this.objectToEvaluate = objectToEvaluate;
    }
}
