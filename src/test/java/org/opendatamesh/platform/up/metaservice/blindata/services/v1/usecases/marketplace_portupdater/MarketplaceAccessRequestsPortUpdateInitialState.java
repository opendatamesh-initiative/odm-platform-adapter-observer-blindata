package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.marketplace_portupdater;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.marketplace.OdmExecutorResultReceivedEventExecutorResponse;

public class MarketplaceAccessRequestsPortUpdateInitialState {
    private OdmExecutorResultReceivedEventExecutorResponse odmMarketplaceAccessRequestPortUploadResult;

    public MarketplaceAccessRequestsPortUpdateInitialState() {
    }

    public OdmExecutorResultReceivedEventExecutorResponse getOdmMarketplaceAccessRequestPortUploadResult() {
        return this.odmMarketplaceAccessRequestPortUploadResult;
    }

    public void setOdmMarketplaceAccessRequestPortUploadResult(OdmExecutorResultReceivedEventExecutorResponse odmMarketplaceAccessRequestPortUploadResult) {
        this.odmMarketplaceAccessRequestPortUploadResult = odmMarketplaceAccessRequestPortUploadResult;
    }
} 