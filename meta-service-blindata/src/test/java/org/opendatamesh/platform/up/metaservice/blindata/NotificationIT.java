package org.opendatamesh.platform.up.metaservice.blindata;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.up.notification.api.resources.ErrorResource;
import org.opendatamesh.platform.up.notification.api.resources.NotificationResource;
import org.opendatamesh.platform.up.notification.api.resources.NotificationStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class NotificationIT extends MetaserviceAppIT {

    // ----------------------------------------
    // CREATE Notification
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testNotificationCreate_DataProductCreated() throws IOException {
        
        NotificationResource notificationResource = createNotification1();
        assertThat(notificationResource.getEvent().getType()).isEqualTo("DATA_PRODUCT_CREATED");
        assertThat(notificationResource.getStatus()).isEqualTo(NotificationStatus.UNPROCESSABLE);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testNotificationCreate_DataProductDeleted() throws IOException {

        NotificationResource notificationResource = createNotification2();
        assertThat(notificationResource.getEvent().getType()).isEqualTo("DATA_PRODUCT_DELETED");
        assertThat(notificationResource.getStatus()).isEqualTo(NotificationStatus.UNPROCESSABLE);
    }

    // ----------------------------------------
    // READ One Notification
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadOneNotification() throws IOException {

        NotificationResource notificationResource = createNotification1();

        // TEST 1: Notification present
        ResponseEntity<NotificationResource> readNotificationResourceResponse = notificationClient.readOneNotification(notificationResource.getId());
        verifyResponseEntity(readNotificationResourceResponse, HttpStatus.OK, true);

        assertThat(notificationResource).isEqualTo(readNotificationResourceResponse.getBody());

        // TEST 2: Notification not present
        ResponseEntity<ErrorResource> readNotificationErrorResponse = notificationClient.readOneNotification(1000L);
        verifyResponseEntity(readNotificationResourceResponse, HttpStatus.OK, true);
        assertThat(readNotificationErrorResponse.getBody()).isNull();
    }

    // ----------------------------------------
    // SEARCH Notifications
    // ----------------------------------------
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSearchNotifications() throws IOException {

        NotificationResource notificationResource1 = createNotification1();
        NotificationResource notificationResource2 = createNotification2();
        assertThat(notificationResource1.getEvent().getType()).isEqualTo("DATA_PRODUCT_CREATED");
        assertThat(notificationResource1.getStatus()).isEqualTo(NotificationStatus.UNPROCESSABLE);
        assertThat(notificationResource2.getEvent().getType()).isEqualTo("DATA_PRODUCT_DELETED");
        assertThat(notificationResource2.getStatus()).isEqualTo(NotificationStatus.UNPROCESSABLE);


        // TEST 1: searchNotification without filters
        ResponseEntity<NotificationResource[]> readNotificationsResourceResponse = notificationClient.searchNotifications(null, null);

        verifyResponseEntity(readNotificationsResourceResponse, HttpStatus.OK, true);
        assertThat(readNotificationsResourceResponse.getBody().length).isEqualTo(2);

        // TODO searchNotifications method with filters (not implemented yet in the notification service)

        // TEST 2: searchNotification with eventType filter
//        readNotificationsResourceResponse = notificationClient.searchNotifications("DATA_PRODUCT_VERSION_CREATED", null);
//        assertThat(readNotificationsResourceResponse.getBody()).isNull();

        // TEST 3: searchNotification with notificationStatus filter
//        readNotificationsResourceResponse = notificationClient.searchNotifications(null,"PROCESSING");
//        assertThat(readNotificationsResourceResponse.getBody()).isNull();


    }

    // ----------------------------------------
    // DELETE Notification TODO change method name
    // ----------------------------------------
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDeleteNotification() throws IOException {

        NotificationResource notificationResource = createNotification1();

        ResponseEntity<NotificationResource> readNotificationsResourceResponse = notificationClient.readOneNotification(notificationResource.getId());

        verifyResponseEntity(readNotificationsResourceResponse, HttpStatus.OK, true);
        assertThat(notificationResource).isEqualTo(readNotificationsResourceResponse.getBody());

        // TEST 1: Verify the DELETE API response
        ResponseEntity<Void> deleteResponse = notificationClient.deleteNotification(notificationResource.getId());
        verifyResponseEntity(deleteResponse, HttpStatus.OK, false);

        // TEST 2: Verify that the Notification has been deleted
        readNotificationsResourceResponse = notificationClient.readOneNotification(notificationResource.getId());

        // The HTTP status of the response must be OK (200) but the body is null. TODO It will be changed to expect a 404
        verifyResponseEntity(readNotificationsResourceResponse, HttpStatus.OK, false);
        assertThat(readNotificationsResourceResponse.getBody()).isNull();

        // TEST 3: Trying to delete a Notification with an id that doesn't exist
        // The response is an internal server error for now because it doesn't find a Notification with that id. TODO It will be changed to expect a 404
        ResponseEntity<ErrorResource> deleteErrorResponse = notificationClient.deleteNotification(notificationResource.getId());
        verifyResponseEntity(deleteErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR, false);
    }

}
