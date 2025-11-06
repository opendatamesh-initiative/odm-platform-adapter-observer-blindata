package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDSystemRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDProductPortAssetsRes;

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
    public void updateDataProduct(BDDataProductRes bdDataProductRes) {
        //DO NOTHING
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

    @Override
    public String getDataProductAdditionalPropertiesRegex() {
        return "\\bx-([\\S]+)";
    }
}
