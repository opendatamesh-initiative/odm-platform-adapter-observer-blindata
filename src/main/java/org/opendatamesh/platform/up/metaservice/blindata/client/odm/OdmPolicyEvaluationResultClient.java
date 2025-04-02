package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEvaluationResultResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEvaluationResultSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OdmPolicyEvaluationResultClient {

    Page<OdmPolicyEvaluationResultResource> getPolicyEvaluationResults(Pageable pageable, OdmPolicyEvaluationResultSearchOptions searchOptions);

    OdmPolicyEvaluationResultResource getPolicyEvaluationResult(Long id);

    OdmPolicyEvaluationResultResource createPolicyEvaluationResult(OdmPolicyEvaluationResultResource policyEvaluationResult);

    OdmPolicyEvaluationResultResource modifyPolicyEvaluationResult(Long id, OdmPolicyEvaluationResultResource policyEvaluationResult);

    void deletePolicyEvaluationResult(Long id);

}
