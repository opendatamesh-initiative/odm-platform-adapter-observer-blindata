package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_upload;

import org.opendatamesh.dpds.model.info.Info;
import org.opendatamesh.platform.up.metaservice.blindata.client.odm.OdmPolicyEvaluationResultClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEvaluationResultResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEvaluationResultSearchOptions;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;

import java.util.List;

class PoliciesUploadOdmOutboundPortImpl implements PoliciesUploadOdmOutboundPort {

    private final OdmPolicyEvaluationResultClient policyEvaluationResultClient;
    private final Info dataProductInfo;

    public PoliciesUploadOdmOutboundPortImpl(OdmPolicyEvaluationResultClient policyEvaluationResultClient, Info dataProductInfo) {
        this.policyEvaluationResultClient = policyEvaluationResultClient;
        this.dataProductInfo = dataProductInfo;
    }

    @Override
    public Info getDataProductInfo() {
        return dataProductInfo;
    }

    @Override
    public List<OdmPolicyEvaluationResultResource> getDataProductPoliciesEvaluationResults(Info info) {
        OdmPolicyEvaluationResultSearchOptions policyEvaluationResultFilters = new OdmPolicyEvaluationResultSearchOptions();
        policyEvaluationResultFilters.setDataProductId(info.getId());
        if (StringUtils.hasText(info.getVersion())) {
            policyEvaluationResultFilters.setDataProductVersion(info.getVersion());
        }
        return policyEvaluationResultClient.getPolicyEvaluationResults(PageRequest.ofSize(Integer.MAX_VALUE), policyEvaluationResultFilters).toList();
    }
}
