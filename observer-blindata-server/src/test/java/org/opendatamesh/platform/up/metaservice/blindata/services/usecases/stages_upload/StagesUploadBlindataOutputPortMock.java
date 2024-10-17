package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.stages_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductStageRes;

import java.util.List;
import java.util.Optional;

public class StagesUploadBlindataOutputPortMock implements StagesUploadBlindataOutputPort {

    private final StagesUploadInitialState initialState;

    public StagesUploadBlindataOutputPortMock(StagesUploadInitialState initialState) {
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
