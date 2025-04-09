package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEngineResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEngineSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OdmPolicyEngineClient {
    Page<OdmPolicyEngineResource> getPolicyEngines(Pageable pageable, OdmPolicyEngineSearchOptions searchOptions);

    OdmPolicyEngineResource getPolicyEngine(Long id);

    OdmPolicyEngineResource createPolicyEngine(OdmPolicyEngineResource policyEngineResource);

    OdmPolicyEngineResource modifyPolicyEngine(Long id, OdmPolicyEngineResource policyEngine);

    void deletePolicyEngine(Long id);
}
