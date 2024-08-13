package org.opendatamesh.platform.up.metaservice.blindata.services;

import org.opendatamesh.platform.pp.notification.api.clients.EventNotificationClient;
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
    EventNotificationClient notificationClient() {
        return mock(EventNotificationClient.class);
    }
}
