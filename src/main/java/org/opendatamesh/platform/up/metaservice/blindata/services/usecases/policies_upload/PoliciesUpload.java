package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicyEvaluationRecord;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicyEvaluationRecords;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicyResultsUploadResultsRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEvaluationResultResource;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;

class PoliciesUpload implements UseCase {

    private final String USE_CASE_PREFIX = "[PoliciesUpload]";

    private final PoliciesUploadBlindataOutboundPort blindataOutboundPort;
    private final PoliciesUploadOdmOutboundPort odmOutboundPort;

    public PoliciesUpload(PoliciesUploadBlindataOutboundPort blindataOutboundPort, PoliciesUploadOdmOutboundPort odmOutboundPort) {
        this.blindataOutboundPort = blindataOutboundPort;
        this.odmOutboundPort = odmOutboundPort;
    }


    @Override
    public void execute() throws UseCaseExecutionException {
        try {
            Optional<BDDataProductRes> blindataDataProduct = blindataOutboundPort.findDataProduct(odmOutboundPort.getDataProductInfo().getFullyQualifiedName());
            if (blindataDataProduct.isEmpty()) {
                getUseCaseLogger().warn(String.format("%s Data product: %s has not been created yet on Blindata.", USE_CASE_PREFIX, odmOutboundPort.getDataProductInfo().getFullyQualifiedName()));
                return;
            }
            List<OdmPolicyEvaluationResultResource> odmPolicyEvaluationResults = odmOutboundPort.getDataProductPoliciesEvaluationResults(odmOutboundPort.getDataProductInfo());
            if (odmPolicyEvaluationResults.isEmpty()) {
                getUseCaseLogger().warn(String.format("%s Data product: %s has not policies evaluation results.", USE_CASE_PREFIX, odmOutboundPort.getDataProductInfo().getFullyQualifiedName()));
                return;
            }

            getUseCaseLogger().info(String.format("%s Data product: %s found %s policies results.", USE_CASE_PREFIX, odmOutboundPort.getDataProductInfo().getFullyQualifiedName(), odmPolicyEvaluationResults.size()));
            BDPolicyEvaluationRecords bdPolicyEvaluationRecords = new BDPolicyEvaluationRecords();
            for (OdmPolicyEvaluationResultResource odmPolicyEvaluationResult : odmPolicyEvaluationResults) {
                BDPolicyEvaluationRecord bdPolicyEvaluationRecord = odmEvaluationResultToBlindataEvaluationRecord(blindataDataProduct.get().getUuid(), odmPolicyEvaluationResult);
                bdPolicyEvaluationRecords.getRecords().add(bdPolicyEvaluationRecord);
            }

            BDPolicyResultsUploadResultsRes uploadResultsMessage = blindataOutboundPort.createPolicyEvaluationRecords(bdPolicyEvaluationRecords);
            getUseCaseLogger().info(String.format("%s Data product: %s uploaded %s policies results on Blindata, discarded %s.", USE_CASE_PREFIX, odmOutboundPort.getDataProductInfo().getFullyQualifiedName(), uploadResultsMessage.getRowCreated(), uploadResultsMessage.getRowDiscarded()));
            logPolicyEvaluationRecordsUploadErrors(uploadResultsMessage);
        } catch (Exception e) {
            throw new UseCaseExecutionException(e.getMessage(), e);
        }
    }

    private void logPolicyEvaluationRecordsUploadErrors(BDPolicyResultsUploadResultsRes uploadResultsMessage) {
        if (!CollectionUtils.isEmpty(uploadResultsMessage.getErrors())) {
            uploadResultsMessage.getErrors().stream().filter(error -> StringUtils.hasText(error.getMessage()))
                    .forEach(error ->
                            getUseCaseLogger().warn(
                                    String.format("%s Data product: %s error on policy result upload: %s.", USE_CASE_PREFIX, odmOutboundPort.getDataProductInfo().getFullyQualifiedName(), error.getMessage())
                            )
                    );
        }
    }


    private BDPolicyEvaluationRecord odmEvaluationResultToBlindataEvaluationRecord(String bdDataProductUuid, OdmPolicyEvaluationResultResource odmPolicyEvaluationResult) {
        BDPolicyEvaluationRecord bdPolicyEvaluationRecord = new BDPolicyEvaluationRecord();
        bdPolicyEvaluationRecord.setPolicyName(odmPolicyEvaluationResult.getPolicy().getName());
        bdPolicyEvaluationRecord.setImplementationName(odmPolicyEvaluationResult.getPolicy().getName());
        bdPolicyEvaluationRecord.setResolverKey("uuid");
        bdPolicyEvaluationRecord.setResolverValue(bdDataProductUuid);
        bdPolicyEvaluationRecord.setResourceType("DATA_PRODUCT");
        bdPolicyEvaluationRecord.setEvaluationResult(Boolean.TRUE.equals(odmPolicyEvaluationResult.getResult()) ? BDPolicyEvaluationRecord.BDPolicyEvaluationResult.VERIFIED : BDPolicyEvaluationRecord.BDPolicyEvaluationResult.FAILED);
        bdPolicyEvaluationRecord.setEvaluationDate(odmPolicyEvaluationResult.getCreatedAt());
        return bdPolicyEvaluationRecord;
    }

}
