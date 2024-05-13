package org.opendatamesh.platform.up.metaservice.blindata.client.odm.proxies;

import org.opendatamesh.platform.pp.policy.api.clients.PolicyEvaluationResultClient;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class OdmPolicyProxy implements PolicyEvaluationResultClient {

    private final PolicyEvaluationResultClient policyEvaluationResultClient;

    public OdmPolicyProxy(PolicyEvaluationResultClient policyEvaluationResultClient) {
        this.policyEvaluationResultClient = policyEvaluationResultClient;
    }

    @Override
    public Page<PolicyEvaluationResultResource> getPolicyEvaluationResults(Pageable pageable, PolicyEvaluationResultSearchOptions searchOptions) {
        if (policyEvaluationResultClient != null) {
            return policyEvaluationResultClient.getPolicyEvaluationResults(pageable, searchOptions);
        } else {
            return Page.empty();
        }
    }

    @Override
    public PolicyEvaluationResultResource getPolicyEvaluationResult(Long id) {
        if (policyEvaluationResultClient != null) {
            return policyEvaluationResultClient.getPolicyEvaluationResult(id);
        } else {
            return null;
        }
    }

    @Override
    public PolicyEvaluationResultResource createPolicyEvaluationResult(PolicyEvaluationResultResource policyEvaluationResult) {
        if (policyEvaluationResultClient != null) {
            return policyEvaluationResultClient.createPolicyEvaluationResult(policyEvaluationResult);
        } else {
            return null;
        }
    }

    @Override
    public PolicyEvaluationResultResource modifyPolicyEvaluationResult(Long id, PolicyEvaluationResultResource policyEvaluationResult) {
        if (policyEvaluationResultClient != null) {
            return policyEvaluationResultClient.modifyPolicyEvaluationResult(id, policyEvaluationResult);
        } else {
            return null;
        }
    }

    @Override
    public void deletePolicyEvaluationResult(Long id) {
        if (policyEvaluationResultClient != null) {
            policyEvaluationResultClient.deletePolicyEvaluationResult(id);
        }
    }
}
