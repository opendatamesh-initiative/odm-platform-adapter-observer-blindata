package org.opendatamesh.platform.up.metaservice.blindata.rest.v2;

import com.google.common.io.Resources;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opendatamesh.platform.up.metaservice.blindata.ObserverBlindataAppIT;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.events.DataProductDeletedEventContentResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmEventNotificationResourceV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmEventNotificationStatusV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

public class DataProductDeletionV2IT extends ObserverBlindataAppIT {

    @Autowired
    private BdDataProductClient bdDataProductClient;

    @BeforeEach
    public void resetMocks() {
        Mockito.reset(bdDataProductClient);
    }

    @Test
    public void testDataProductDeletedRemovesBlindataDataProduct() throws IOException {
        BDDataProductRes dataProduct = new BDDataProductRes();
        dataProduct.setUuid("test-uuid");
        dataProduct.setIdentifier("odm://domain.dp1");
        Mockito.when(bdDataProductClient.getDataProduct("odm://domain.dp1"))
                .thenReturn(Optional.of(dataProduct));

        OdmEventNotificationResourceV2 notificationResource = mapper.readValue(
                Resources.toByteArray(Objects.requireNonNull(
                        getClass().getResource("dataProductDeletion_eventNotification.json"))),
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

        Mockito.verify(bdDataProductClient, Mockito.times(1))
                .getDataProduct("odm://domain.dp1");
        Mockito.verify(bdDataProductClient, Mockito.times(1))
                .deleteDataProduct("test-uuid");
    }

    @Test
    public void testDataProductDeletedWithVersionStillDeletesBlindataDataProduct() throws IOException {
        BDDataProductRes dataProduct = new BDDataProductRes();
        dataProduct.setUuid("test-uuid");
        dataProduct.setIdentifier("odm://domain.dp1");
        Mockito.when(bdDataProductClient.getDataProduct("odm://domain.dp1"))
                .thenReturn(Optional.of(dataProduct));

        OdmEventNotificationResourceV2 notificationResource = mapper.readValue(
                Resources.toByteArray(Objects.requireNonNull(
                        getClass().getResource("dataProductDeletion_withVersion_eventNotification.json"))),
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

        Mockito.verify(bdDataProductClient, Mockito.times(1))
                .getDataProduct("odm://domain.dp1");
        Mockito.verify(bdDataProductClient, Mockito.times(1))
                .deleteDataProduct("test-uuid");
    }

    @Test
    public void testDataProductDeletedEventContentResourceDoesNotExposeDataProductVersionField() {
        Field[] fields = DataProductDeletedEventContentResource.class.getDeclaredFields();
        boolean hasDataProductVersionField = false;
        for (Field field : fields) {
            if ("dataProductVersion".equals(field.getName())) {
                hasDataProductVersionField = true;
                break;
            }
        }
        Assertions.assertThat(hasDataProductVersionField)
                .as("DataProductDeletedEventContentResource should not expose a dataProductVersion field")
                .isFalse();
    }
}
