package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.stages_upload;

import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductStageRes;

import java.util.List;

public class StagesUploadOdmOutputPortMock implements StagesUploadOdmOutputPort {
    private final StagesUploadInitialState initialState;

    public StagesUploadOdmOutputPortMock(StagesUploadInitialState initialState) {
        this.initialState = initialState;
    }

    @Override
    public List<BDDataProductStageRes> extractDataProductStages() {
        return initialState.getDataProductVersionStages();
    }

    @Override
    public DataProductVersionDPDS getDataProductVersion() {
        return initialState.getDataProductDescriptor();
    }
}
