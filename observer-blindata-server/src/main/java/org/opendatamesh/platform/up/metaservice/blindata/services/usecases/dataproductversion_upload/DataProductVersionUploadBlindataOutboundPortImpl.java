package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductversion_upload;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDProductPortAssetsRes;

import java.util.Optional;

class DataProductVersionUploadBlindataOutboundPortImpl implements DataProductVersionUploadBlindataOutboundPort {

    private final BDDataProductClient bdDataProductClient;

    public DataProductVersionUploadBlindataOutboundPortImpl(BDDataProductClient bdDataProductClient) {
        this.bdDataProductClient = bdDataProductClient;
    }

    @Override
    public Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName) {
        return bdDataProductClient.getDataProduct(fullyQualifiedName);
    }

    @Override
    public void updateDataProductPorts(BDDataProductRes dataProduct) {
        bdDataProductClient.updateDataProduct(dataProduct);
    }

    @Override
    public void createDataProductAssets(BDProductPortAssetsRes dataProductPortsAssets) {
        bdDataProductClient.createDataProductAssets(dataProductPortsAssets);
    }

}
