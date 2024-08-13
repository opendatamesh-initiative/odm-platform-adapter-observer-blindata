package org.opendatamesh.platform.up.metaservice.blindata.services.notificationevents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_removal.DataProductRemovalFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload.DataProductUploadFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductversion_upload.DataProductVersionUploadFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_upload.PoliciesUploadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class NotificationEventManagerConfiguration {

    @Value("${blindata.eventHandlers}")
    private String configs;

    @Autowired
    private DataProductUploadFactory dataProductUploadFactory;
    @Autowired
    private DataProductVersionUploadFactory dataProductVersionUploadFactory;
    @Autowired
    private PoliciesUploadFactory policiesUploadFactory;
    @Autowired
    private DataProductRemovalFactory dataProductRemovalFactory;

    @Bean
    public NotificationEventManager eventManager() throws JsonProcessingException {
        List<NotificationEventHandler> eventHandlers = new ArrayList<>();

        EventHandlerConfig[] eventHandlerConfigs = new ObjectMapper().readValue(configs, EventHandlerConfig[].class);

        for (EventHandlerConfig config : eventHandlerConfigs) {
            eventHandlers.add(new UseCasesExecutionTemplate(
                    config.getActiveUseCases().contains("DATA_PRODUCT_UPLOAD") ? dataProductUploadFactory : null,
                    config.getActiveUseCases().contains("DATA_PRODUCT_VERSION_UPLOAD") ? dataProductVersionUploadFactory : null,
                    config.getActiveUseCases().contains("POLICIES_UPLOAD") ? policiesUploadFactory : null,
                    config.getActiveUseCases().contains("DATA_PRODUCT_REMOVAL") ? dataProductRemovalFactory : null,
                    config.eventType,
                    config.filter
            ));
        }
        return new NotificationEventManager(eventHandlers);
    }

    @Data
    public static class EventHandlerConfig {
        private String eventType;
        private String filter;
        private List<String> activeUseCases;
    }
}
