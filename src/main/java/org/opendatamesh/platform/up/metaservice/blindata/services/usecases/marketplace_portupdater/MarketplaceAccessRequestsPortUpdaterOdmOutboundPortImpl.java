package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.marketplace_portupdater;

import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states.MarketplaceAccessRequestEventState;
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
