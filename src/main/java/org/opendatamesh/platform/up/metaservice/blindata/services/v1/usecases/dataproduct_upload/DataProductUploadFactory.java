package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.dataproduct_upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.states.ActivityEventState;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.states.DataProductEventState;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.states.DataProductVersionEventState;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v2.events.EventTypeV2;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v2.events.EventV2;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdStewardshipClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdUserClient;
import org.opendatamesh.platform.up.metaservice.blindata.configurations.BdDataProductConfig;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.events.DataProductInitializationRequestedEventContentResource;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.UseCaseDryRunFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.UseCaseFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.exceptions.UseCaseInitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Function;

import static org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.EventType.*;
import static org.opendatamesh.platform.up.metaservice.blindata.adapter.v2.events.EventTypeV2.DATA_PRODUCT_INITIALIZATION_REQUESTED;

@Component
public class DataProductUploadFactory implements UseCaseFactory, UseCaseDryRunFactory {

    @Autowired
    private BdUserClient bdUserClient;
    @Autowired
    private BdDataProductClient bdDataProductClient;
    @Autowired
    private BdStewardshipClient bdStewardshipClient;
    @Autowired
    private BdDataProductConfig dataProductConfig;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${blindata.roleUuid}")
    private String roleUuid;

    private final Set<EventType> supportedEventTypes = Set.of(
            DATA_PRODUCT_CREATED,
            DATA_PRODUCT_VERSION_CREATED,
            DATA_PRODUCT_ACTIVITY_COMPLETED
    );

    private final Set<EventTypeV2> supportedEventTypesV2 = Set.of(
            DATA_PRODUCT_INITIALIZATION_REQUESTED
    );


    @Override
    public UseCase getUseCase(Event event) throws UseCaseInitException {
        if (!supportedEventTypes.contains(event.getEventType())) {
            throw new UseCaseInitException("Failed to init DataProductUpload use case, unsupported event type: " + event.getEventType());
        }
        try {
            DataProductUploadBlindataOutboundPort blindataOutboundPort = new DataProductUploadBlindataOutboundPortImpl(
                    bdUserClient,
                    bdDataProductClient,
                    bdStewardshipClient,
                    dataProductConfig,
                    roleUuid);
            DataProductUploadOdmOutboundPort odmOutboundPort = initOdmOutboundPort(event);
            return new DataProductUpload(
                    odmOutboundPort,
                    blindataOutboundPort
            );
        } catch (Exception e) {
            throw new UseCaseInitException("Failed to init DataProductUpload use case." + e.getMessage(), e);
        }
    }

    @Override
    public UseCase getUseCaseV2(EventV2 event) throws UseCaseInitException {
        if (!supportedEventTypesV2.contains(event.getEventType())) {
            throw new UseCaseInitException("Failed to init DataProductUpload use case, unsupported event type: " + event.getEventType());
        }
        try {
            DataProductUploadBlindataOutboundPort blindataOutboundPort = new DataProductUploadBlindataOutboundPortImpl(
                    bdUserClient,
                    bdDataProductClient,
                    bdStewardshipClient,
                    dataProductConfig,
                    roleUuid);
            DataProductUploadOdmOutboundPort odmOutboundPort = initOdmOutboundPort(event);
            return new DataProductUpload(
                    odmOutboundPort,
                    blindataOutboundPort
            );
        } catch (Exception e) {
            throw new UseCaseInitException("Failed to init DataProductUpload use case." + e.getMessage(), e);
        }
    }

    private DataProductUploadOdmOutboundPort initOdmOutboundPort(Event event) throws UseCaseInitException {
        Object afterState = event.getAfterState();
        if (afterState == null) {
            throw new UseCaseInitException("AfterState is null for event type: " + event.getEventType());
        }
        switch (event.getEventType()) {
            case DATA_PRODUCT_CREATED:
                return createOutboundPort(afterState, DataProductEventState.class, state -> new DataProductUploadOdmOutboundPortImpl(state.getDataProduct()));
            case DATA_PRODUCT_VERSION_CREATED:
                return createOutboundPort(afterState, DataProductVersionEventState.class, state -> new DataProductUploadOdmOutboundPortImpl(state.getDataProductVersion().getInfo()));
            case DATA_PRODUCT_ACTIVITY_COMPLETED:
                return createOutboundPort(afterState, ActivityEventState.class, state -> new DataProductUploadOdmOutboundPortImpl(state.getDataProductVersion().getInfo()));
            default:
                throw new UseCaseInitException("Unsupported event type: " + event.getEventType());
        }
    }

    private <T> DataProductUploadOdmOutboundPort createOutboundPort(Object afterState, Class<T> expectedClass, Function<T, DataProductUploadOdmOutboundPort> converter) throws UseCaseInitException {
        if (!expectedClass.isInstance(afterState)) {
            throw new UseCaseInitException("Invalid afterState type for " + expectedClass.getSimpleName() + " event: " +
                    afterState.getClass().getTypeName());
        }
        return converter.apply(expectedClass.cast(afterState));
    }

    private DataProductUploadOdmOutboundPort initOdmOutboundPort(EventV2 event) throws UseCaseInitException {
        Object eventContent = event.getEventContent();
        if (eventContent == null) {
            throw new UseCaseInitException("Event content is null for event type: " + event.getEventType());
        }
        try {
            DataProductInitializationRequestedEventContentResource dataProductInitializationRequestedEventContentResource = objectMapper.readValue(eventContent.toString(), DataProductInitializationRequestedEventContentResource.class);
            return new DataProductUploadOdmOutboundPortImpl(dataProductInitializationRequestedEventContentResource.getDataProduct());
        } catch (JsonProcessingException e) {
            throw new UseCaseInitException("Failed to parse event content to DataProductInitializedEventResource." + e.getMessage(), e);
        }
    }


    @Override
    public UseCase getUseCaseDryRun(Event event) throws UseCaseInitException {
        if (!supportedEventTypes.contains(event.getEventType())) {
            throw new UseCaseInitException("Failed to init DataProductUpload use case, unsupported event type: " + event.getEventType());
        }
        try {
            DataProductUploadBlindataOutboundPort blindataOutboundPort = new DataProductUploadBlindataOutboundPortImpl(
                    bdUserClient,
                    bdDataProductClient,
                    bdStewardshipClient,
                    dataProductConfig,
                    roleUuid);
            DataProductUploadOdmOutboundPort odmOutboundPort = initOdmOutboundPort(event);
            return new DataProductUpload(
                    odmOutboundPort,
                    new DataProductUploadBlindataOutboundPortDryRunImpl(blindataOutboundPort)
            );
        } catch (Exception e) {
            throw new UseCaseInitException("Failed to init DataProductUpload use case." + e.getMessage(), e);
        }
    }
}
