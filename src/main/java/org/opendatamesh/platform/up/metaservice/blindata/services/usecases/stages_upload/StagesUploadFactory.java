package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.stages_upload;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states.ActivityEventState;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states.DataProductVersionEventState;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCaseFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;
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


    @Override
    public UseCase getUseCase(Event event) throws UseCaseInitException {
        if (!supportedEventTypes.contains(event.getEventType())) {
            throw new UseCaseInitException("Failed to init PoliciesUpload use case, unsupported event type: " + event.getEventType());
        }
        try {
            StagesUploadBlindataOutboundPort bdOutboundPort = new StagesUploadBlindataOutboundPortImpl(bdDataProductClient);
            StagesUploadOdmOutboundPort odmOutboundPort = initOdmOutboundPort(event);
            return new StagesUpload(bdOutboundPort, odmOutboundPort);
        } catch (Exception e) {
            throw new UseCaseInitException("Failed to init PoliciesUpload use case.", e);
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
