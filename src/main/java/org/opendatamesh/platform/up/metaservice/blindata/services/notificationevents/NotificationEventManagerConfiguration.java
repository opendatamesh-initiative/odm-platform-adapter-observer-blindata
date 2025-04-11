package org.opendatamesh.platform.up.metaservice.blindata.services.notificationevents;

import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_removal.DataProductRemovalFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload.DataProductUploadFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload.DataProductPortsAndAssetsUploadFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_upload.PoliciesUploadFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.quality_upload.QualityUploadFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.stages_upload.StagesUploadFactory;
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
public class NotificationEventManagerConfiguration {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataProductUploadFactory dataProductUploadFactory;
    @Autowired
    private DataProductPortsAndAssetsUploadFactory dataProductPortsAndAssetsUploadFactory;
    @Autowired
    private QualityUploadFactory qualityUploadFactory;
    @Autowired
    private PoliciesUploadFactory policiesUploadFactory;
    @Autowired
    private DataProductRemovalFactory dataProductRemovalFactory;
    @Autowired
    private StagesUploadFactory stagesUploadFactory;

    private final BlindataProperties blindataProperties;

    public NotificationEventManagerConfiguration(BlindataProperties blindataProperties) {
        this.blindataProperties = blindataProperties;
    }

    @Bean
    public NotificationEventManager eventManager() {
        List<NotificationEventHandler> eventHandlers = new ArrayList<>();

        for (BlindataProperties.EventHandler eventHandler : blindataProperties.getEventHandlers()) {
            validateEventHandler(eventHandler);
            eventHandlers.add(new UseCasesExecutionTemplate(
                    eventHandler.getActiveUseCases().contains("DATA_PRODUCT_UPLOAD") ? dataProductUploadFactory : null,
                    eventHandler.getActiveUseCases().contains("DATA_PRODUCT_VERSION_UPLOAD") ? dataProductPortsAndAssetsUploadFactory : null,
                    eventHandler.getActiveUseCases().contains("QUALITY_UPLOAD") ? qualityUploadFactory : null,
                    eventHandler.getActiveUseCases().contains("STAGES_UPLOAD") ? stagesUploadFactory : null,
                    eventHandler.getActiveUseCases().contains("POLICIES_UPLOAD") ? policiesUploadFactory : null,
                    eventHandler.getActiveUseCases().contains("DATA_PRODUCT_REMOVAL") ? dataProductRemovalFactory : null,
                    eventHandler.getEventType(),
                    eventHandler.getFilter()
            ));
            log.info("Activated event handler with the following configuration: \n{}", eventHandler);
        }
        return new NotificationEventManager(eventHandlers);
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
