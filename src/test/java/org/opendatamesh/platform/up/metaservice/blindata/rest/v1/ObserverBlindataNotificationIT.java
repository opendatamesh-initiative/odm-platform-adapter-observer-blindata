package org.opendatamesh.platform.up.metaservice.blindata.rest.v1;

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
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmEventNotificationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Optional;

public class ObserverBlindataNotificationIT extends ObserverBlindataAppIT {

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
    public void testDataProductCreated() throws IOException {
        // Mock BDDataProductClient behavior
        BDDataProductRes dataProduct = new BDDataProductRes();
        dataProduct.setName("Test Data Product");
        dataProduct.setIdentifier("test-data-product");
        dataProduct.setDescription("Test Description");
        dataProduct.setDomain("test-domain");
        dataProduct.setUuid("test-uuid");
        Mockito.when(bdDataProductClient.createDataProduct(Mockito.any())).thenReturn(dataProduct);
        Mockito.when(bdDataProductClient.getDataProduct(Mockito.anyString())).thenReturn(Optional.empty());

        OdmEventNotificationResource notificationResource = mapper.readValue(
                Resources.toByteArray(getClass().getResource("dataProductCreation_eventNotification.json")),
                OdmEventNotificationResource.class
        );

        ResponseEntity<OdmEventNotificationResource> resourceResponseEntity = rest.postForEntity(
                apiUrl(RoutesV1.CONSUME),
                notificationResource,
                OdmEventNotificationResource.class);

        Assertions.assertThat(resourceResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        //The notification status will be updated asynchronously after all registered event handlers have been executed
        Assertions.assertThat(resourceResponseEntity.getBody().getStatus()).hasToString(OdmEventNotificationStatus.PROCESSING.toString());

        // Verify that the data product was created in Blindata
        Mockito.verify(bdDataProductClient, Mockito.times(1)).createDataProduct(Mockito.any());

        Mockito.verify(bdUserClient, Mockito.never()).getBlindataUser(Mockito.anyString());
        Mockito.verify(bdStewardshipClient, Mockito.never()).getRole(Mockito.anyString());
        Mockito.verify(bdStewardshipClient, Mockito.never()).createResponsibility(Mockito.any());
    }
}
