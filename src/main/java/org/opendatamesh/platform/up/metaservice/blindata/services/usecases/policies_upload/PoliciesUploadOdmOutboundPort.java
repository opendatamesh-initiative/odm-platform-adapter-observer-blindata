package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_upload;

import org.opendatamesh.dpds.model.info.InfoDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEvaluationResultResource;

import java.util.List;

interface PoliciesUploadOdmOutboundPort {
    InfoDPDS getDataProductInfo();

    List<OdmPolicyEvaluationResultResource> getDataProductPoliciesEvaluationResults(InfoDPDS infoDPDS);
}
