package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDPolicyEvaluationRecords;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDUploadResultsMessage;

import java.util.Optional;

public class PoliciesUploadBlindataOutputPortMock implements PoliciesUploadBlindataOutputPort {

    private final PoliciesUploadInitialState initialState;

    public PoliciesUploadBlindataOutputPortMock(PoliciesUploadInitialState initialState) {
        this.initialState = initialState;
    }

    @Override
    public Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName) {
        return Optional.ofNullable(initialState.getExistentDataProduct());
    }

    @Override
    public BDUploadResultsMessage createPolicyEvaluationRecords(BDPolicyEvaluationRecords bdPolicyEvaluationRecords) {
        return new BDUploadResultsMessage();
    }
}
