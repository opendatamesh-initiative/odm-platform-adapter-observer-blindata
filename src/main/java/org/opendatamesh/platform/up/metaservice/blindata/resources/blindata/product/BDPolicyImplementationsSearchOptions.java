package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product;

public class BDPolicyImplementationsSearchOptions {

    private String policyUuid;
    private String implementationName;

    public BDPolicyImplementationsSearchOptions() {
    }

    public String getPolicyUuid() {
        return policyUuid;
    }

    public void setPolicyUuid(String policyUuid) {
        this.policyUuid = policyUuid;
    }

    public String getImplementationName() {
        return implementationName;
    }

    public void setImplementationName(String implementationName) {
        this.implementationName = implementationName;
    }
}
