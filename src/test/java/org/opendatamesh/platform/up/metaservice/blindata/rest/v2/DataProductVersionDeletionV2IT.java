package org.opendatamesh.platform.up.metaservice.blindata.rest.v2;

import com.google.common.io.Resources;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opendatamesh.platform.up.metaservice.blindata.ObserverBlindataAppIT;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmEventNotificationResourceV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmEventNotificationStatusV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Objects;

public class DataProductVersionDeletionV2IT extends ObserverBlindataAppIT {

    @Autowired
    private BdDataProductClient bdDataProductClient;

    @BeforeEach
    public void resetMocks() {
        Mockito.reset(bdDataProductClient);
    }

    @Test
    public void testDataProductVersionDeletedIsNoOpOnBlindata() throws IOException {
        OdmEventNotificationResourceV2 notificationResource = mapper.readValue(
                Resources.toByteArray(Objects.requireNonNull(
                        getClass().getResource("dataProductVersionDeletion_eventNotification.json"))),
                OdmEventNotificationResourceV2.class
        );

        ResponseEntity<OdmEventNotificationResourceV2> response = rest.postForEntity(
                apiUrlFromString(RoutesV2.CONSUME.getPath()),
                notificationResource,
                OdmEventNotificationResourceV2.class
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getStatus())
                .hasToString(OdmEventNotificationStatusV2.PROCESSING.toString());

        Mockito.verify(bdDataProductClient, Mockito.never())
                .deleteDataProduct(Mockito.anyString());
        Mockito.verify(bdDataProductClient, Mockito.never())
                .createDataProduct(Mockito.any());
        Mockito.verify(bdDataProductClient, Mockito.never())
                .patchDataProduct(Mockito.any());
        Mockito.verify(bdDataProductClient, Mockito.never())
                .putDataProduct(Mockito.any());
    }

    @Test
    public void testDataProductVersionDeletedDoesNotTriggerDataProductRemoval() throws IOException {
        OdmEventNotificationResourceV2 notificationResource = mapper.readValue(
                Resources.toByteArray(Objects.requireNonNull(
                        getClass().getResource("dataProductVersionDeletion_eventNotification.json"))),
                OdmEventNotificationResourceV2.class
        );

        ResponseEntity<OdmEventNotificationResourceV2> response = rest.postForEntity(
                apiUrlFromString(RoutesV2.CONSUME.getPath()),
                notificationResource,
                OdmEventNotificationResourceV2.class
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getStatus())
                .hasToString(OdmEventNotificationStatusV2.PROCESSING.toString());

        Mockito.verify(bdDataProductClient, Mockito.never())
                .deleteDataProduct(Mockito.anyString());
    }

    @Test
    public void testDeletingOneVersionDoesNotAffectBlindataDataProduct() throws IOException {
        OdmEventNotificationResourceV2 notificationResource = mapper.readValue(
                Resources.toByteArray(Objects.requireNonNull(
                        getClass().getResource("dataProductVersionDeletion_multiVersion_eventNotification.json"))),
                OdmEventNotificationResourceV2.class
        );

        ResponseEntity<OdmEventNotificationResourceV2> response = rest.postForEntity(
                apiUrlFromString(RoutesV2.CONSUME.getPath()),
                notificationResource,
                OdmEventNotificationResourceV2.class
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getStatus())
                .hasToString(OdmEventNotificationStatusV2.PROCESSING.toString());

        Mockito.verify(bdDataProductClient, Mockito.never())
                .deleteDataProduct(Mockito.anyString());
        Mockito.verify(bdDataProductClient, Mockito.never())
                .createDataProduct(Mockito.any());
        Mockito.verify(bdDataProductClient, Mockito.never())
                .patchDataProduct(Mockito.any());
        Mockito.verify(bdDataProductClient, Mockito.never())
                .putDataProduct(Mockito.any());
    }
}
