package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.marketplace_portupdater;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdMarketplaceAccessRequestsUploadResultClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.marketplace.BDMarketplaceAccessRequestsUploadRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDMarketplaceAccessRequestPortStatusUploadResultsRes;

class MarketplaceAccessRequestsPortUpdateBlindataOutboundPortImpl implements MarketplaceAccessRequestsPortUpdaterBlindataOutboundPort {
    private final BdMarketplaceAccessRequestsUploadResultClient bdMarketplaceAccessRequestsUploadResultClient;

    public MarketplaceAccessRequestsPortUpdateBlindataOutboundPortImpl(BdMarketplaceAccessRequestsUploadResultClient bdMarketplaceAccessRequestsUploadResultClient) {
        this.bdMarketplaceAccessRequestsUploadResultClient = bdMarketplaceAccessRequestsUploadResultClient;
    }

    @Override
    public BDMarketplaceAccessRequestPortStatusUploadResultsRes uploadPortStatuses(BDMarketplaceAccessRequestsUploadRes bdMarketplaceAccessRequestsUploadRes) {
        return bdMarketplaceAccessRequestsUploadResultClient.uploadAccessRequestPortStatusRes(bdMarketplaceAccessRequestsUploadRes);
    }
}
