package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.eventstates;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyResource;

public class OdmPolicyNotificationEventState {
    private OdmPolicyResource policy;

    public OdmPolicyNotificationEventState() {
    }

    public OdmPolicyResource getPolicy() {
        return policy;
    }

    public void setPolicy(OdmPolicyResource policy) {
        this.policy = policy;
    }

    public JsonNode toJsonNode() {
        return new ObjectMapper().valueToTree(this);
    }
}
