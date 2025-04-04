package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_upload;

import org.opendatamesh.dpds.model.info.InfoDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.client.odm.OdmPolicyEvaluationResultClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEvaluationResultResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEvaluationResultSearchOptions;
import org.springframework.data.domain.PageRequest;

import java.util.List;

class PoliciesUploadOdmOutboundPortImpl implements PoliciesUploadOdmOutboundPort {

    private final OdmPolicyEvaluationResultClient policyEvaluationResultClient;
    private final InfoDPDS dataProductInfo;

    public PoliciesUploadOdmOutboundPortImpl(OdmPolicyEvaluationResultClient policyEvaluationResultClient, InfoDPDS dataProductInfo) {
        this.policyEvaluationResultClient = policyEvaluationResultClient;
        this.dataProductInfo = dataProductInfo;
    }

    @Override
    public InfoDPDS getDataProductInfo() {
        return dataProductInfo;
    }

    @Override
    public List<OdmPolicyEvaluationResultResource> getDataProductPoliciesEvaluationResults(InfoDPDS odmDataProductInfo) {
        OdmPolicyEvaluationResultSearchOptions policyEvaluationResultFilters = new OdmPolicyEvaluationResultSearchOptions();
        policyEvaluationResultFilters.setDataProductId(odmDataProductInfo.getDataProductId());
        return policyEvaluationResultClient.getPolicyEvaluationResults(PageRequest.ofSize(Integer.MAX_VALUE), policyEvaluationResultFilters).toList();
    }
}
