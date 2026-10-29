package org.opendatamesh.platform.up.metaservice.blindata.utils.services.programmatic;

/**
 * Runs work in the background on the Spring-managed virtual-thread executor
 * ({@link AsyncExecutorConfig#VIRTUAL_THREAD_EXECUTOR}, also the default {@code @Async} executor).
 * Use it for blocking I/O: HTTP calls, database access, file reads.
 * For CPU-heavy work, or code that blocks inside {@code synchronized} or native calls,
 * use {@link PlatformThreadExecutorService}.
 * The caller's MDC (correlation id) is copied into the task.
 * Configured with {@code spring.task.execution.simple.*}.
 */
public interface AsyncExecutorService {

    void execute(Runnable runnable);
}
