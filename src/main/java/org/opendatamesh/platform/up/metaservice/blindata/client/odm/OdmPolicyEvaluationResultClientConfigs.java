package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtils;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEvaluationResultResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEvaluationResultSearchOptions;
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
public class OdmPolicyEvaluationResultClientConfigs {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${odm.productPlane.policyService.address}")
    private String address;

    @Value("${odm.productPlane.policyService.active}")
    private boolean active;

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public OdmPolicyEvaluationResultClient policyEvaluationResultClient() {
        if (active) {
            return new OdmPolicyEvaluationResultClientImpl(
                    address,
                    new RestUtils(restTemplate)
            );
        } else {
            log.warn("ODM Policy Client is not enabled in the configuration.");
            return new OdmPolicyEvaluationResultClient() {
                @Override
                public Page<OdmPolicyEvaluationResultResource> getPolicyEvaluationResults(Pageable pageable, OdmPolicyEvaluationResultSearchOptions searchOptions) {
                    log.warn("getPolicyEvaluationResults called but policy client is disabled. Returning empty page.");
                    return Page.empty();
                }

                @Override
                public OdmPolicyEvaluationResultResource getPolicyEvaluationResult(Long id) {
                    log.warn("getPolicyEvaluationResult called but policy client is disabled. No policy evaluation result found for id {}.", id);
                    return null;
                }

                @Override
                public OdmPolicyEvaluationResultResource createPolicyEvaluationResult(OdmPolicyEvaluationResultResource policyEvaluationResult) {
                    log.warn("createPolicyEvaluationResult called but policy client is disabled. Policy evaluation result not created.");
                    return null;
                }

                @Override
                public OdmPolicyEvaluationResultResource modifyPolicyEvaluationResult(Long id, OdmPolicyEvaluationResultResource policyEvaluationResult) {
                    log.warn("modifyPolicyEvaluationResult called but policy client is disabled. No policy evaluation result modified for id {}.", id);
                    return null;
                }

                @Override
                public void deletePolicyEvaluationResult(Long id) {
                    log.warn("deletePolicyEvaluationResult called but policy client is disabled. No policy evaluation result deleted for id {}.", id);
                }
            };
        }
    }
}
