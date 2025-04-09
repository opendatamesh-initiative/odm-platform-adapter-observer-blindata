package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.stages_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDDataProductStageRes;

import java.util.List;
import java.util.Optional;

public class StagesUploadBlindataOutboundPortMock implements StagesUploadBlindataOutboundPort {

    private final StagesUploadInitialState initialState;

    public StagesUploadBlindataOutboundPortMock(StagesUploadInitialState initialState) {
        this.initialState = initialState;
    }

    @Override
    public void uploadDataProductStages(String dataProductUuid, List<BDDataProductStageRes> stages) {
        //DO NOTHING
    }

    @Override
    public Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName) {
        return Optional.of(initialState.getExistentDataProduct());
    }
}
