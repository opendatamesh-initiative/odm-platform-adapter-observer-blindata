package org.opendatamesh.platform.up.metaservice.blindata;

import org.opendatamesh.platform.up.metaservice.blindata.client.odm.OdmEventNotificationClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    //Makes all @Async methods synchronous
    @Bean
    @Primary
    public TaskExecutor taskExecutor() {
        return new SyncTaskExecutor();
    }

    @Bean
    @Primary
    public OdmEventNotificationClient notificationClient() {
        return mock(OdmEventNotificationClient.class);
    }
}
