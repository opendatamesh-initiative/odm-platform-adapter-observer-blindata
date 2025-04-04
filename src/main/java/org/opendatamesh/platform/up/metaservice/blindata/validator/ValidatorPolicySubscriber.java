package org.opendatamesh.platform.up.metaservice.blindata.validator;

import com.google.common.collect.Lists;
import org.opendatamesh.platform.up.metaservice.blindata.client.odm.OdmPolicyClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.odm.OdmPolicyEngineClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEngineSearchOptions;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEngineResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEvaluationEventResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicySearchOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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


    @Value("${server.baseUrl}")
    private String baseUrl;

    @Autowired
    private OdmPolicyEngineClient policyEngineClient;

    @Autowired
    private OdmPolicyClient policyClient;

    @PostConstruct
    public void init() {
        if (!validatorIsActive) {
            return;
        }

        Optional<OdmPolicyEngineResource> existingEngine = findPolicyEngine(policyEngineClient);
        OdmPolicyEngineResource policyEngine = existingEngine.orElseGet(() -> createPolicyEngine(policyEngineClient));

        OdmPolicyResource policyResource = new OdmPolicyResource();
        policyResource.setPolicyEngine(policyEngine);
        policyResource.setBlockingFlag(isPolicyBlocking);
        policyResource.setName(policyName);

        OdmPolicyEvaluationEventResource dpCreation = new OdmPolicyEvaluationEventResource();
        dpCreation.setEvent("DATA_PRODUCT_VERSION_CREATION");
        OdmPolicyEvaluationEventResource dpvCreation = new OdmPolicyEvaluationEventResource();
        dpvCreation.setEvent("DATA_PRODUCT_CREATION");
        policyResource.setEvaluationEvents(Lists.newArrayList(dpCreation, dpvCreation));


        if (!policyExists(policyClient, policyEngine, policyResource.getName())) {
            policyClient.createPolicy(policyResource);
        }
    }

    private Optional<OdmPolicyEngineResource> findPolicyEngine(OdmPolicyEngineClient policyEngineClient) {
        Page<OdmPolicyEngineResource> engines = policyEngineClient.getPolicyEngines(Pageable.ofSize(500), new OdmPolicyEngineSearchOptions());
        return engines.get().filter(engine -> policyEngineName.equals(engine.getName())).findFirst();
    }

    private OdmPolicyEngineResource createPolicyEngine(OdmPolicyEngineClient policyEngineClient) {
        OdmPolicyEngineResource policyEngine = new OdmPolicyEngineResource();
        policyEngine.setName(policyEngineName);
        policyEngine.setDisplayName(policyEngineName);
        policyEngine.setAdapterUrl(baseUrl);
        return policyEngineClient.createPolicyEngine(policyEngine);
    }

    private boolean policyExists(OdmPolicyClient policyClient, OdmPolicyEngineResource policyEngine, String policyName) {
        OdmPolicySearchOptions filter = new OdmPolicySearchOptions();
        filter.setPolicyEngineName(policyEngine.getName());
        filter.setName(policyName);

        return !policyClient.getPolicies(Pageable.ofSize(1), filter).isEmpty();
    }

}
