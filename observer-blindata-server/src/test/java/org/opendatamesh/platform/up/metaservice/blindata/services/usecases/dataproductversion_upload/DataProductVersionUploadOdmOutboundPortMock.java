package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductversion_upload;

import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductPortAssetDetailRes;

import java.util.List;

public class DataProductVersionUploadOdmOutboundPortMock implements DataProductVersionUploadOdmOutboundPort {

    private final DataProductVersionUploadInitialState initialState;

    public DataProductVersionUploadOdmOutboundPortMock(DataProductVersionUploadInitialState initialState) {
        this.initialState = initialState;
    }

    @Override
    public DataProductVersionDPDS getDataProductVersion() {
        return initialState.getDataProductDescriptor();
    }

    @Override
    public List<BDDataProductPortAssetDetailRes> extractBDAssetsFromPorts(List<PortDPDS> ports) {
        return initialState.getExtractedAssets();
    }
}
