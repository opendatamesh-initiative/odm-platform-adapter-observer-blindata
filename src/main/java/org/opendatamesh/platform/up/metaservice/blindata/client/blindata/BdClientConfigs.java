package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.opendatamesh.platform.up.metaservice.blindata.configurations.BdDataProductConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BdClientConfigs {
    @Autowired
    private BdCredentials bdCredentials;
    @Autowired
    private BdDataProductConfig bdDataProductConfig;
    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public BdGovernancePolicyClient bdGovernancePolicyClient() {
        return new BdClientImpl(bdCredentials, bdDataProductConfig, restTemplate);
    }

    @Bean
    public BdGovernancePolicySuiteClient bdGovernancePolicySuiteClient() {
        return new BdClientImpl(bdCredentials, bdDataProductConfig, restTemplate);
    }

    @Bean
    public BdGovernancePolicyImplementationClient bdGovernancePolicyImplementationClient() {
        return new BdClientImpl(bdCredentials, bdDataProductConfig, restTemplate);
    }

    @Bean
    public BdDataProductClient bdDataProductClient() {
        return new BdClientImpl(bdCredentials, bdDataProductConfig, restTemplate);
    }

    @Bean
    public BdQualityClient bdQualityClient() {
        return new BdClientImpl(bdCredentials, bdDataProductConfig, restTemplate);
    }

    @Bean
    public BdIssueCampaignClient bdIssueCampaignClient() {
        return new BdClientImpl(bdCredentials, bdDataProductConfig, restTemplate);
    }

    @Bean
    public BdUserClient bdUserClient() {
        return new BdClientImpl(bdCredentials, bdDataProductConfig, restTemplate);
    }

    @Bean
    public BdStewardshipClient bdStewardshipClient() {
        return new BdClientImpl(bdCredentials, bdDataProductConfig, restTemplate);
    }

    @Bean
    public BdPolicyEvaluationResultClient bdPolicyEvaluationResultClient() {
        return new BdClientImpl(bdCredentials, bdDataProductConfig, restTemplate);
    }

    @Bean
    public BdSemanticLinkingClient bdSemanticLinkingClient() {
        return new BdClientImpl(bdCredentials, bdDataProductConfig, restTemplate);
    }

}
