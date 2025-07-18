package org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.marketplace.OdmExecutorResultReceivedEventAccessRequest;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.marketplace.OdmExecutorResultReceivedEventExecutorResponse;

public class MarketplaceAccessRequestEventState implements EventState {
    private OdmExecutorResultReceivedEventAccessRequest accessRequest;
    private OdmExecutorResultReceivedEventExecutorResponse executorResponse;

    public MarketplaceAccessRequestEventState() {
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
}
