package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.policies_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicyEvaluationRecords;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicyResultsUploadResultsRes;

import java.util.Optional;

public class PoliciesUploadBlindataOutboundPortMock implements PoliciesUploadBlindataOutboundPort {

    private final PoliciesUploadInitialState initialState;

    public PoliciesUploadBlindataOutboundPortMock(PoliciesUploadInitialState initialState) {
        this.initialState = initialState;
    }

    @Override
    public Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName) {
        return Optional.ofNullable(initialState.getExistentDataProduct());
    }

    @Override
    public BDPolicyResultsUploadResultsRes createPolicyEvaluationRecords(BDPolicyEvaluationRecords bdPolicyEvaluationRecords) {
        return new BDPolicyResultsUploadResultsRes();
    }
}
