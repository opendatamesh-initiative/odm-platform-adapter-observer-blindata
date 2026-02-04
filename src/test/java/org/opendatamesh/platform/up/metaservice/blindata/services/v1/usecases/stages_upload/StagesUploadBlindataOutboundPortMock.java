package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.stages_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductStageRes;

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
