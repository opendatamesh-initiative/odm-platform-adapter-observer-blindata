package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_removal;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDDataProductClient;

class DataProductRemovalBlindataOutboundPortImpl implements DataProductRemovalBlindataOutboundPort {

    private final BDDataProductClient bdDataProductClient;

    public DataProductRemovalBlindataOutboundPortImpl(BDDataProductClient bdDataProductClient) {
        this.bdDataProductClient = bdDataProductClient;
    }

    @Override
    public void deleteDataProduct(String fullyQualifiedName) {
        bdDataProductClient.deleteDataProduct(fullyQualifiedName);
    }
}
