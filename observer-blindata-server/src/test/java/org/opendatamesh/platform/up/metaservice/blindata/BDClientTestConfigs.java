package org.opendatamesh.platform.up.metaservice.blindata;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDPolicyEvaluationResultClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDStewardshipClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDUserClient;
import org.opendatamesh.platform.up.metaservice.blindata.mocks.BDClientMock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class BDClientTestConfigs {
    @Bean
    BDDataProductClient bdDataProductClient() {
        return new BDClientMock();
    }

    @Bean
    BDUserClient bdUserClient() {
        return new BDClientMock();
    }

    @Bean
    BDStewardshipClient bdStewardshipClient() {
        return new BDClientMock();
    }

    @Bean
    BDPolicyEvaluationResultClient bdPolicyEvaluationResultClient() {
        return new BDClientMock();
    }
}
