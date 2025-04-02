package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtils;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationSearchOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OdmEventNotificationClientConfigs {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${odm.productPlane.notificationService.address}")
    private String address;

    @Value("${odm.productPlane.notificationService.active}")
    private boolean active;

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public OdmEventNotificationClient eventNotificationClient() {
        if (active) {
            return new OdmEventNotificationClientImpl(address, new RestUtils(restTemplate));
        } else {
            log.warn("ODM Event Notification Client is not enabled in the configuration.");
            return new OdmEventNotificationClient() {
                @Override
                public OdmEventNotificationResource updateEventNotification(Long notificationId, OdmEventNotificationResource eventNotificationResource) {
                    log.warn("updateEventNotification called but notification client is disabled. No update performed for notificationId {}.", notificationId);
                    return null;
                }

                @Override
                public OdmEventNotificationResource readOneEventNotification(Long notificationId) {
                    log.warn("readOneEventNotification called but notification client is disabled. No event notification found for notificationId {}.", notificationId);
                    return null;
                }

                @Override
                public Page<OdmEventNotificationResource> searchEventNotifications(Pageable pageable, OdmEventNotificationSearchOptions searchOption) {
                    log.warn("searchEventNotifications called but notification client is disabled. Returning empty page.");
                    return Page.empty();
                }
            };
        }
    }
}
