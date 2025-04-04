package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDProductPortAssetsRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDSystemRes;

import java.util.Optional;

public class DataProductPortsAndAssetsUploadBlindataOutboundPortMock implements DataProductPortsAndAssetsUploadBlindataOutboundPort {

    private final DataProductVersionUploadInitialState initialState;

    public DataProductPortsAndAssetsUploadBlindataOutboundPortMock(DataProductVersionUploadInitialState initialState) {
        this.initialState = initialState;
    }


    @Override
    public Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName) {
        return Optional.ofNullable(initialState.getExistentDataProduct());
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
        return Optional.empty();
    }
}
