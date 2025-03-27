package org.opendatamesh.platform.up.metaservice.blindata.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClient;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClientImpl;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyEngineClient;
import org.opendatamesh.platform.pp.policy.api.resources.*;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtils;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtilsFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Configuration
public class ValidatorPolicySubscriber {

    @Value("${blindata.validator.active}")
    private boolean validatorIsActive;
    @Value("${blindata.validator.policyEngine.name}")
    private String policyEngineName;
    @Value("${blindata.validator.policy.name}")
    private String policyName;
    @Value("${blindata.validator.policy.blocking}")
    private boolean isPolicyBlocking;

    @Value("${odm.productPlane.policyService.address}")
    private String policyServiceBaseUrl;
    @Value("${server.baseUrl}")
    private String baseUrl;


    @PostConstruct
    public void init() {
        if (!validatorIsActive) {
            return;
        }
        PolicyEngineClient policyEngineClient = new PolicyEngineClientImpl(
                RestUtilsFactory.getRestUtils(new RestTemplate()),
                policyServiceBaseUrl);
        PolicyClient policyClient = new PolicyClientImpl(policyServiceBaseUrl, new ObjectMapper());

        Optional<PolicyEngineResource> existingEngine = findPolicyEngine(policyEngineClient);
        PolicyEngineResource policyEngine = existingEngine.orElseGet(() -> createPolicyEngine(policyEngineClient));

        PolicyResource policyResource = new PolicyResource();
        policyResource.setPolicyEngine(policyEngine);
        policyResource.setBlockingFlag(isPolicyBlocking);
        policyResource.setName(policyName);

        PolicyEvaluationEventResource dpCreation = new PolicyEvaluationEventResource();
        dpCreation.setEvent("DATA_PRODUCT_VERSION_CREATION");
        PolicyEvaluationEventResource dpvCreation = new PolicyEvaluationEventResource();
        dpvCreation.setEvent("DATA_PRODUCT_CREATION");
        policyResource.setEvaluationEvents(Lists.newArrayList(dpCreation, dpvCreation));


        if (!policyExists(policyClient, policyEngine, policyResource.getName())) {
            policyClient.createPolicy(policyResource);
        }
    }

    private Optional<PolicyEngineResource> findPolicyEngine(PolicyEngineClient policyEngineClient) {
        Page<PolicyEngineResource> engines = policyEngineClient.getPolicyEngines(Pageable.ofSize(500), new PolicyEngineSearchOptions());
        return engines.get().filter(engine -> policyEngineName.equals(engine.getName())).findFirst();
    }

    private PolicyEngineResource createPolicyEngine(PolicyEngineClient policyEngineClient) {
        PolicyEngineResource policyEngine = new PolicyEngineResource();
        policyEngine.setName(policyEngineName);
        policyEngine.setDisplayName(policyEngineName);
        policyEngine.setAdapterUrl(baseUrl);
        return policyEngineClient.createPolicyEngine(policyEngine);
    }

    private boolean policyExists(PolicyClient policyClient, PolicyEngineResource policyEngine, String policyName) {
        PolicySearchOptions filter = new PolicySearchOptions();
        filter.setPolicyEngineName(policyEngine.getName());
        filter.setName(policyName);

        return !policyClient.getPolicies(Pageable.ofSize(1), filter).isEmpty();
    }

    static class PolicyEngineClientImpl implements PolicyEngineClient {

        private final RestUtils restUtils;
        private final String policyEngineBaseUrl;

        PolicyEngineClientImpl(RestUtils restUtils, String baseUrl) {
            this.restUtils = restUtils;
            this.policyEngineBaseUrl = baseUrl;
        }

        @Override
        public Page<PolicyEngineResource> getPolicyEngines(Pageable pageable, PolicyEngineSearchOptions searchOptions) {
            return restUtils.getPage(this.policyEngineBaseUrl + "/api/v1/pp/policy/policy-engines", null, pageable, searchOptions, PolicyEngineResource.class);
        }

        @Override
        public PolicyEngineResource getPolicyEngine(Long id) {
            //To be implemented
            return null;
        }

        @Override
        public PolicyEngineResource createPolicyEngine(PolicyEngineResource policyEngineResource) {
            return restUtils.create(this.policyEngineBaseUrl + "/api/v1/pp/policy/policy-engines", null, policyEngineResource, PolicyEngineResource.class);
        }

        @Override
        public PolicyEngineResource modifyPolicyEngine(Long id, PolicyEngineResource policyEngine) {
            //To be implemented
            return null;
        }

        @Override
        public void deletePolicyEngine(Long id) {
            //to be implemented
        }
    }
}
