package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.stages_upload;

import lombok.extern.slf4j.Slf4j;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductStageRes;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
class StagesUpload implements UseCase {
    private final String USE_CASE_PREFIX = "[StagesUpload]";

    private final StagesUploadBlindataOutputPort blindataOutputPort;
    private final StagesUploadOdmOutputPort odmOutputPort;

    StagesUpload(StagesUploadBlindataOutputPort blindataOutputPort, StagesUploadOdmOutputPort odmOutputPort) {
        this.blindataOutputPort = blindataOutputPort;
        this.odmOutputPort = odmOutputPort;
    }

    @Override
    public void execute() throws UseCaseExecutionException {
        try {
            Optional<BDDataProductRes> existentDataProduct = blindataOutputPort.findDataProduct(odmOutputPort.getDataProductVersion().getInfo().getFullyQualifiedName());
            if (existentDataProduct.isEmpty()) {
                log.warn("{} Data product: {} has not been created yet on Blindata.", USE_CASE_PREFIX, odmOutputPort.getDataProductVersion().getInfo().getFullyQualifiedName());
                return;
            }

            List<BDDataProductStageRes> stages = odmOutputPort.extractDataProductStages();
            log.info("{} Data product: {}, found {} stages.", USE_CASE_PREFIX, existentDataProduct.get().getIdentifier(), stages.size());
            if (!CollectionUtils.isEmpty(stages)) {
                blindataOutputPort.uploadDataProductStages(existentDataProduct.get().getUuid(), stages);
            }
        } catch (Exception e) {
            throw new UseCaseExecutionException(e.getMessage(), e);
        }
    }
}
