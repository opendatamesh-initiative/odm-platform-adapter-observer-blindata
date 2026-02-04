package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.marketplace_portupdater;

import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.states.MarketplaceAccessRequestEventState;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.marketplace.OdmExecutorResultReceivedEventExecutorResponse;

class MarketplaceAccessRequestsPortUpdaterOdmOutboundPortImpl implements MarketplaceAccessRequestsPortUpdaterOdmOutboundPort {
    private final MarketplaceAccessRequestEventState marketplaceAccessRequestEventState;

    public MarketplaceAccessRequestsPortUpdaterOdmOutboundPortImpl(MarketplaceAccessRequestEventState marketplaceAccessRequestEventState) {
        this.marketplaceAccessRequestEventState = marketplaceAccessRequestEventState;
    }

    @Override
    public OdmExecutorResultReceivedEventExecutorResponse getOdmMarketplaceAccessRequestPortUploadResult() {
        return marketplaceAccessRequestEventState.getExecutorResponse();
    }
}
