package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.opendatamesh.platform.pp.notification.api.clients.EventNotificationClient;
import org.opendatamesh.platform.pp.notification.api.clients.NotificationClientImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OdmNotificationClientConfigs {

    @Value("${odm.productPlane.notificationService.address}")
    private String address;

    @Value("${odm.productPlane.notificationService.active}")
    private boolean active;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    EventNotificationClient policyEvaluationResultClient(){
        if(active){
            return new NotificationClientImpl(
                    address,
                    objectMapper
            );
        } else {
            log.warn("ODM Notification Client is not enabled in the configuration.");
            return null;//TODO
        }
    }

}
