package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.marketplace_portupdater;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.marketplace.OdmExecutorResultReceivedEventExecutorResponse;

interface MarketplaceAccessRequestsPortUpdaterOdmOutboundPort {
    OdmExecutorResultReceivedEventExecutorResponse getOdmMarketplaceAccessRequestPortUploadResult();
}
