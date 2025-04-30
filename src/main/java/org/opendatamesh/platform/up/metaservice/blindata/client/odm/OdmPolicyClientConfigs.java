package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtilsFactory;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicySearchOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OdmPolicyClientConfigs {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${odm.productPlane.policyService.address}")
    private String policyServiceBaseUrl;

    @Value("${odm.productPlane.policyService.active}")
    private boolean active;

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public OdmPolicyClient odmPolicyClient() {
        if (active) {
            return new OdmPolicyClientImpl(policyServiceBaseUrl, RestUtilsFactory.getRestUtils(restTemplate));
        } else {
            log.warn("ODM Policy Client is not enabled in the configuration.");
            return new OdmPolicyClient() {
                @Override
                public Page<OdmPolicyResource> getPolicies(Pageable pageable, OdmPolicySearchOptions searchOptions) {
                    log.warn("getPolicies called but policy client is disabled. Returning empty page.");
                    return Page.empty();
                }

                @Override
                public OdmPolicyResource getPolicy(Long id) {
                    log.warn("getPolicy called but policy client is disabled. No policy found for id {}.", id);
                    return null;
                }

                @Override
                public OdmPolicyResource getPolicyVersion(Long versionId) {
                    log.warn("getPolicyVersion called but policy client is disabled. No policy version found for versionId {}.", versionId);
                    return null;
                }

                @Override
                public OdmPolicyResource createPolicy(OdmPolicyResource policy) {
                    log.warn("createPolicy called but policy client is disabled. Policy not created.");
                    return null;
                }

                @Override
                public OdmPolicyResource modifyPolicy(Long id, OdmPolicyResource policy) {
                    log.warn("modifyPolicy called but policy client is disabled. No policy modified for id {}.", id);
                    return null;
                }

                @Override
                public void deletePolicy(Long id) {
                    log.warn("deletePolicy called but policy client is disabled. No policy deleted for id {}.", id);
                }
            };
        }
    }
}
