package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.marketplace_portupdater;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.marketplace.OdmExecutorResultReceivedEventExecutorResponse;

public class MarketplaceAccessRequestsPortUpdaterOdmOutboundPortMock implements MarketplaceAccessRequestsPortUpdaterOdmOutboundPort {

    private final MarketplaceAccessRequestsPortUpdateInitialState initialState;

    public MarketplaceAccessRequestsPortUpdaterOdmOutboundPortMock(MarketplaceAccessRequestsPortUpdateInitialState initialState) {
        this.initialState = initialState;
    }

    @Override
    public OdmExecutorResultReceivedEventExecutorResponse getOdmMarketplaceAccessRequestPortUploadResult() {
        return initialState.getOdmMarketplaceAccessRequestPortUploadResult();
    }
} 