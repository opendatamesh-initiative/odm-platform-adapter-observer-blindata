package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.eventstates;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.marketplace.OdmExecutorResultReceivedEventAccessRequest;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.marketplace.OdmExecutorResultReceivedEventExecutorResponse;

public class OdmExecutorResultReceivedEventState {
    private OdmExecutorResultReceivedEventAccessRequest accessRequest;
    private OdmExecutorResultReceivedEventExecutorResponse executorResponse;

    public OdmExecutorResultReceivedEventState() {
    }

    public OdmExecutorResultReceivedEventAccessRequest getAccessRequest() {
        return accessRequest;
    }

    public void setAccessRequest(OdmExecutorResultReceivedEventAccessRequest accessRequest) {
        this.accessRequest = accessRequest;
    }

    public OdmExecutorResultReceivedEventExecutorResponse getExecutorResponse() {
        return executorResponse;
    }

    public void setExecutorResponse(OdmExecutorResultReceivedEventExecutorResponse executorResponse) {
        this.executorResponse = executorResponse;
    }

    public JsonNode toJsonNode() {
        return new ObjectMapper().valueToTree(this);
    }

}
