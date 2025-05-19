package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtilsFactory;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEngineResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEngineSearchOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OdmPolicyEngineClientConfigs {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${odm.productPlane.policyService.address}")
    private String policyServiceBaseUrl;

    @Value("${odm.productPlane.policyService.active}")
    private boolean active;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private RestTemplate getRestTemplate() {
        return restTemplateBuilder.build();
    }

    @Bean
    public OdmPolicyEngineClient policyEngineClient() {
        if (active) {
            return new OdmPolicyEngineClientImpl(RestUtilsFactory.getRestUtils(getRestTemplate()), policyServiceBaseUrl);
        } else {
            log.warn("ODM Policy Engine Client is not enabled in the configuration.");
            return new OdmPolicyEngineClient() {
                @Override
                public Page<OdmPolicyEngineResource> getPolicyEngines(Pageable pageable, OdmPolicyEngineSearchOptions searchOptions) {
                    log.warn("getPolicyEngines called but policy engine client is disabled. Returning empty page.");
                    return Page.empty();
                }

                @Override
                public OdmPolicyEngineResource getPolicyEngine(Long id) {
                    log.warn("getPolicyEngine called but policy engine client is disabled. No policy engine found for id {}.", id);
                    return null;
                }

                @Override
                public OdmPolicyEngineResource createPolicyEngine(OdmPolicyEngineResource policyEngineResource) {
                    log.warn("createPolicyEngine called but policy engine client is disabled. Policy engine not created.");
                    return null;
                }

                @Override
                public OdmPolicyEngineResource modifyPolicyEngine(Long id, OdmPolicyEngineResource policyEngine) {
                    log.warn("modifyPolicyEngine called but policy engine client is disabled. No policy engine modified for id {}.", id);
                    return null;
                }

                @Override
                public void deletePolicyEngine(Long id) {
                    log.warn("deletePolicyEngine called but policy engine client is disabled. No policy engine deleted for id {}.", id);
                }
            };
        }
    }
}
