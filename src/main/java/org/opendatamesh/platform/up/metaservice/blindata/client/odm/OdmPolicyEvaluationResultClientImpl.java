package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtils;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEvaluationResultResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEvaluationResultSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

class OdmPolicyEvaluationResultClientImpl implements OdmPolicyEvaluationResultClient {

    private static final String route = "api/v1/pp/policy";
    private final String baseUrl;
    private final RestUtils restUtils;

    OdmPolicyEvaluationResultClientImpl(String baseUrl, RestUtils restUtils) {
        this.baseUrl = baseUrl;
        this.restUtils = restUtils;
    }


    @Override
    public Page<OdmPolicyEvaluationResultResource> getPolicyEvaluationResults(Pageable pageable, OdmPolicyEvaluationResultSearchOptions searchOptions) {
        return restUtils.getPage(String.format("%s/%s/policy-evaluation-results", baseUrl, route), null, pageable, searchOptions, OdmPolicyEvaluationResultResource.class);
    }

    @Override
    public OdmPolicyEvaluationResultResource getPolicyEvaluationResult(Long id) {
        return restUtils.get(String.format("%s/%s/policy-evaluation-results/{id}", baseUrl, route), null, id, OdmPolicyEvaluationResultResource.class);
    }

    @Override
    public OdmPolicyEvaluationResultResource createPolicyEvaluationResult(OdmPolicyEvaluationResultResource policyEvaluationResult) {
        return restUtils.create(String.format("%s/%s/policy-evaluation-results", baseUrl, route), null, policyEvaluationResult, OdmPolicyEvaluationResultResource.class);
    }

    @Override
    public OdmPolicyEvaluationResultResource modifyPolicyEvaluationResult(Long id, OdmPolicyEvaluationResultResource policyEvaluationResult) {
        return restUtils.put(String.format("%s/%s/policy-evaluation-results/{id}", baseUrl, route), null, id, policyEvaluationResult, OdmPolicyEvaluationResultResource.class);
    }

    @Override
    public void deletePolicyEvaluationResult(Long id) {
        restUtils.delete(String.format("%s/%s/policy-evaluation-results/{id}", baseUrl, route), null, id);
    }


}
