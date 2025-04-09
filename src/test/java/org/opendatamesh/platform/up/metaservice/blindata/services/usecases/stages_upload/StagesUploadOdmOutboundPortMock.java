package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.stages_upload;

import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDDataProductStageRes;

import java.util.List;

public class StagesUploadOdmOutboundPortMock implements StagesUploadOdmOutboundPort {
    private final StagesUploadInitialState initialState;

    public StagesUploadOdmOutboundPortMock(StagesUploadInitialState initialState) {
        this.initialState = initialState;
    }

    @Override
    public List<BDDataProductStageRes> extractDataProductStages() {
        return initialState.getDataProductVersionStages();
    }

    @Override
    public DataProductVersion getDataProductVersion() {
        return initialState.getDataProductDescriptor();
    }
}
