package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdSystemClient;
import org.opendatamesh.platform.up.metaservice.blindata.configurations.BdDataProductConfig;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDSystemRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDProductPortAssetsRes;

import java.util.Optional;

class DataProductPortsAndAssetsUploadBlindataOutboundPortImpl implements DataProductPortsAndAssetsUploadBlindataOutboundPort {

    private final BdDataProductClient bdDataProductClient;
    private final BdDataProductConfig dataProductConfig;
    private final BdSystemClient bdSystemClient;

    public DataProductPortsAndAssetsUploadBlindataOutboundPortImpl(BdDataProductClient bdDataProductClient,  BdDataProductConfig dataProductConfig, BdSystemClient bdSystemClient) {
        this.bdDataProductClient = bdDataProductClient;
        this.dataProductConfig = dataProductConfig;
        this.bdSystemClient = bdSystemClient;
    }

    @Override
    public Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName) {
        return bdDataProductClient.getDataProduct(fullyQualifiedName);
    }

    @Override
    public void updateDataProduct(BDDataProductRes bdDataProductRes) {
        bdDataProductClient.patchDataProduct(bdDataProductRes);
    }

    @Override
    public void updateDataProductPorts(BDDataProductRes dataProduct) {
        bdDataProductClient.patchDataProduct(dataProduct);
    }

    @Override
    public void createDataProductAssets(BDProductPortAssetsRes dataProductPortsAssets) {
        bdDataProductClient.createDataProductAssets(dataProductPortsAssets);
    }

    @Override
    public Optional<BDSystemRes> getSystemDependency(String systemName) {
        return bdSystemClient.getSystem(systemName);
    }

    @Override
    public String getDataProductAdditionalPropertiesRegex() {
        return dataProductConfig.getAdditionalPropertiesRegex();
    }

}
