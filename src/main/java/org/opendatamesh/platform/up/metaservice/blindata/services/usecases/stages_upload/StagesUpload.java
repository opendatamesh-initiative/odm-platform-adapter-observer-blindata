package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.stages_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductStageRes;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

import static org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;

class StagesUpload implements UseCase {
    private final String USE_CASE_PREFIX = "[StagesUpload]";

    private final StagesUploadBlindataOutboundPort blindataOutboundPort;
    private final StagesUploadOdmOutboundPort odmOutboundPort;

    StagesUpload(StagesUploadBlindataOutboundPort blindataOutboundPort, StagesUploadOdmOutboundPort odmOutboundPort) {
        this.blindataOutboundPort = blindataOutboundPort;
        this.odmOutboundPort = odmOutboundPort;
    }


    @Override
    public void execute() throws UseCaseExecutionException {
        try {
            Optional<BDDataProductRes> existentDataProduct = blindataOutboundPort.findDataProduct(odmOutboundPort.getDataProductVersion().getInfo().getFullyQualifiedName());
            if (existentDataProduct.isEmpty()) {
                getUseCaseLogger().warn(String.format("%s Data product: %s has not been created yet on Blindata.", USE_CASE_PREFIX, odmOutboundPort.getDataProductVersion().getInfo().getFullyQualifiedName()));
                return;
            }

            List<BDDataProductStageRes> stages = odmOutboundPort.extractDataProductStages();
            getUseCaseLogger().info(String.format("%s Data product: %s, found %s stages.", USE_CASE_PREFIX, existentDataProduct.get().getIdentifier(), stages.size()));
            if (!CollectionUtils.isEmpty(stages)) {
                blindataOutboundPort.uploadDataProductStages(existentDataProduct.get().getUuid(), stages);
            }
        } catch (Exception e) {
            throw new UseCaseExecutionException(e.getMessage(), e);
        }
    }
}
