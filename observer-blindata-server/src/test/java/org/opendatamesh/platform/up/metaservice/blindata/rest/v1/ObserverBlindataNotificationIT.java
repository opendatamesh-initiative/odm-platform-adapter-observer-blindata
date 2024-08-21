package org.opendatamesh.platform.up.metaservice.blindata.rest.v1;

import com.google.common.io.Resources;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.ObserverBlindataAppIT;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.OBEventNotificationStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public class ObserverBlindataNotificationIT extends ObserverBlindataAppIT {
    @Test
    public void testNotificationEvent() throws IOException {
        EventNotificationResource notificationResource = mapper.readValue(
                Resources.toByteArray(getClass().getResource("dataProductCreation_eventNotification.json")),
                EventNotificationResource.class
        );

        ResponseEntity<EventNotificationResource> resourceResponseEntity = rest.postForEntity(
                apiUrl(RoutesV1.CONSUME),
                notificationResource,
                EventNotificationResource.class);

        Assertions.assertThat(resourceResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        //The notification status will be updated asynchronously after all registered event handlers have been executed
        Assertions.assertThat(resourceResponseEntity.getBody().getStatus()).hasToString(OBEventNotificationStatus.PROCESSING.toString());
    }
}
