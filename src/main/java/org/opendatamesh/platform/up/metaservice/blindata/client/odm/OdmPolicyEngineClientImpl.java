package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.apache.commons.lang3.NotImplementedException;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtils;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEngineResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEngineSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

class OdmPolicyEngineClientImpl implements OdmPolicyEngineClient {

    private final RestUtils restUtils;
    private final String policyEngineBaseUrl;

    OdmPolicyEngineClientImpl(RestUtils restUtils, String baseUrl) {
        this.restUtils = restUtils;
        this.policyEngineBaseUrl = baseUrl;
    }

    @Override
    public Page<OdmPolicyEngineResource> getPolicyEngines(Pageable pageable, OdmPolicyEngineSearchOptions searchOptions) {
        return restUtils.getPage(this.policyEngineBaseUrl + "/api/v1/pp/policy/policy-engines", null, pageable, searchOptions, OdmPolicyEngineResource.class);
    }

    @Override
    public OdmPolicyEngineResource getPolicyEngine(Long id) {
        throw new NotImplementedException("getPolicyEngine not implemented yet");
    }

    @Override
    public OdmPolicyEngineResource createPolicyEngine(OdmPolicyEngineResource policyEngineResource) {
        return restUtils.create(this.policyEngineBaseUrl + "/api/v1/pp/policy/policy-engines", null, policyEngineResource, OdmPolicyEngineResource.class);
    }

    @Override
    public OdmPolicyEngineResource modifyPolicyEngine(Long id, OdmPolicyEngineResource policyEngine) {
        throw new NotImplementedException("modifyPolicyEngine not implemented yet");
    }

    @Override
    public void deletePolicyEngine(Long id) {
        throw new NotImplementedException("deletePolicyEngine not implemented yet");
    }
}
