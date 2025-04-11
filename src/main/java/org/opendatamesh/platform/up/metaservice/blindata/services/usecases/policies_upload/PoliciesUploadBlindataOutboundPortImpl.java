package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_upload;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDPolicyEvaluationResultClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicyEvaluationRecords;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDUploadResultsMessage;

import java.util.Optional;

class PoliciesUploadBlindataOutboundPortImpl implements PoliciesUploadBlindataOutboundPort {

    private final BDDataProductClient bdDataProductClient;
    private final BDPolicyEvaluationResultClient policyEvaluationResultClient;

    public PoliciesUploadBlindataOutboundPortImpl(BDDataProductClient bdDataProductClient, BDPolicyEvaluationResultClient policyEvaluationResultClient) {
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
