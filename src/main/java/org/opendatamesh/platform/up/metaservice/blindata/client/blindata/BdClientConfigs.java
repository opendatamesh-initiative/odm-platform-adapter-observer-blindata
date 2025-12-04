package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.opendatamesh.platform.up.metaservice.blindata.configurations.BdDataProductConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
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
    private RestTemplateBuilder restTemplateBuilder;

    private RestTemplate getRestTemplate() {
        return restTemplateBuilder.build();
    }

    @Bean
    public BdGovernancePolicyClient bdGovernancePolicyClient() {
        return new BdClientImpl(bdCredentials, bdDataProductConfig, getRestTemplate());
    }

    @Bean
    public BdGovernancePolicySuiteClient bdGovernancePolicySuiteClient() {
        return new BdClientImpl(bdCredentials, bdDataProductConfig, getRestTemplate());
    }

    @Bean
    public BdGovernancePolicyImplementationClient bdGovernancePolicyImplementationClient() {
        return new BdClientImpl(bdCredentials, bdDataProductConfig, getRestTemplate());
    }

    @Bean
    public BdDataProductClient bdDataProductClient() {
        return new BdClientImpl(bdCredentials, bdDataProductConfig, getRestTemplate());
    }

    @Bean
    public BdQualityClient bdQualityClient() {
        return new BdClientImpl(bdCredentials, bdDataProductConfig, getRestTemplate());
    }

    @Bean
    public BdIssueCampaignClient bdIssueCampaignClient() {
        return new BdClientImpl(bdCredentials, bdDataProductConfig, getRestTemplate());
    }

    @Bean
    public BdUserClient bdUserClient() {
        return new BdClientImpl(bdCredentials, bdDataProductConfig, getRestTemplate());
    }

    @Bean
    public BdStewardshipClient bdStewardshipClient() {
        return new BdClientImpl(bdCredentials, bdDataProductConfig, getRestTemplate());
    }

    @Bean
    public BdPolicyEvaluationResultClient bdPolicyEvaluationResultClient() {
        return new BdClientImpl(bdCredentials, bdDataProductConfig, getRestTemplate());
    }

    @Bean
    public BdSemanticLinkingClient bdSemanticLinkingClient() {
        return new BdClientImpl(bdCredentials, bdDataProductConfig, getRestTemplate());
    }

    @Bean
    public BdMarketplaceAccessRequestsUploadResultClient bdMarketplaceAccessRequestsUploadResultClient() {
        return new BdClientImpl(bdCredentials, bdDataProductConfig, getRestTemplate());
    }

    @Bean
    public BdSystemClient bdSystemClient() {
        return new BdClientImpl(bdCredentials, bdDataProductConfig, getRestTemplate());
    }
}
