package org.opendatamesh.platform.up.metaservice.blindata.utils.services.programmatic;

import org.springframework.boot.task.SimpleAsyncTaskExecutorBuilder;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Two executors built from Spring Boot's auto-configured builders, so {@code spring.task.execution.*} applies.
 * The virtual-thread executor replaces Boot's {@code applicationTaskExecutor} and is the default for {@code @Async}.
 */
@Configuration(proxyBeanMethods = false)
public class AsyncExecutorConfig {

    public static final String VIRTUAL_THREAD_EXECUTOR = "applicationTaskExecutor";
    public static final String PLATFORM_THREAD_EXECUTOR = "platformThreadTaskExecutor";

    @Bean(name = {VIRTUAL_THREAD_EXECUTOR, "taskExecutor"})
    @Primary
    public SimpleAsyncTaskExecutor virtualThreadTaskExecutor(SimpleAsyncTaskExecutorBuilder builder) {
        return builder
                .virtualThreads(true)
                .threadNamePrefix("virtual-")
                .taskDecorator(new MdcTaskDecorator())
                .build();
    }

    @Bean(name = PLATFORM_THREAD_EXECUTOR)
    public ThreadPoolTaskExecutor platformThreadTaskExecutor(ThreadPoolTaskExecutorBuilder builder) {
        return builder
                .threadNamePrefix("platform-")
                .taskDecorator(new MdcTaskDecorator())
                .build();
    }
}
