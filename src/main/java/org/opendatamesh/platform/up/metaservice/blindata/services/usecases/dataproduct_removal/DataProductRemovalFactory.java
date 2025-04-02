package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_removal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.eventstates.DataProductEventState;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCaseFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

import static org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventType.DATA_PRODUCT_DELETED;

@Component
public class DataProductRemovalFactory implements UseCaseFactory {

    @Autowired
    private BDDataProductClient bdDataProductClient;

    @Autowired
    private ObjectMapper objectMapper;

    private final Set<String> supportedEventTypes = Set.of(
            DATA_PRODUCT_DELETED.name()
    );


    @Override
    public UseCase getUseCase(OdmEventNotificationResource event) throws UseCaseInitException {
        if (!supportedEventTypes.contains(event.getEvent().getType().toUpperCase())) {
            throw new UseCaseInitException("Failed to init DataProductRemoval use case, unsupported event type: " + event.getEvent().getType());
        }
        try {
            DataProductRemovalOdmOutboundPort odmOutboundPort = initOdmOutboundPort(event);
            DataProductRemovalBlindataOutboundPort blindataOutboundPort = new DataProductRemovalBlindataOutboundPortImpl(
                    bdDataProductClient
            );
            return new DataProductRemoval(
                    odmOutboundPort,
                    blindataOutboundPort
            );
        } catch (Exception e) {
            throw new UseCaseInitException("Failed to init DataProductRemoval use case." + e.getMessage(), e);
        }
    }

    private DataProductRemovalOdmOutboundPort initOdmOutboundPort(OdmEventNotificationResource event) throws JsonProcessingException {
        DataProductEventState dataProductEventState = objectMapper.treeToValue(event.getEvent().getBeforeState(), DataProductEventState.class);
        String fullyQualifiedName = dataProductEventState.getDataProduct().getFullyQualifiedName();
        return new DataProductRemovalOdmOutboundPortImpl(fullyQualifiedName);
    }
}

