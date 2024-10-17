package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductversion_upload;

import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductPortAssetDetailRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductStageRes;

import java.util.List;

public class DataProductVersionUploadOdmOutputPortMock implements DataProductVersionUploadOdmOutputPort {

    private final DataProductVersionUploadInitialState initialState;

    public DataProductVersionUploadOdmOutputPortMock(DataProductVersionUploadInitialState initialState) {
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
