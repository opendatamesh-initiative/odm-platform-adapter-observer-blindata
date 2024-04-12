package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode
public class BDPolicyEvaluationRecord {

    private String policyName;

    private String implementationName;

    private String suiteName;

    private String resolverKey;

    private String resolverValue;

    private BDResourceType resourceType;

    private BDPolicyEvaluationResult evaluationResult;

    private Date evaluationDate;

    private String description;

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

    public String getSuiteName() {
        return suiteName;
    }

    public void setSuiteName(String suiteName) {
        this.suiteName = suiteName;
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

    public BDResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(BDResourceType resourceType) {
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

