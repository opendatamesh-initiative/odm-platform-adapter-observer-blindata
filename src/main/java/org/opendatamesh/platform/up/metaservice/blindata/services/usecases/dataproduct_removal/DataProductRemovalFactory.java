package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_removal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states.DataProductEventState;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCaseFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataProductRemovalFactory implements UseCaseFactory {

    @Autowired
    private BdDataProductClient bdDataProductClient;

    @Autowired
    private ObjectMapper objectMapper;

    private final Set<EventType> supportedEventTypes = Set.of(
            EventType.DATA_PRODUCT_DELETED
    );


    @Override
    public UseCase getUseCase(Event event) throws UseCaseInitException {
        if (!supportedEventTypes.contains(event.getEventType())) {
            throw new UseCaseInitException("Failed to init DataProductRemoval use case, unsupported event type: " + event.getEventType().name());
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

    private DataProductRemovalOdmOutboundPort initOdmOutboundPort(Event event) throws JsonProcessingException, UseCaseInitException {
        if (!(event.getBeforeState() instanceof DataProductEventState)) {
            throw new UseCaseInitException("The event: " + event.getEventType() + " has not DataProductEventState as beforeState.");
        }
        DataProductEventState dataProductEventState = (DataProductEventState) event.getBeforeState();
        String fullyQualifiedName;
        if (dataProductEventState.getDataProduct() != null) {
            fullyQualifiedName = dataProductEventState.getDataProduct().getFullyQualifiedName();
        } else if (dataProductEventState.getDataProductVersion() != null) {
            fullyQualifiedName = dataProductEventState.getDataProductVersion().getInfo().getFullyQualifiedName();
        } else {
            throw new UseCaseInitException("Impossible to retrieve fullyQualifiedName on DATA_PRODUCT_DELETE event: " + objectMapper.writeValueAsString(event));
        }
        return new DataProductRemovalOdmOutboundPortImpl(fullyQualifiedName);
    }
}

