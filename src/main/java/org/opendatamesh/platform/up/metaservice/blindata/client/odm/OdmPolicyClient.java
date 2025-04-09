package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicySearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OdmPolicyClient {
    Page<OdmPolicyResource> getPolicies(Pageable pageable, OdmPolicySearchOptions searchOptions);

    OdmPolicyResource getPolicy(Long id);

    OdmPolicyResource getPolicyVersion(Long versionId);

    OdmPolicyResource createPolicy(OdmPolicyResource policy);

    OdmPolicyResource modifyPolicy(Long id, OdmPolicyResource policy);

    void deletePolicy(Long id);
}
