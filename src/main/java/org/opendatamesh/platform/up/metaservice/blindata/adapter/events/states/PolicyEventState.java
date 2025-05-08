package org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyResource;

public class PolicyEventState implements EventState {
    private OdmPolicyResource policy;

    public PolicyEventState() {
        //DO NOTHING
    }

    public OdmPolicyResource getPolicy() {
        return policy;
    }

    public void setPolicy(OdmPolicyResource policy) {
        this.policy = policy;
    }
}
