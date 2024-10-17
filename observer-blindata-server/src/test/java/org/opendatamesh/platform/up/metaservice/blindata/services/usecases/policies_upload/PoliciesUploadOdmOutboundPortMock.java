package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_upload;

import org.opendatamesh.dpds.model.info.InfoDPDS;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;

import java.util.List;

public class PoliciesUploadOdmOutboundPortMock implements PoliciesUploadOdmOutboundPort {

    private final PoliciesUploadInitialState initialState;

    public PoliciesUploadOdmOutboundPortMock(PoliciesUploadInitialState initialState) {
        this.initialState = initialState;
    }

    @Override
    public InfoDPDS getDataProductInfo() {
        return initialState.getDataProductInfo();
    }

    @Override
    public List<PolicyEvaluationResultResource> getDataProductPoliciesEvaluationResults(InfoDPDS infoDPDS) {
        return initialState.getPoliciesResults();
    }
}
