package org.opendatamesh.platform.up.metaservice.blindata.utils.services.programmatic;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class PlatformThreadExecutorServiceImpl implements PlatformThreadExecutorService {

    private final TaskExecutor executor;

    public PlatformThreadExecutorServiceImpl(@Qualifier(AsyncExecutorConfig.PLATFORM_THREAD_EXECUTOR) TaskExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }
}
