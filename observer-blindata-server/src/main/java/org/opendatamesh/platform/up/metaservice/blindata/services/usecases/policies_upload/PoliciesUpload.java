package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_upload;

import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDPolicyEvaluationRecord;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDPolicyEvaluationRecords;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDUploadResultsMessage;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseRecoverableExceptionContext.getExceptionHandler;

class PoliciesUpload implements UseCase {

    private final String USE_CASE_PREFIX = "[PoliciesUpload]";

    private final PoliciesUploadBlindataOutboundPort blindataOutboundPort;
    private final PoliciesUploadOdmOutboundPort odmOutboundPort;
    private final Logger log;

    public PoliciesUpload(PoliciesUploadBlindataOutboundPort blindataOutboundPort, PoliciesUploadOdmOutboundPort odmOutboundPort) {
        this.blindataOutboundPort = blindataOutboundPort;
        this.odmOutboundPort = odmOutboundPort;
        this.log = LoggerFactory.getLogger(this.getClass());
        getExceptionHandler().setLogger(log);
    }

    public PoliciesUpload(PoliciesUploadBlindataOutboundPort blindataOutboundPort, PoliciesUploadOdmOutboundPort odmOutboundPort, Logger logger) {
        this.blindataOutboundPort = blindataOutboundPort;
        this.odmOutboundPort = odmOutboundPort;
        this.log = logger;
        getExceptionHandler().setLogger(log);
    }


    @Override
    public void execute() throws UseCaseExecutionException {
        try {
            Optional<BDDataProductRes> blindataDataProduct = blindataOutboundPort.findDataProduct(odmOutboundPort.getDataProductInfo().getFullyQualifiedName());
            if (blindataDataProduct.isEmpty()) {
                getExceptionHandler().warn(String.format("%s Data product: %s has not been created yet on Blindata.", USE_CASE_PREFIX, odmOutboundPort.getDataProductInfo().getFullyQualifiedName()));
                return;
            }
            List<PolicyEvaluationResultResource> odmPolicyEvaluationResults = odmOutboundPort.getDataProductPoliciesEvaluationResults(odmOutboundPort.getDataProductInfo());
            if (odmPolicyEvaluationResults.isEmpty()) {
                getExceptionHandler().warn(String.format("%s Data product: %s has not policies evaluation results.", USE_CASE_PREFIX, odmOutboundPort.getDataProductInfo().getFullyQualifiedName()));
                return;
            }

            log.info("{} Data product: {} found {} policies results.", USE_CASE_PREFIX, odmOutboundPort.getDataProductInfo().getFullyQualifiedName(), odmPolicyEvaluationResults.size());
            BDPolicyEvaluationRecords bdPolicyEvaluationRecords = new BDPolicyEvaluationRecords();
            for (PolicyEvaluationResultResource odmPolicyEvaluationResult : odmPolicyEvaluationResults) {
                BDPolicyEvaluationRecord bdPolicyEvaluationRecord = odmEvaluationResultToBlindataEvaluationRecord(blindataDataProduct.get().getUuid(), odmPolicyEvaluationResult);
                bdPolicyEvaluationRecords.getRecords().add(bdPolicyEvaluationRecord);
            }

            BDUploadResultsMessage uploadResultsMessage = blindataOutboundPort.createPolicyEvaluationRecords(bdPolicyEvaluationRecords);
            log.info("{} Data product: {} uploaded {} policies results on Blindata, discarded {}.", USE_CASE_PREFIX, odmOutboundPort.getDataProductInfo().getFullyQualifiedName(), uploadResultsMessage.getRowCreated(), uploadResultsMessage.getRowDiscarded());
        } catch (Exception e) {
            throw new UseCaseExecutionException(e.getMessage(), e);
        }
    }


    private BDPolicyEvaluationRecord odmEvaluationResultToBlindataEvaluationRecord(String bdDataProductUuid, PolicyEvaluationResultResource odmPolicyEvaluationResult) {
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
