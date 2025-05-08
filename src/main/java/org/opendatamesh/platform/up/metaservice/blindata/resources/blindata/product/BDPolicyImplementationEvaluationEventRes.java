package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product;

public class BDPolicyImplementationEvaluationEventRes {

    private BDPolicyEvaluationEvent event;

    public BDPolicyImplementationEvaluationEventRes() {
    }

    public BDPolicyImplementationEvaluationEventRes(BDPolicyEvaluationEvent event) {
        this.event = event;
    }

    public BDPolicyEvaluationEvent getEvent() {
        return event;
    }

    public void setEvent(BDPolicyEvaluationEvent event) {
        this.event = event;
    }
}