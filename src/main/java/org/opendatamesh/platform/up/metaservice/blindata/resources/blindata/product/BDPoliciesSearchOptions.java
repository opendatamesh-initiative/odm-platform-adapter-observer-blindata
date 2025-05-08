package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDResourceType;

import java.util.List;

public class BDPoliciesSearchOptions {

    private List<String> policySuiteUuids;
    private String name;
    private String search;
    private BDPolicySuiteState state;
    private String type;
    private BDPolicyImplementationType implementationType;
    private BDResourceType adoptionTargetType;

    public BDPoliciesSearchOptions() {
    }

    public List<String> getPolicySuiteUuids() {
        return policySuiteUuids;
    }

    public void setPolicySuiteUuids(List<String> policySuiteUuids) {
        this.policySuiteUuids = policySuiteUuids;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public BDPolicySuiteState getState() {
        return state;
    }

    public void setState(BDPolicySuiteState state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
