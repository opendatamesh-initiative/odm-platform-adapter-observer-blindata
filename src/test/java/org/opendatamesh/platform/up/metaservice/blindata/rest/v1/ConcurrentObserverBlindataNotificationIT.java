package org.opendatamesh.platform.up.metaservice.blindata.rest.v1;

import com.google.common.io.Resources;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.opendatamesh.platform.up.metaservice.blindata.AsyncTestConfig;
import org.opendatamesh.platform.up.metaservice.blindata.ObserverBlindataAppIT;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdStewardshipClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdUserClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationStatus;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@SpringBootTest(
        classes = {AsyncTestConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = {
        "blindata.enableAsync=true"
})
public class ConcurrentObserverBlindataNotificationIT extends ObserverBlindataAppIT {

    @MockBean
    private BdDataProductClient bdDataProductClient;

    @MockBean
    private BdUserClient bdUserClient;

    @MockBean
    private BdStewardshipClient bdStewardshipClient;

    @BeforeEach
    public void resetMocks() {
        Mockito.reset(bdDataProductClient, bdUserClient, bdStewardshipClient);
    }

    @Test
    public void testConcurrentNotificationsProcessing() throws IOException, InterruptedException, java.util.concurrent.ExecutionException {
        // Store thread names for verification
        Set<String> threadNames = ConcurrentHashMap.newKeySet();
        String mainThreadName = Thread.currentThread().getName();
        threadNames.add(mainThreadName);

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
        ExecutorService executorService = Executors.newFixedThreadPool(numNotifications);

        // Create futures that will wait for the signal before executing
        List<CompletableFuture<ResponseEntity<OdmEventNotificationResource>>> futures = new ArrayList<>();

        for (OdmEventNotificationResource notification : notifications) {
            CompletableFuture<ResponseEntity<OdmEventNotificationResource>> future = CompletableFuture.supplyAsync(() -> {
                try {
                    // Record the thread name
                    threadNames.add(Thread.currentThread().getName());

                    startLatch.await(); // Wait for the signal
                    return rest.postForEntity(
                            apiUrl(RoutesV1.CONSUME),
                            notification,
                            OdmEventNotificationResource.class
                    );
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }, executorService);
            futures.add(future);
        }

        // Trigger all requests simultaneously
        startLatch.countDown();

        // Wait for all requests to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        // Verify all responses were successful and in PROCESSING state
        for (CompletableFuture<ResponseEntity<OdmEventNotificationResource>> future : futures) {
            ResponseEntity<OdmEventNotificationResource> response = future.get();
            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(response.getBody().getStatus()).hasToString(OdmEventNotificationStatus.PROCESSING.toString());
        }

        // Verify sequential processing using Mockito's InOrder
        InOrder inOrder = Mockito.inOrder(bdDataProductClient);

        // Verify that createDataProduct was called exactly numNotifications times in sequence
        for (int i = 0; i < numNotifications; i++) {
            inOrder.verify(bdDataProductClient, Mockito.timeout(5000)).createDataProduct(Mockito.any());
        }

        // Verify total number of calls
        Mockito.verify(bdDataProductClient, Mockito.times(numNotifications)).createDataProduct(Mockito.any());

        // Verify no unexpected calls were made
        Mockito.verify(bdUserClient, Mockito.never()).getBlindataUser(Mockito.anyString());
        Mockito.verify(bdStewardshipClient, Mockito.never()).getRole(Mockito.anyString());
        Mockito.verify(bdStewardshipClient, Mockito.never()).createResponsibility(Mockito.any());

        // Verify that we used multiple threads
        Assertions.assertThat(threadNames)
                .as("Expected multiple threads to be used, but only found: " + threadNames)
                .hasSizeGreaterThan(1);

        // Verify that worker threads are different from main thread
        threadNames.remove(mainThreadName);
        Assertions.assertThat(threadNames)
                .as("Expected all worker threads to be named pool-1-thread-*, but found: " + threadNames)
                .allMatch(name -> name.startsWith("pool-1-thread-"));

        // Verify that we have the expected number of worker threads
        Assertions.assertThat(threadNames)
                .as("Expected " + numNotifications + " worker threads, but found: " + threadNames.size())
                .hasSize(numNotifications);
    }
} 