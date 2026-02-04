package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.policies_upload;

import org.opendatamesh.dpds.model.info.Info;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEvaluationResultResource;

import java.util.List;

interface PoliciesUploadOdmOutboundPort {
    Info getDataProductInfo();

    List<OdmPolicyEvaluationResultResource> getDataProductPoliciesEvaluationResults(Info info);
}
