package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_upload;

import lombok.Data;
import org.opendatamesh.dpds.model.info.InfoDPDS;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;

import java.util.List;

@Data
public class PoliciesUploadInitialState {
    private BDDataProductRes existentDataProduct;
    private InfoDPDS dataProductInfo;
    private List<PolicyEvaluationResultResource> policiesResults;
}
