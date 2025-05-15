package org.opendatamesh.platform.up.metaservice.blindata;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/*
* This test configuration should be used in tests that need a multithread environment
* This can be achieved by annotating the test class as follows:
*    @SpringBootTest(
*            classes = {AsyncTestConfig.class},
*            webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
*    )
* */
@TestConfiguration
public class AsyncTestConfig {
    
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("AsyncThread-");
        executor.initialize();
        return executor;
    }
} 