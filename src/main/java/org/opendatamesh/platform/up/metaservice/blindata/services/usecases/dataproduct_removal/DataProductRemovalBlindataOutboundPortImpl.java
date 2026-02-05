package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_removal;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;

import java.util.Optional;

class DataProductRemovalBlindataOutboundPortImpl implements DataProductRemovalBlindataOutboundPort {

    private final BdDataProductClient bdDataProductClient;

    public DataProductRemovalBlindataOutboundPortImpl(BdDataProductClient bdDataProductClient) {
        this.bdDataProductClient = bdDataProductClient;
    }

    @Override
    public Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName) {
        return bdDataProductClient.getDataProduct(fullyQualifiedName);
    }

    @Override
    public void deleteDataProduct(String dataProductUuid) {
        bdDataProductClient.deleteDataProduct(dataProductUuid);
    }
}
