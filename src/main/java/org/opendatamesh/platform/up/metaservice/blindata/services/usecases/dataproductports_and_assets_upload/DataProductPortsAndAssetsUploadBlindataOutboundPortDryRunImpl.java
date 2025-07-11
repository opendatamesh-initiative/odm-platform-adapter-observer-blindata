package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDSystemRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDProductPortAssetsRes;

import java.util.Optional;

class DataProductPortsAndAssetsUploadBlindataOutboundPortDryRunImpl implements DataProductPortsAndAssetsUploadBlindataOutboundPort {

    private final DataProductPortsAndAssetsUploadBlindataOutboundPort outboundPort;

    DataProductPortsAndAssetsUploadBlindataOutboundPortDryRunImpl(DataProductPortsAndAssetsUploadBlindataOutboundPort outboundPort) {
        this.outboundPort = outboundPort;
    }


    @Override
    public Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName) {
        return outboundPort.findDataProduct(fullyQualifiedName);
    }

    @Override
    public void updateDataProductPorts(BDDataProductRes dataProduct) {
        //DO NOTHING
    }

    @Override
    public void createDataProductAssets(BDProductPortAssetsRes dataProductPortsAssets) {
        //DO NOTHING
    }

    @Override
    public Optional<BDSystemRes> getSystemDependency(String portDependency) {
        return outboundPort.getSystemDependency(portDependency);
    }

    @Override
    public String getDataProductAdditionalPropertiesRegex() {
        return outboundPort.getDataProductAdditionalPropertiesRegex();
    }

}
