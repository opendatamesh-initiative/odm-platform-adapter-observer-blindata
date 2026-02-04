package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.policies_upload;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdPolicyEvaluationResultClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicyEvaluationRecords;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicyResultsUploadResultsRes;

import java.util.Optional;

class PoliciesUploadBlindataOutboundPortImpl implements PoliciesUploadBlindataOutboundPort {

    private final BdDataProductClient bdDataProductClient;
    private final BdPolicyEvaluationResultClient policyEvaluationResultClient;

    public PoliciesUploadBlindataOutboundPortImpl(BdDataProductClient bdDataProductClient, BdPolicyEvaluationResultClient policyEvaluationResultClient) {
        this.bdDataProductClient = bdDataProductClient;
        this.policyEvaluationResultClient = policyEvaluationResultClient;
    }

    @Override
    public Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName) {
        return bdDataProductClient.getDataProduct(fullyQualifiedName);
    }

    @Override
    public BDPolicyResultsUploadResultsRes createPolicyEvaluationRecords(BDPolicyEvaluationRecords bdPolicyEvaluationRecords) {
        return policyEvaluationResultClient.createPolicyEvaluationRecords(bdPolicyEvaluationRecords);
    }
}
