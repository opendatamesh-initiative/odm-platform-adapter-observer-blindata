package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_upload;

import org.opendatamesh.dpds.model.info.InfoDPDS;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyEvaluationResultClient;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultSearchOptions;
import org.springframework.data.domain.PageRequest;

import java.util.List;

class PoliciesUploadOdmOutboundPortImpl implements PoliciesUploadOdmOutboundPort {

    private final PolicyEvaluationResultClient policyEvaluationResultClient;
    private final InfoDPDS dataProductInfo;

    public PoliciesUploadOdmOutboundPortImpl(PolicyEvaluationResultClient policyEvaluationResultClient, InfoDPDS dataProductInfo) {
        this.policyEvaluationResultClient = policyEvaluationResultClient;
        this.dataProductInfo = dataProductInfo;
    }

    @Override
    public InfoDPDS getDataProductInfo() {
        return dataProductInfo;
    }

    @Override
    public List<PolicyEvaluationResultResource> getDataProductPoliciesEvaluationResults(InfoDPDS odmDataProductInfo) {
        PolicyEvaluationResultSearchOptions policyEvaluationResultFilters = new PolicyEvaluationResultSearchOptions();
        policyEvaluationResultFilters.setDataProductId(odmDataProductInfo.getDataProductId());
        return policyEvaluationResultClient.getPolicyEvaluationResults(PageRequest.ofSize(Integer.MAX_VALUE), policyEvaluationResultFilters).toList();
    }
}
