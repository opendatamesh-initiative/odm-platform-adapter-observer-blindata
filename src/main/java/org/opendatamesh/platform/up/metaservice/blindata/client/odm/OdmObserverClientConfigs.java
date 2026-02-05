package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v2.events.EventTypeV2;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtilsFactory;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmObserverResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmObserverSearchOptions;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmObserverSubscribeResponseResourceV2;
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

import java.util.Set;
import java.util.stream.Collectors;

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

    @Value("${odm.productPlane.notificationService.apiVersion:v1}")
    private String apiVersion;

    private final Set<EventType> eventTypesV1ToSubscribe = Set.of(
            // EventType.DATA_PRODUCT_CREATED, subscribed as V2 event
            EventType.DATA_PRODUCT_UPDATED,
            // EventType.DATA_PRODUCT_DELETED, subscribed as V2 event
            // EventType.DATA_PRODUCT_VERSION_CREATED, subscribed as V2 event
            // EventType.DATA_PRODUCT_VERSION_DELETED, subscribed as V2 event

            EventType.DATA_PRODUCT_ACTIVITY_CREATED,
            EventType.DATA_PRODUCT_ACTIVITY_STARTED,
            EventType.DATA_PRODUCT_ACTIVITY_COMPLETED,
            EventType.DATA_PRODUCT_TASK_CREATED,
            EventType.DATA_PRODUCT_TASK_STARTED,
            EventType.DATA_PRODUCT_TASK_COMPLETED,

            EventType.POLICY_CREATED,
            EventType.POLICY_UPDATED,
            EventType.POLICY_DELETED,

            EventType.MARKETPLACE_EXECUTOR_RESULT_RECEIVED
    );

    private final Set<EventTypeV2> eventTypesV2ToSubscribe = Set.of(
            EventTypeV2.DATA_PRODUCT_INITIALIZED,
            EventTypeV2.DATA_PRODUCT_DELETED,
            EventTypeV2.DATA_PRODUCT_VERSION_PUBLISHED,
            EventTypeV2.DATA_PRODUCT_VERSION_DELETED
    );

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
                public OdmObserverSubscribeResponseResourceV2 subscribeObserverV2(OdmObserverSubscribeResponseResourceV2.OdmObserverSubscribeResourceV2 observerSubscribeResource) {
                    log.warn("subscribeObserverV2 called but notification client is disabled. Observer '{}' not registered.", observerSubscribeResource.getName());
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
        switch (apiVersion.toUpperCase()) {
            case "V1": {
                OdmObserverResource observerResource = new OdmObserverResource();
                observerResource.setName(subscribeWithName);
                observerResource.setDisplayName(subscribeWithName);
                observerResource.setObserverServerBaseUrl(serverBaseUrl);

                observerClient.addObserver(observerResource);
                break;
            }
            case "V2": {
                // Subscribe to V1 events setting the observer API version to V1
                OdmObserverSubscribeResponseResourceV2.OdmObserverSubscribeResourceV2 observerSubscribeResource = new OdmObserverSubscribeResponseResourceV2.OdmObserverSubscribeResourceV2();
                observerSubscribeResource.setName(subscribeWithName);
                observerSubscribeResource.setDisplayName(subscribeWithName);
                observerSubscribeResource.setObserverBaseUrl(serverBaseUrl);
                observerSubscribeResource.setObserverApiVersion("V1");
                observerSubscribeResource.setEventTypes(eventTypesV1ToSubscribe.stream().map(EventType::name).collect(Collectors.toList()));

                observerClient.subscribeObserverV2(observerSubscribeResource);

                // Subscribe to V2 events setting the observer API version to V2
                OdmObserverSubscribeResponseResourceV2.OdmObserverSubscribeResourceV2 observerSubscribeResourceV2 = new OdmObserverSubscribeResponseResourceV2.OdmObserverSubscribeResourceV2();
                observerSubscribeResourceV2.setName(subscribeWithName + "-V2"); // Different name to avoid conflicts with V1 observer
                observerSubscribeResourceV2.setDisplayName(subscribeWithName + "-V2");
                observerSubscribeResourceV2.setObserverBaseUrl(serverBaseUrl);
                observerSubscribeResourceV2.setObserverApiVersion("V2");
                observerSubscribeResourceV2.setEventTypes(eventTypesV2ToSubscribe.stream().map(EventTypeV2::name).collect(Collectors.toList()));

                observerClient.subscribeObserverV2(observerSubscribeResourceV2);

                break;
            }
            default: {
                log.warn("Unsupported version: {}. No observer registered.", apiVersion);
                break;
            }
        }
    }
}