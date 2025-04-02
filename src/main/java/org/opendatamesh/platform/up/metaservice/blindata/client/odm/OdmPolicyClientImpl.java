package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtils;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicySearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

class OdmPolicyClientImpl implements OdmPolicyClient {

    private static final String route = "api/v1/pp/policy";
    private final String baseUrl;
    private final RestUtils restUtils;

    OdmPolicyClientImpl(String baseUrl, RestUtils restUtils) {
        this.baseUrl = baseUrl;
        this.restUtils = restUtils;
    }

    @Override
    public Page<OdmPolicyResource> getPolicies(Pageable pageable, OdmPolicySearchOptions searchOptions) {
        return restUtils.getPage(String.format("%s/%s/policies", baseUrl, route), null, pageable, searchOptions, OdmPolicyResource.class);
    }

    @Override
    public OdmPolicyResource getPolicy(Long id) {
        return restUtils.get(String.format("%s/%s/policies/{id}", baseUrl, route), null, id, OdmPolicyResource.class);
    }

    @Override
    public OdmPolicyResource getPolicyVersion(Long versionId) {
        return restUtils.get(String.format("%s/%s/policies/versions/{id}", baseUrl, route), null, versionId, OdmPolicyResource.class);
    }

    @Override
    public OdmPolicyResource createPolicy(OdmPolicyResource policy) {
        return restUtils.create(String.format("%s/%s/policies", baseUrl, route), null, policy, OdmPolicyResource.class);
    }

    @Override
    public OdmPolicyResource modifyPolicy(Long id, OdmPolicyResource policy) {
        return restUtils.put(String.format("%s/%s/policies/{id}", baseUrl, route), null, id, policy, OdmPolicyResource.class);
    }

    @Override
    public void deletePolicy(Long id) {
        restUtils.delete(String.format("%s/%s/policies/{id}", baseUrl, route), null, id);
    }
}
