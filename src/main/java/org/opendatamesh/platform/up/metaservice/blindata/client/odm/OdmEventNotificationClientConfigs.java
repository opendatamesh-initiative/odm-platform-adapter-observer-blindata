package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtilsFactory;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmEventNotificationResourceV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OdmEventNotificationClientConfigs {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${odm.productPlane.notificationService.address}")
    private String address;

    @Value("${odm.productPlane.notificationService.active}")
    private boolean active;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private RestTemplate getRestTemplate() {
        return restTemplateBuilder.build();
    }

    @Bean
    public OdmEventNotificationClient eventNotificationClient() {
        if (active) {
            return new OdmEventNotificationClientImpl(address, RestUtilsFactory.getRestUtils(getRestTemplate()));
        } else {
            log.warn("ODM Event Notification Client is not enabled in the configuration.");
            return new OdmEventNotificationClient() {
                @Override
                public OdmEventNotificationResource updateEventNotification(Long notificationId, OdmEventNotificationResource eventNotificationResource) {
                    log.warn("updateEventNotification called but notification client is disabled. No update performed for notificationId {}.", notificationId);
                    return null;
                }

                @Override
                public OdmEventNotificationResourceV2 updateEventNotificationV2(Long sequenceId, OdmEventNotificationResourceV2 eventNotificationResource) {
                    log.warn("updateEventNotificationV2 called but notification client is disabled. No update performed for sequenceId {}.", sequenceId);
                    return null;
                }
            };
        }
    }
}
