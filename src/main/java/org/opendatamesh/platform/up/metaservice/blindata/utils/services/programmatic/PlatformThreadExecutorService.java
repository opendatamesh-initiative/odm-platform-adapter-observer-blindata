package org.opendatamesh.platform.up.metaservice.blindata.utils.services.programmatic;

/**
 * Runs work in the background on the Spring-managed platform-thread pool
 * ({@link AsyncExecutorConfig#PLATFORM_THREAD_EXECUTOR}).
 * Use it for CPU-heavy work, or code that blocks inside {@code synchronized} or native calls.
 * For blocking I/O, use {@link AsyncExecutorService}.
 * The caller's MDC (correlation id) is copied into the task.
 * Configured with {@code spring.task.execution.pool.*}.
 */
public interface PlatformThreadExecutorService {

    void execute(Runnable runnable);
}
