package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicyEvaluationRecords;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDUploadResultsMessage;

import java.util.Optional;

interface PoliciesUploadBlindataOutboundPort {
    Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName);

    BDUploadResultsMessage createPolicyEvaluationRecords(BDPolicyEvaluationRecords bdPolicyEvaluationRecords);
}

