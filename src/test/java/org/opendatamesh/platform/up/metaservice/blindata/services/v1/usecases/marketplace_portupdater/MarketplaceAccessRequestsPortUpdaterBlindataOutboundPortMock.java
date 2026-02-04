package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.marketplace_portupdater;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.marketplace.BDMarketplaceAccessRequestsUploadRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDMarketplaceAccessRequestPortStatusUploadResultsRes;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.marketplace_portupdater.MarketplaceAccessRequestsPortUpdaterBlindataOutboundPort;

public class MarketplaceAccessRequestsPortUpdaterBlindataOutboundPortMock implements MarketplaceAccessRequestsPortUpdaterBlindataOutboundPort {

    @Override
    public BDMarketplaceAccessRequestPortStatusUploadResultsRes uploadPortStatuses(BDMarketplaceAccessRequestsUploadRes uploadRes) {
        return new BDMarketplaceAccessRequestPortStatusUploadResultsRes();
    }
} 