package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtilsFactory;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmObserverResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmObserverSearchOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OdmObserverClientConfigs {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${odm.productPlane.notificationService.subscribeWithName:BLINDATA}")
    private String subscribeWithName;

    @Value("${odm.productPlane.notificationService.address}")
    private String address;

    @Value("${odm.productPlane.notificationService.active}")
    private boolean active;

    @Value("${server.baseUrl}")
    private String serverBaseUrl;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private RestTemplate getRestTemplate() {
        return restTemplateBuilder.build();
    }

    @Bean
    public OdmObserverClient notificationClient() {
        if (active) {
            OdmObserverClient observerClient = new OdmObserverClientImpl(
                    address,
                    RestUtilsFactory.getRestUtils(getRestTemplate())
            );
            registerObserver(observerClient);
            return observerClient;
        } else {
            log.warn("ODM Notification Client is not enabled in the configuration.");
            return new OdmObserverClient() {

                @Override
                public OdmObserverResource addObserver(OdmObserverResource observerResource) {
                    log.warn("addObserver called but notification client is disabled. Observer '{}' not registered.", observerResource.getName());
                    return null;
                }

                @Override
                public OdmObserverResource updateObserver(Long id, OdmObserverResource observerResource) {
                    log.warn("updateObserver called but notification client is disabled. No update performed for observer id {}.", id);
                    return null;
                }

                @Override
                public Page<OdmObserverResource> getObservers(Pageable pageable, OdmObserverSearchOptions searchOptions) {
                    log.warn("getObservers called but notification client is disabled. Returning an empty page.");
                    return Page.empty();
                }

                @Override
                public OdmObserverResource getObserver(Long id) {
                    log.warn("getObserver called but notification client is disabled. No observer found for id {}.", id);
                    return null;
                }

                @Override
                public void removeObserver(Long id) {
                    log.warn("removeObserver called but notification client is disabled. No observer removed for id {}.", id);
                }
            };
        }
    }

    private void registerObserver(OdmObserverClient observerClient) {
        OdmObserverResource observerResource = new OdmObserverResource();
        observerResource.setName(subscribeWithName);
        observerResource.setDisplayName(subscribeWithName);
        observerResource.setObserverServerBaseUrl(serverBaseUrl);

        observerClient.addObserver(observerResource);
    }
}