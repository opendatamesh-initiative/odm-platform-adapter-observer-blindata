package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicyEvaluationRecords;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicyResultsUploadResultsRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityUploadResultsRes;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.exceptions.BlindataClientException;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.exceptions.BlindataClientResourceMappingException;

public interface BDPolicyEvaluationResultClient {
    BDPolicyResultsUploadResultsRes createPolicyEvaluationRecords(BDPolicyEvaluationRecords evaluationRecords) throws BlindataClientException, BlindataClientResourceMappingException;
}
