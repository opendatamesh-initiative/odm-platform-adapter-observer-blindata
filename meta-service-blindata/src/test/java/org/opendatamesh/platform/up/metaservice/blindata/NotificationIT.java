package org.opendatamesh.platform.up.metaservice.blindata;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.up.notification.api.resources.NotificationResource;
import org.opendatamesh.platform.up.notification.api.resources.NotificationStatus;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class NotificationIT extends MetaserviceAppIT {

    // ----------------------------------------
    // CREATE Notification
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testNotificationCreate_DataProductCreated() throws IOException {
        
        NotificationResource notificationResource = createNotification1();
        assertThat(notificationResource.getEvent().getType()).isEqualTo("DATA_PRODUCT_CREATED");
        assertThat(notificationResource.getStatus()).isEqualTo(NotificationStatus.UNPROCESSABLE);

    }

}
