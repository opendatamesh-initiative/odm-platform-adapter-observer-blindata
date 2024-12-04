package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BDClientConfigs {
    @Autowired
    private BDCredentials bdCredentials;
    @Autowired
    private RestTemplate restTemplate;

    @Bean
    BDDataProductClient bdDataProductClient() {
        return new BDClientImpl(bdCredentials, restTemplate);
    }

    @Bean
    BDUserClient bdUserClient() {
        return new BDClientImpl(bdCredentials, restTemplate);
    }

    @Bean
    BDStewardshipClient bdStewardshipClient() {
        return new BDClientImpl(bdCredentials, restTemplate);
    }

    @Bean
    BDPolicyEvaluationResultClient bdPolicyEvaluationResultClient() {
        return new BDClientImpl(bdCredentials, restTemplate);
    }

    @Bean
    BDSemanticLinkingClient bdSemanticLinkingClient() {
        return new BDClientImpl(bdCredentials, restTemplate);
    }

}
