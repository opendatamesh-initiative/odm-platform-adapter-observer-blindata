package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata;

import java.util.Date;

public class BDPolicyEvaluationRecord {

    private String policyName;

    private String implementationName;

    private String resolverKey;

    private String resolverValue;

    private String resourceType;

    private BDPolicyEvaluationResult evaluationResult;

    private Date evaluationDate;

    private String description;

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof BDPolicyEvaluationRecord)) return false;
        final BDPolicyEvaluationRecord other = (BDPolicyEvaluationRecord) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$policyName = this.getPolicyName();
        final Object other$policyName = other.getPolicyName();
        if (this$policyName == null ? other$policyName != null : !this$policyName.equals(other$policyName))
            return false;
        final Object this$implementationName = this.getImplementationName();
        final Object other$implementationName = other.getImplementationName();
        if (this$implementationName == null ? other$implementationName != null : !this$implementationName.equals(other$implementationName))
            return false;
        final Object this$resolverKey = this.getResolverKey();
        final Object other$resolverKey = other.getResolverKey();
        if (this$resolverKey == null ? other$resolverKey != null : !this$resolverKey.equals(other$resolverKey))
            return false;
        final Object this$resolverValue = this.getResolverValue();
        final Object other$resolverValue = other.getResolverValue();
        if (this$resolverValue == null ? other$resolverValue != null : !this$resolverValue.equals(other$resolverValue))
            return false;
        final Object this$resourceType = this.getResourceType();
        final Object other$resourceType = other.getResourceType();
        if (this$resourceType == null ? other$resourceType != null : !this$resourceType.equals(other$resourceType))
            return false;
        final Object this$evaluationResult = this.getEvaluationResult();
        final Object other$evaluationResult = other.getEvaluationResult();
        if (this$evaluationResult == null ? other$evaluationResult != null : !this$evaluationResult.equals(other$evaluationResult))
            return false;
        final Object this$evaluationDate = this.getEvaluationDate();
        final Object other$evaluationDate = other.getEvaluationDate();
        if (this$evaluationDate == null ? other$evaluationDate != null : !this$evaluationDate.equals(other$evaluationDate))
            return false;
        final Object this$description = this.getDescription();
        final Object other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BDPolicyEvaluationRecord;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $policyName = this.getPolicyName();
        result = result * PRIME + ($policyName == null ? 43 : $policyName.hashCode());
        final Object $implementationName = this.getImplementationName();
        result = result * PRIME + ($implementationName == null ? 43 : $implementationName.hashCode());
        final Object $resolverKey = this.getResolverKey();
        result = result * PRIME + ($resolverKey == null ? 43 : $resolverKey.hashCode());
        final Object $resolverValue = this.getResolverValue();
        result = result * PRIME + ($resolverValue == null ? 43 : $resolverValue.hashCode());
        final Object $resourceType = this.getResourceType();
        result = result * PRIME + ($resourceType == null ? 43 : $resourceType.hashCode());
        final Object $evaluationResult = this.getEvaluationResult();
        result = result * PRIME + ($evaluationResult == null ? 43 : $evaluationResult.hashCode());
        final Object $evaluationDate = this.getEvaluationDate();
        result = result * PRIME + ($evaluationDate == null ? 43 : $evaluationDate.hashCode());
        final Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        return result;
    }

    public enum BDPolicyEvaluationResult {
        VERIFIED,
        FAILED
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getResolverKey() {
        return resolverKey;
    }

    public void setResolverKey(String resolverKey) {
        this.resolverKey = resolverKey;
    }

    public String getResolverValue() {
        return resolverValue;
    }

    public void setResolverValue(String resolverValue) {
        this.resolverValue = resolverValue;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public BDPolicyEvaluationResult getEvaluationResult() {
        return evaluationResult;
    }

    public void setEvaluationResult(BDPolicyEvaluationResult evaluationResult) {
        this.evaluationResult = evaluationResult;
    }

    public Date getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(Date evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImplementationName() {
        return implementationName;
    }

    public void setImplementationName(String implementationName) {
        this.implementationName = implementationName;
    }
}

