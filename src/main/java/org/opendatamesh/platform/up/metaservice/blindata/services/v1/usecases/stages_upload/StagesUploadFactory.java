package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.stages_upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.states.ActivityEventState;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.states.DataProductVersionEventState;
import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v2.events.EventTypeV2;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v2.events.EventV2;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.events.DataProductVersionPublishedEventContentResource;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.UseCaseFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.exceptions.UseCaseInitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class StagesUploadFactory implements UseCaseFactory {

    @Autowired
    private BdDataProductClient bdDataProductClient;

    @Autowired
    private ObjectMapper objectMapper;

    private final Set<EventType> supportedEventTypes = Set.of(
            EventType.DATA_PRODUCT_VERSION_CREATED,
            EventType.DATA_PRODUCT_ACTIVITY_COMPLETED
    );

    private final Set<EventTypeV2> supportedEventTypesV2 = Set.of(
            EventTypeV2.DATA_PRODUCT_VERSION_PUBLISHED
    );

    @Override
    public UseCase getUseCase(Event event) throws UseCaseInitException {
        if (!supportedEventTypes.contains(event.getEventType())) {
            throw new UseCaseInitException("Failed to init StagesUpload use case, unsupported event type: " + event.getEventType());
        }
        try {
            StagesUploadBlindataOutboundPort bdOutboundPort = new StagesUploadBlindataOutboundPortImpl(bdDataProductClient);
            StagesUploadOdmOutboundPort odmOutboundPort = initOdmOutboundPort(event);
            return new StagesUpload(bdOutboundPort, odmOutboundPort);
        } catch (Exception e) {
            throw new UseCaseInitException("Failed to init StagesUpload use case.", e);
        }
    }

    @Override
    public UseCase getUseCaseV2(EventV2 event) throws UseCaseInitException {
        if (!supportedEventTypesV2.contains(event.getEventType())) {
            throw new UseCaseInitException("Failed to init StagesUpload use case, unsupported event type: " + event.getEventType());
        }
        try {
            StagesUploadBlindataOutboundPort bdOutboundPort = new StagesUploadBlindataOutboundPortImpl(bdDataProductClient);
            StagesUploadOdmOutboundPort odmOutboundPort = initOdmOutboundPortV2(event);
            return new StagesUpload(bdOutboundPort, odmOutboundPort);
        } catch (Exception e) {
            throw new UseCaseInitException("Failed to init StagesUpload use case.", e);
        }
    }

    private StagesUploadOdmOutboundPort initOdmOutboundPort(Event event) throws UseCaseInitException {
        switch (event.getEventType()) {
            case DATA_PRODUCT_ACTIVITY_COMPLETED: {
                ActivityEventState state = castState(event.getAfterState(), ActivityEventState.class, event.getEventType(), "afterState");
                return new StagesUploadOdmOutboundPortImpl(state.getDataProductVersion(), state.getActivity());
            }
            case DATA_PRODUCT_VERSION_CREATED: {
                DataProductVersionEventState state = castState(event.getAfterState(), DataProductVersionEventState.class, event.getEventType(), "afterState");
                return new StagesUploadOdmOutboundPortImpl(state.getDataProductVersion(), null);
            }
            default:
                throw new UseCaseInitException("Failed to init odmOutboundPort on DataProductVersionUpload use case.");
        }
    }

    private StagesUploadOdmOutboundPort initOdmOutboundPortV2(EventV2 event) throws UseCaseInitException {
        Object eventContent = event.getEventContent();
        if (eventContent == null) {
            throw new UseCaseInitException("Event content is null for event type: " + event.getEventType());
        }
        try {
            switch (event.getEventType()) {
                case DATA_PRODUCT_VERSION_PUBLISHED: {
                    DataProductVersionPublishedEventContentResource dataProductVersionPublishedEventContentResource = objectMapper.readValue(eventContent.toString(), DataProductVersionPublishedEventContentResource.class);
                    DataProductVersion dataProductVersion = objectMapper.readValue(dataProductVersionPublishedEventContentResource.getDataProductVersion().getContent().toString(), DataProductVersion.class);
                    return new StagesUploadOdmOutboundPortImpl(dataProductVersion, null);
                }
                default:
                    throw new UseCaseInitException("Unsupported event type: " + event.getEventType());
            }
        } catch (JsonProcessingException e) {
            throw new UseCaseInitException("Failed to parse event content, " + e.getMessage(), e);
        }
    }

    private <T> T castState(Object state, Class<T> expectedClass, EventType eventType, String stateName) throws UseCaseInitException {
        if (state == null) {
            throw new UseCaseInitException("The " + stateName + " is null for event: " + eventType);
        }
        if (!expectedClass.isInstance(state)) {
            throw new UseCaseInitException("The event: " + eventType + " does not have " +
                    expectedClass.getSimpleName() + " as " + stateName + ", but got: " + state.getClass().getTypeName());
        }
        return expectedClass.cast(state);
    }

}
