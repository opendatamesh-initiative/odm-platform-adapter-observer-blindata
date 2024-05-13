package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClientImpl;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyEvaluationResultClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.odm.proxies.OdmPolicyProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OdmPolicyClientConfigs {
    @Value("${odm.productPlane.policyService.address}")
    private String address;

    @Value("${odm.productPlane.policyService.active}")
    private boolean active;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    PolicyEvaluationResultClient policyEvaluationResultClient() {
        if (active) {
            return new OdmPolicyProxy(new PolicyClientImpl(
                    address,
                    objectMapper
            ));
        } else {
            log.warn("ODM Policy Client is not enabled in the configuration.");
            return new OdmPolicyProxy(null);
        }
    }

}
