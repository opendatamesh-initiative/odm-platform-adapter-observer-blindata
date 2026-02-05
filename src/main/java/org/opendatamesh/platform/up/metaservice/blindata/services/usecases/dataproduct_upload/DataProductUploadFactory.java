package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states.ActivityEventState;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states.DataProductEventState;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states.DataProductVersionEventState;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdStewardshipClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdUserClient;
import org.opendatamesh.platform.up.metaservice.blindata.configurations.BdDataProductConfig;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.eventcontents.DataProductInitializedEventContentResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.eventcontents.DataProductVersionPublishedEventContentResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.internal.EventTypeV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.internal.EventV2;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCaseDryRunFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCaseFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Function;

import static org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventType.*;
import static org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.internal.EventTypeV2.DATA_PRODUCT_INITIALIZED;
import static org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.internal.EventTypeV2.DATA_PRODUCT_VERSION_PUBLISHED;

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
            DATA_PRODUCT_INITIALIZED,
            DATA_PRODUCT_VERSION_PUBLISHED
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
            DataProductUploadOdmOutboundPort odmOutboundPort = initOdmOutboundPortV2(event);
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

    private DataProductUploadOdmOutboundPort initOdmOutboundPortV2(EventV2 event) throws UseCaseInitException {
        Object eventContent = event.getEventContent();
        if (eventContent == null) {
            throw new UseCaseInitException("Event content is null for event type: " + event.getEventType());
        }
        try {
            switch (event.getEventType()) {
                case DATA_PRODUCT_INITIALIZED: {
                    DataProductInitializedEventContentResource dataProductInitializedEventContentResource = objectMapper.readValue(eventContent.toString(), DataProductInitializedEventContentResource.class);
                    return new DataProductUploadOdmOutboundPortImpl(dataProductInitializedEventContentResource.getDataProduct());
                }
                case DATA_PRODUCT_VERSION_PUBLISHED: {
                    DataProductVersionPublishedEventContentResource dataProductVersionPublishedEventContentResource = objectMapper.readValue(eventContent.toString(), DataProductVersionPublishedEventContentResource.class);
                    return new DataProductUploadOdmOutboundPortImpl(dataProductVersionPublishedEventContentResource.getDataProductVersion().getDataProduct());
                }
                default:
                    throw new UseCaseInitException("Unsupported event type: " + event.getEventType());
            }
        } catch (JsonProcessingException e) {
            throw new UseCaseInitException("Failed to parse event content, " + e.getMessage(), e);
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
