package org.opendatamesh.platform.up.metaservice.blindata.rest.v2;

import com.google.common.io.Resources;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opendatamesh.platform.up.metaservice.blindata.ObserverBlindataAppIT;
import org.opendatamesh.platform.up.metaservice.blindata.TestAsyncExecutor;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdStewardshipClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdUserClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmEventNotificationResourceV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmEventNotificationStatusV2;
import org.opendatamesh.platform.up.metaservice.blindata.utils.services.programmatic.AsyncExecutorConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/*
 * This test runs @Async methods on the application's virtual-thread executor, so notifications are processed
 * concurrently. TestConfig keeps @Async synchronous for the other ITs; this test switches it and switches it back.
 */
public class ConcurrentObserverBlindataNotificationV2IT extends ObserverBlindataAppIT {

    @Autowired
    private BdDataProductClient bdDataProductClient;

    @Autowired
    private BdUserClient bdUserClient;

    @Autowired
    private BdStewardshipClient bdStewardshipClient;

    @Autowired
    private TestAsyncExecutor testAsyncExecutor;

    @Autowired
    @Qualifier(AsyncExecutorConfig.VIRTUAL_THREAD_EXECUTOR)
    private TaskExecutor applicationTaskExecutor;

    @BeforeEach
    public void resetMocks() {
        Mockito.reset(bdDataProductClient, bdUserClient, bdStewardshipClient);
        testAsyncExecutor.runOn(applicationTaskExecutor);
    }

    @AfterEach
    public void restoreSynchronousAsync() throws InterruptedException, TimeoutException {
        testAsyncExecutor.reset(Duration.ofSeconds(30));
    }

    @Test
    public void testConcurrentNotificationsProcessing() throws IOException, InterruptedException, ExecutionException {
        BDDataProductRes dataProduct = new BDDataProductRes();
        dataProduct.setName("Test Data Product");
        dataProduct.setIdentifier("test-data-product");
        dataProduct.setDescription("Test Description");
        dataProduct.setDomain("test-domain");
        dataProduct.setUuid("test-uuid");
        Mockito.when(bdDataProductClient.createDataProduct(Mockito.any())).thenReturn(dataProduct);
        Mockito.when(bdDataProductClient.getDataProduct(Mockito.anyString())).thenReturn(Optional.empty());

        OdmEventNotificationResourceV2 baseNotification = mapper.readValue(
                Resources.toByteArray(getClass().getResource("dataProductInitialized_eventNotification.json")),
                OdmEventNotificationResourceV2.class
        );

        int numNotifications = 5;
        List<OdmEventNotificationResourceV2> notifications = LongStream.range(0, numNotifications)
                .mapToObj(i -> {
                    OdmEventNotificationResourceV2 notification = new OdmEventNotificationResourceV2(baseNotification);
                    notification.setSequenceId(i);
                    return notification;
                })
                .collect(Collectors.toList());

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(numNotifications);
        ExecutorService executorService = Executors.newFixedThreadPool(numNotifications);

        List<CompletableFuture<ResponseEntity<OdmEventNotificationResourceV2>>> futures = new ArrayList<>();

        for (OdmEventNotificationResourceV2 notification : notifications) {
            CompletableFuture<ResponseEntity<OdmEventNotificationResourceV2>> future = CompletableFuture.supplyAsync(() -> {
                try {
                    startLatch.await();
                    ResponseEntity<OdmEventNotificationResourceV2> response = rest.postForEntity(
                            apiUrlFromString(RoutesV2.CONSUME.getPath()),
                            notification,
                            OdmEventNotificationResourceV2.class
                    );
                    completionLatch.countDown();
                    return response;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }, executorService);
            futures.add(future);
        }

        startLatch.countDown();

        completionLatch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        for (CompletableFuture<ResponseEntity<OdmEventNotificationResourceV2>> future : futures) {
            ResponseEntity<OdmEventNotificationResourceV2> response = future.get();
            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(response.getBody().getStatus()).hasToString(OdmEventNotificationStatusV2.PROCESSING.toString());
        }

        Mockito.verify(bdDataProductClient, Mockito.timeout(5000).times(numNotifications)).createDataProduct(Mockito.any());

        Mockito.verify(bdUserClient, Mockito.never()).getBlindataUser(Mockito.anyString());
        Mockito.verify(bdStewardshipClient, Mockito.never()).getRole(Mockito.anyString());
        Mockito.verify(bdStewardshipClient, Mockito.never()).createResponsibility(Mockito.any());
    }
}
