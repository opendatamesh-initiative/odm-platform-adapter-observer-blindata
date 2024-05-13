package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.opendatamesh.platform.pp.notification.api.clients.EventNotificationClient;
import org.opendatamesh.platform.pp.notification.api.clients.NotificationClientImpl;
import org.opendatamesh.platform.pp.notification.api.clients.ObserverClient;
import org.opendatamesh.platform.pp.notification.api.resources.ObserverResource;
import org.opendatamesh.platform.up.metaservice.blindata.client.odm.proxies.OdmNotificationProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Configuration
@Slf4j
public class OdmNotificationClientConfigs {

    @Value("${odm.productPlane.notificationService.address}")
    private String address;

    @Value("${odm.productPlane.notificationService.active}")
    private boolean active;

    @Value("${server.baseUrl}")
    private String serverBaseUrl;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    EventNotificationClient notificationClient() {
        if (active) {
            NotificationClientImpl notificationClient = new NotificationClientImpl(
                    address,
                    objectMapper
            );
            registerObserver(notificationClient);
            return new OdmNotificationProxy(notificationClient);
        } else {
            log.warn("ODM Notification Client is not enabled in the configuration.");
            return new OdmNotificationProxy(null);
        }
    }

    private void registerObserver(ObserverClient observerClient) {
        Page<ObserverResource> oldBlindataObservers = observerClient.getObservers(Pageable.ofSize(2000), null);
        oldBlindataObservers
                .forEach(oldBlindataObserver -> observerClient.removeObserver(oldBlindataObserver.getId()));//TODO

        ObserverResource observerResource = new ObserverResource();
        observerResource.setName("BLINDATA");
        observerResource.setDisplayName("Blindata");
        observerResource.setObserverServerBaseUrl(serverBaseUrl);

        observerClient.addObserver(observerResource);
    }

}
