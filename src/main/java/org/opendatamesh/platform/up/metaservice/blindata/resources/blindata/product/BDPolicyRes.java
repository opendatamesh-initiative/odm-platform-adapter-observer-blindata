package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDAdditionalPropertiesRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDResourceType;

import java.util.List;

public class BDPolicyRes {
    private String uuid;
    private BDPolicySuiteRes governancePolicySuite;
    private String name;
    private String displayName;
    private String description;
    private String policyVersion;
    private BDPolicySuiteState policyStatus;
    private String policyType;
    private BDPolicyImplementationType implementationType;
    private BDResourceType adoptionTargetType;
    private List<BDAdditionalPropertiesRes> additionalProperties;

    public BDPolicyRes() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public BDPolicySuiteRes getGovernancePolicySuite() {
        return governancePolicySuite;
    }

    public void setGovernancePolicySuite(BDPolicySuiteRes governancePolicySuite) {
        this.governancePolicySuite = governancePolicySuite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPolicyVersion() {
        return policyVersion;
    }

    public void setPolicyVersion(String policyVersion) {
        this.policyVersion = policyVersion;
    }

    public BDPolicySuiteState getPolicyStatus() {
        return policyStatus;
    }

    public void setPolicyStatus(BDPolicySuiteState policyStatus) {
        this.policyStatus = policyStatus;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public BDPolicyImplementationType getImplementationType() {
        return implementationType;
    }

    public void setImplementationType(BDPolicyImplementationType implementationType) {
        this.implementationType = implementationType;
    }

    public BDResourceType getAdoptionTargetType() {
        return adoptionTargetType;
    }

    public void setAdoptionTargetType(BDResourceType adoptionTargetType) {
        this.adoptionTargetType = adoptionTargetType;
    }

    public List<BDAdditionalPropertiesRes> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(List<BDAdditionalPropertiesRes> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }
}
