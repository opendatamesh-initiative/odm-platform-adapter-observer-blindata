package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_upload;

import org.opendatamesh.dpds.model.info.Info;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEvaluationResultResource;

import java.util.List;

public class PoliciesUploadOdmOutboundPortMock implements PoliciesUploadOdmOutboundPort {

    private final PoliciesUploadInitialState initialState;

    public PoliciesUploadOdmOutboundPortMock(PoliciesUploadInitialState initialState) {
        this.initialState = initialState;
    }

    @Override
    public Info getDataProductInfo() {
        return initialState.getDataProductInfo();
    }

    @Override
    public List<OdmPolicyEvaluationResultResource> getDataProductPoliciesEvaluationResults(Info info) {
        return initialState.getPoliciesResults();
    }
}
