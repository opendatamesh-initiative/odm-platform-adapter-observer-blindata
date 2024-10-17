package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDPolicyEvaluationRecords;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDUploadResultsMessage;

import java.util.Optional;

interface PoliciesUploadBlindataOutboundPort {
    Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName);

    BDUploadResultsMessage createPolicyEvaluationRecords(BDPolicyEvaluationRecords bdPolicyEvaluationRecords);
}

