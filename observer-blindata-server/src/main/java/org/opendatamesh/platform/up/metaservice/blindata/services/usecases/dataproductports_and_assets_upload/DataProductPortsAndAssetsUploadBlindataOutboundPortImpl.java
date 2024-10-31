package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDProductPortAssetsRes;

import java.util.Optional;

class DataProductPortsAndAssetsUploadBlindataOutboundPortImpl implements DataProductPortsAndAssetsUploadBlindataOutboundPort {

    private final BDDataProductClient bdDataProductClient;

    public DataProductPortsAndAssetsUploadBlindataOutboundPortImpl(BDDataProductClient bdDataProductClient) {
        this.bdDataProductClient = bdDataProductClient;
    }

    @Override
    public Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName) {
        return bdDataProductClient.getDataProduct(fullyQualifiedName);
    }

    @Override
    public void updateDataProductPorts(BDDataProductRes dataProduct) {
        bdDataProductClient.patchDataProduct(dataProduct);
    }

    @Override
    public void createDataProductAssets(BDProductPortAssetsRes dataProductPortsAssets) {
        bdDataProductClient.createDataProductAssets(dataProductPortsAssets);
    }

}
