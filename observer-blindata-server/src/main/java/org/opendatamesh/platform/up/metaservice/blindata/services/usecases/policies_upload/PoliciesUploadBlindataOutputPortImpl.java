package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_upload;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDPolicyEvaluationResultClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDPolicyEvaluationRecords;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDUploadResultsMessage;

import java.util.Optional;

class PoliciesUploadBlindataOutputPortImpl implements PoliciesUploadBlindataOutputPort {

    private final BDDataProductClient bdDataProductClient;
    private final BDPolicyEvaluationResultClient policyEvaluationResultClient;

    public PoliciesUploadBlindataOutputPortImpl(BDDataProductClient bdDataProductClient, BDPolicyEvaluationResultClient policyEvaluationResultClient) {
        this.bdDataProductClient = bdDataProductClient;
        this.policyEvaluationResultClient = policyEvaluationResultClient;
    }

    @Override
    public Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName) {
        return bdDataProductClient.getDataProduct(fullyQualifiedName);
    }

    @Override
    public BDUploadResultsMessage createPolicyEvaluationRecords(BDPolicyEvaluationRecords bdPolicyEvaluationRecords) {
        return policyEvaluationResultClient.createPolicyEvaluationRecords(bdPolicyEvaluationRecords);
    }
}
