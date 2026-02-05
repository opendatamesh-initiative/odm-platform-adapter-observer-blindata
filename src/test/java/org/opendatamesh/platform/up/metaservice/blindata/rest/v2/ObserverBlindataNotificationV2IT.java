package org.opendatamesh.platform.up.metaservice.blindata.rest.v2;

import com.google.common.io.Resources;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opendatamesh.platform.up.metaservice.blindata.ObserverBlindataAppIT;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdStewardshipClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdUserClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmEventNotificationResourceV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmEventNotificationStatusV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Optional;

public class ObserverBlindataNotificationV2IT extends ObserverBlindataAppIT {

    @Autowired
    private BdDataProductClient bdDataProductClient;

    @Autowired
    private BdUserClient bdUserClient;

    @Autowired
    private BdStewardshipClient bdStewardshipClient;

    @BeforeEach
    public void resetMocks() {
        Mockito.reset(bdDataProductClient, bdUserClient, bdStewardshipClient);
    }

    @Test
    public void testDataProductInitialized() throws IOException {
        // Mock BDDataProductClient behavior
        BDDataProductRes dataProduct = new BDDataProductRes();
        dataProduct.setName("Test Data Product");
        dataProduct.setIdentifier("test-data-product");
        dataProduct.setDescription("Test Description");
        dataProduct.setDomain("test-domain");
        dataProduct.setUuid("test-uuid");
        Mockito.when(bdDataProductClient.createDataProduct(Mockito.any())).thenReturn(dataProduct);
        Mockito.when(bdDataProductClient.getDataProduct(Mockito.anyString())).thenReturn(Optional.empty());

        OdmEventNotificationResourceV2 notificationResource = mapper.readValue(
                Resources.toByteArray(getClass().getResource("dataProductInitialized_eventNotification.json")),
                OdmEventNotificationResourceV2.class
        );

        ResponseEntity<OdmEventNotificationResourceV2> resourceResponseEntity = rest.postForEntity(
                apiUrlFromString(RoutesV2.CONSUME.getPath()),
                notificationResource,
                OdmEventNotificationResourceV2.class);

        Assertions.assertThat(resourceResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(resourceResponseEntity.getBody().getStatus()).hasToString(OdmEventNotificationStatusV2.PROCESSING.toString());

        Mockito.verify(bdDataProductClient, Mockito.times(1)).createDataProduct(Mockito.any());

        Mockito.verify(bdUserClient, Mockito.never()).getBlindataUser(Mockito.anyString());
        Mockito.verify(bdStewardshipClient, Mockito.never()).getRole(Mockito.anyString());
        Mockito.verify(bdStewardshipClient, Mockito.never()).createResponsibility(Mockito.any());
    }
}
