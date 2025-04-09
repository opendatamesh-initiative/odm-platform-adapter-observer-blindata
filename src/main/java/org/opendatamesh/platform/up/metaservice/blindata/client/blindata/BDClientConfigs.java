package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BDClientConfigs {
    @Autowired
    private BDCredentials bdCredentials;
    @Autowired
    private BDDataProductClientConfig bdDataProductClientConfig;
    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public BDDataProductClient bdDataProductClient() {
        return new BDClientImpl(bdCredentials, bdDataProductClientConfig, restTemplate);
    }

    @Bean
    public BDUserClient bdUserClient() {
        return new BDClientImpl(bdCredentials, bdDataProductClientConfig, restTemplate);
    }

    @Bean
    public BDStewardshipClient bdStewardshipClient() {
        return new BDClientImpl(bdCredentials, bdDataProductClientConfig, restTemplate);
    }

    @Bean
    public BDPolicyEvaluationResultClient bdPolicyEvaluationResultClient() {
        return new BDClientImpl(bdCredentials, bdDataProductClientConfig, restTemplate);
    }

    @Bean
    public BDSemanticLinkingClient bdSemanticLinkingClient() {
        return new BDClientImpl(bdCredentials, bdDataProductClientConfig, restTemplate);
    }

}
