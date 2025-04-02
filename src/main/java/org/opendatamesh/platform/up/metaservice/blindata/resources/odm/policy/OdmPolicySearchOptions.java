package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy;

public class OdmPolicySearchOptions {

    private String evaluationEvent;
    private String policyEngineName;
    private String name;
    private Boolean lastVersion = true;

    public OdmPolicySearchOptions() {
    }

    public String getEvaluationEvent() {
        return evaluationEvent;
    }

    public String getPolicyEngineName() {
        return policyEngineName;
    }

    public void setPolicyEngineName(String policyEngineName) {
        this.policyEngineName = policyEngineName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(Boolean lastVersion) {
        this.lastVersion = lastVersion;
    }

    public void setEvaluationEvent(String evaluationEvent) {
        this.evaluationEvent = evaluationEvent;
    }

}
