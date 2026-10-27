# Norm: Asynchronous execution

How the service runs work in the background, and on which kind of thread.

---

## Choosing the executor

| The task mostly… | Use | Threads |
| --- | --- | --- |
| Waits on I/O (HTTP calls, database, files, queues) | `AsyncExecutorService` | Virtual |
| Computes (parsing large payloads, hashing, compression, big in-memory transforms) | `PlatformThreadExecutorService` | Platform |
| Blocks inside `synchronized` or a native call (some JDBC drivers and SDK clients) | `PlatformThreadExecutorService` | Platform |

- **Default to `AsyncExecutorService`.** Most background work in these services is I/O.
- **Pick by what the task does, not by who calls it.** A task that fetches and then computes heavily hands the computation to `PlatformThreadExecutorService`.
- **Never create threads or executors by hand** (`new Thread`, `Executors.new…`). Both services run on executors the framework owns, sizes from configuration and shuts down.
- **The caller's MDC (correlation id) is copied into the task.** Do not copy it again.

## Limits

- **Virtual threads do not cap concurrency.** A burst can exhaust a connection pool or hit a remote rate limit. Cap it where the limit lives, inside the client or adapter for that system (a `Semaphore`, or the client's own pool), not in the executor.
- **Pinning (Java 21).** A virtual thread that blocks inside `synchronized` or a native call holds its carrier thread. Find it with `-Djdk.tracePinnedThreads=full`; move that work to `PlatformThreadExecutorService` or replace the lock with a `ReentrantLock`.
- **No per-thread caches.** `ThreadLocal` caches are rebuilt for every virtual-thread task.
- **Async tasks own their failures.** `execute` does not return a result or rethrow. The task logs or reports its own failure through a port (see [`USE_CASE_IMPLEMENTATION.md`](./USE_CASE_IMPLEMENTATION.md) when the module is present).

## Configuration

Executor sizing is configuration, never code (see [`CONFIGURATION.md`](./CONFIGURATION.md)). The keys are listed below.

---

**Spring:** both executors are beans in `AsyncExecutorConfig`, built from Spring Boot's executor builders. `AsyncExecutorService` runs on `applicationTaskExecutor`, a virtual-thread `SimpleAsyncTaskExecutor` that is also the default for `@Async` and Spring MVC async requests (`spring.task.execution.simple.concurrency-limit`, `spring.task.execution.simple.reject-tasks-when-limit-reached`). `PlatformThreadExecutorService` runs on `platformThreadTaskExecutor`, a `ThreadPoolTaskExecutor` (`spring.task.execution.pool.*`: `core-size`, `max-size`, `queue-capacity`, `keep-alive`; `spring.task.execution.shutdown.*`). Use `@Async(AsyncExecutorConfig.PLATFORM_THREAD_EXECUTOR)` to send an `@Async` method to the platform pool. `spring.threads.virtual.enabled` is independent: it moves Tomcat and `@Scheduled` to virtual threads.
