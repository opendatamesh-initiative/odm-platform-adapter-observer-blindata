package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_upload;

import org.opendatamesh.dpds.model.info.InfoDPDS;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;

import java.util.List;

public interface PoliciesUploadOdmOutputPort {
    InfoDPDS getDataProductInfo();
    List<PolicyEvaluationResultResource> getDataProductPoliciesEvaluationResults(InfoDPDS infoDPDS);
}
