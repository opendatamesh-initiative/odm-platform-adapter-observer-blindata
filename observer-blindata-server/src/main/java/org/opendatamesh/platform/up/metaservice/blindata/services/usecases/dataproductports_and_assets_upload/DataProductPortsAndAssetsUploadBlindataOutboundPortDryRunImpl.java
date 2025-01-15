package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDProductPortAssetsRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDSystemRes;

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

}
