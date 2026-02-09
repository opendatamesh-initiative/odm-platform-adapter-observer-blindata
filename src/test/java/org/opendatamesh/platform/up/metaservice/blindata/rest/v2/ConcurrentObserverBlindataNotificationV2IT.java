package org.opendatamesh.platform.up.metaservice.blindata.rest.v2;

import com.google.common.io.Resources;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opendatamesh.platform.up.metaservice.blindata.AsyncTestConfig;
import org.opendatamesh.platform.up.metaservice.blindata.ObserverBlindataAppIT;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdStewardshipClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdUserClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmEventNotificationResourceV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmEventNotificationStatusV2;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@SpringBootTest(
        classes = {AsyncTestConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "blindata.enableAsync=true"
})
public class ConcurrentObserverBlindataNotificationV2IT extends ObserverBlindataAppIT {

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
