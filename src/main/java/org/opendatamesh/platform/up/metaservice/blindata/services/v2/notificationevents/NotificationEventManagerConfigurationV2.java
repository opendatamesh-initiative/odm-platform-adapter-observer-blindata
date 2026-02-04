package org.opendatamesh.platform.up.metaservice.blindata.services.v2.notificationevents;

import org.opendatamesh.platform.up.metaservice.blindata.services.v1.notificationevents.BlindataProperties;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.dataproduct_removal.DataProductRemovalFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.dataproduct_upload.DataProductUploadFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.dataproduct_version_removal.DataProductVersionRemovalFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.dataproductports_and_assets_upload.DataProductPortsAndAssetsUploadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(BlindataProperties.class)
public class NotificationEventManagerConfigurationV2 {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataProductUploadFactory dataProductUploadFactory;
    @Autowired
    private DataProductPortsAndAssetsUploadFactory dataProductPortsAndAssetsUploadFactory;
    @Autowired
    private DataProductRemovalFactory dataProductRemovalFactory;
    @Autowired
    private DataProductVersionRemovalFactory dataProductVersionRemovalFactory;

    private final BlindataProperties blindataProperties;

    public NotificationEventManagerConfigurationV2(BlindataProperties blindataProperties) {
        this.blindataProperties = blindataProperties;
    }

    @Bean
    public NotificationEventManagerV2 eventManagerV2() {
        List<NotificationEventHandlerV2> eventHandlers = new ArrayList<>();

        for (BlindataProperties.EventHandler eventHandler : blindataProperties.getEventHandlers()) {
            validateEventHandler(eventHandler);
            eventHandlers.add(new UseCasesExecutionTemplateV2(
                    eventHandler.getActiveUseCases().contains("DATA_PRODUCT_UPLOAD") ? dataProductUploadFactory : null,
                    eventHandler.getActiveUseCases().contains("DATA_PRODUCT_VERSION_UPLOAD") ? dataProductPortsAndAssetsUploadFactory : null,
                    eventHandler.getActiveUseCases().contains("DATA_PRODUCT_REMOVAL") ? dataProductRemovalFactory : null,
                    eventHandler.getActiveUseCases().contains("DATA_PRODUCT_VERSION_REMOVAL") ? dataProductVersionRemovalFactory : null,
                    eventHandler.getEventType(),
                    eventHandler.getFilter()
            ));
            log.info("Activated event handler with the following configuration: \n{}", eventHandler);
        }
        return new NotificationEventManagerV2(eventHandlers);
    }

    private void validateEventHandler(BlindataProperties.EventHandler eventHandler) {
        if (!StringUtils.hasText(eventHandler.getEventType())) {
            throw new RuntimeException("Event Handler Configuration is missing EventType");
        }
        if (CollectionUtils.isEmpty(eventHandler.getActiveUseCases())) {
            throw new RuntimeException("Event Handler of " + eventHandler.getEventType() + " has an empty list of UseCases");
        }
    }

}
