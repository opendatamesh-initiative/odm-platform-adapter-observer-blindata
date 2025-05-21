package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product;

import java.util.ArrayList;
import java.util.List;

public class BDPolicyImplementationRes {

    private String uuid;
    private String name;
    private String displayName;
    private String description;
    private BDPolicyEvaluationEvent evaluationEvent;
    private List<BDPolicyImplementationEvaluationEventRes> evaluationEvents = new ArrayList<>();
    private BDPolicyRes governancePolicy;
    private String evaluationCondition;
    private boolean blocking;
    private boolean deployed;
    private String policyBody;
    private String policyEngineName;
    private String version;

    public BDPolicyImplementationRes() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public BDPolicyEvaluationEvent getEvaluationEvent() {
        return evaluationEvent;
    }

    public void setEvaluationEvent(BDPolicyEvaluationEvent evaluationEvent) {
        this.evaluationEvent = evaluationEvent;
    }

    public List<BDPolicyImplementationEvaluationEventRes> getEvaluationEvents() {
        return evaluationEvents;
    }

    public void setEvaluationEvents(List<BDPolicyImplementationEvaluationEventRes> evaluationEvents) {
        this.evaluationEvents = evaluationEvents;
    }

    public BDPolicyRes getGovernancePolicy() {
        return governancePolicy;
    }

    public void setGovernancePolicy(BDPolicyRes governancePolicy) {
        this.governancePolicy = governancePolicy;
    }

    public String getEvaluationCondition() {
        return evaluationCondition;
    }

    public void setEvaluationCondition(String evaluationCondition) {
        this.evaluationCondition = evaluationCondition;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    public String getPolicyBody() {
        return policyBody;
    }

    public void setPolicyBody(String policyBody) {
        this.policyBody = policyBody;
    }

    public String getPolicyEngineName() {
        return policyEngineName;
    }

    public void setPolicyEngineName(String policyEngineName) {
        this.policyEngineName = policyEngineName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isDeployed() {
        return deployed;
    }

    public void setDeployed(boolean deployed) {
        this.deployed = deployed;
    }
}
