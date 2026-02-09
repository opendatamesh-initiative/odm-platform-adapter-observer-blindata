package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_removal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states.DataProductEventState;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.eventcontents.DataProductDeletedEventContentResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.internal.EventTypeV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.internal.EventV2;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCaseFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

    private final Set<EventTypeV2> supportedEventTypesV2 = Set.of(
            EventTypeV2.DATA_PRODUCT_DELETED
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

    @Override
    public UseCase getUseCaseV2(EventV2 event) throws UseCaseInitException {
        if (!supportedEventTypesV2.contains(event.getEventType())) {
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
        if (dataProductEventState.getDataProduct() == null || !StringUtils.hasText(dataProductEventState.getDataProduct().getFullyQualifiedName())) {
            throw new UseCaseInitException("Impossible to retrieve fullyQualifiedName on DATA_PRODUCT_DELETED event: " + objectMapper.writeValueAsString(event));
        }
        String fullyQualifiedName = dataProductEventState.getDataProduct().getFullyQualifiedName();
        return new DataProductRemovalOdmOutboundPortImpl(fullyQualifiedName);
    }

    private DataProductRemovalOdmOutboundPort initOdmOutboundPort(EventV2 event) throws JsonProcessingException, UseCaseInitException {
        Object eventContent = event.getEventContent();
        if (eventContent == null) {
            throw new UseCaseInitException("Event content is null for event type: " + event.getEventType());
        }
        try {
            DataProductDeletedEventContentResource dataProductDeletedEventContentResource = objectMapper.readValue(eventContent.toString(), DataProductDeletedEventContentResource.class);
            String fullyQualifiedName = dataProductDeletedEventContentResource.getDataProductFqn();
            return new DataProductRemovalOdmOutboundPortImpl(fullyQualifiedName);
        } catch (JsonProcessingException e) {
            throw new UseCaseInitException("Failed to parse event content to DataProductDeletedEventResource." + e.getMessage(), e);
        }
    }
}

