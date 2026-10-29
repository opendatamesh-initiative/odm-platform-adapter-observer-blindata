package org.opendatamesh.platform.up.metaservice.blindata;

import org.springframework.core.task.SyncTaskExecutor;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test double for the executor behind @Async, registered by {@link TestConfig}.
 * By default it runs tasks on the caller's thread, so integration tests can assert as soon as a request returns.
 * A test that needs real concurrency calls {@link #runOn(Executor)} and, when it ends, {@link #reset(Duration)},
 * which waits for the tasks still running so they cannot reach the next test's mocks.
 */
public class TestAsyncExecutor implements Executor {

    private final Executor synchronous = new SyncTaskExecutor();
    private final AtomicInteger runningTasks = new AtomicInteger();
    private volatile Executor delegate = synchronous;

    public void runOn(Executor executor) {
        this.delegate = executor;
    }

    public void reset(Duration timeout) throws InterruptedException, TimeoutException {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (runningTasks.get() > 0) {
            if (System.nanoTime() > deadline) {
                throw new TimeoutException(runningTasks.get() + " @Async tasks still running after " + timeout);
            }
            Thread.sleep(10);
        }
        this.delegate = synchronous;
    }

    @Override
    public void execute(Runnable task) {
        runningTasks.incrementAndGet();
        try {
            delegate.execute(() -> {
                try {
                    task.run();
                } finally {
                    runningTasks.decrementAndGet();
                }
            });
        } catch (RuntimeException e) {
            runningTasks.decrementAndGet();
            throw e;
        }
    }
}
