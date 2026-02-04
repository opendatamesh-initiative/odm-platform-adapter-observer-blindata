package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.dataproductports_and_assets_upload;

import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.dpds.model.interfaces.Port;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductPortAssetDetailRes;

import java.util.List;

public class DataProductPortsAndAssetsUploadOdmOutboundPortMock implements DataProductPortsAndAssetsUploadOdmOutboundPort {

    private final DataProductVersionUploadInitialState initialState;

    public DataProductPortsAndAssetsUploadOdmOutboundPortMock(DataProductVersionUploadInitialState initialState) {
        this.initialState = initialState;
    }

    @Override
    public DataProductVersion getDataProductVersion() {
        return initialState.getDataProductDescriptor();
    }

    @Override
    public List<BDDataProductPortAssetDetailRes> extractBDAssetsFromPorts(List<Port> ports) {
        return initialState.getExtractedAssets();
    }
}
