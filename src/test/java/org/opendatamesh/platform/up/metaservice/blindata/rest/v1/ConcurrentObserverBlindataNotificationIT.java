package org.opendatamesh.platform.up.metaservice.blindata.rest.v1;

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
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmEventNotificationStatus;
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
public class ConcurrentObserverBlindataNotificationIT extends ObserverBlindataAppIT {

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
    public void testConcurrentNotificationsProcessing() throws IOException, InterruptedException, java.util.concurrent.ExecutionException {
        // Mock BDDataProductClient behavior
        BDDataProductRes dataProduct = new BDDataProductRes();
        dataProduct.setName("Test Data Product");
        dataProduct.setIdentifier("test-data-product");
        dataProduct.setDescription("Test Description");
        dataProduct.setDomain("test-domain");
        dataProduct.setUuid("test-uuid");
        Mockito.when(bdDataProductClient.createDataProduct(Mockito.any())).thenReturn(dataProduct);
        Mockito.when(bdDataProductClient.getDataProduct(Mockito.anyString())).thenReturn(Optional.empty());

        // Load the base notification
        OdmEventNotificationResource baseNotification = mapper.readValue(
                Resources.toByteArray(getClass().getResource("dataProductCreation_eventNotification.json")),
                OdmEventNotificationResource.class
        );

        // Create multiple notifications with different IDs
        int numNotifications = 5;
        List<OdmEventNotificationResource> notifications = LongStream.range(0, numNotifications)
                .mapToObj(i -> {
                    OdmEventNotificationResource notification = new OdmEventNotificationResource(baseNotification);
                    notification.setId(i);
                    return notification;
                })
                .collect(Collectors.toList());

        // Prepare the countdown latch
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(numNotifications);
        ExecutorService executorService = Executors.newFixedThreadPool(numNotifications);

        // Create futures that will wait for the signal before executing
        List<CompletableFuture<ResponseEntity<OdmEventNotificationResource>>> futures = new ArrayList<>();

        for (OdmEventNotificationResource notification : notifications) {
            CompletableFuture<ResponseEntity<OdmEventNotificationResource>> future = CompletableFuture.supplyAsync(() -> {
                try {
                    startLatch.await(); // Wait for the signal
                    ResponseEntity<OdmEventNotificationResource> response = rest.postForEntity(
                            apiUrl(RoutesV1.CONSUME),
                            notification,
                            OdmEventNotificationResource.class
                    );
                    completionLatch.countDown(); // Signal that this request is complete
                    return response;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }, executorService);
            futures.add(future);
        }

        // Trigger all requests simultaneously
        startLatch.countDown();

        // Wait for all requests to complete with a timeout
        completionLatch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        // Verify all responses were successful and in PROCESSING state
        for (CompletableFuture<ResponseEntity<OdmEventNotificationResource>> future : futures) {
            ResponseEntity<OdmEventNotificationResource> response = future.get();
            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(response.getBody().getStatus()).hasToString(OdmEventNotificationStatus.PROCESSING.toString());
        }

        // Verify total number of calls with timeout
        Mockito.verify(bdDataProductClient, Mockito.timeout(5000).times(numNotifications)).createDataProduct(Mockito.any());

        // Verify no unexpected calls were made
        Mockito.verify(bdUserClient, Mockito.never()).getBlindataUser(Mockito.anyString());
        Mockito.verify(bdStewardshipClient, Mockito.never()).getRole(Mockito.anyString());
        Mockito.verify(bdStewardshipClient, Mockito.never()).createResponsibility(Mockito.any());
    }
} 