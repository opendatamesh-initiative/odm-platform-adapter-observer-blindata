package org.opendatamesh.platform.up.metaservice.blindata.rest.v1;

import com.google.common.io.Resources;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.up.metaservice.blindata.ObserverBlindataAppIT;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

class ObserverBlindataNotificationIT extends ObserverBlindataAppIT {
    @Test
    public void testNotificationEvent() throws IOException {
        OdmEventNotificationResource notificationResource = mapper.readValue(
                Resources.toByteArray(getClass().getResource("dataProductCreation_eventNotification.json")),
                OdmEventNotificationResource.class
        );

        ResponseEntity<OdmEventNotificationResource> resourceResponseEntity = rest.postForEntity(
                apiUrl(RoutesV1.CONSUME),
                notificationResource,
                OdmEventNotificationResource.class);

        Assertions.assertThat(resourceResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        //The notification status will be updated asynchronously after all registered event handlers have been executed
        Assertions.assertThat(resourceResponseEntity.getBody().getStatus()).hasToString(OdmEventNotificationStatus.PROCESSING.toString());
    }
}
