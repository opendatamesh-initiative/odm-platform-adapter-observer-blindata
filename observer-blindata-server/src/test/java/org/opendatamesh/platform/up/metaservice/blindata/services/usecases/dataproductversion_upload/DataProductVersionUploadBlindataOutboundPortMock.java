package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductversion_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDProductPortAssetsRes;

import java.util.Optional;

public class DataProductVersionUploadBlindataOutboundPortMock implements DataProductVersionUploadBlindataOutboundPort {

    private final DataProductVersionUploadInitialState initialState;

    public DataProductVersionUploadBlindataOutboundPortMock(DataProductVersionUploadInitialState initialState) {
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
}
