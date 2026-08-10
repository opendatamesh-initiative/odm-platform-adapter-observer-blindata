package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.probes_upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states.ActivityEventState;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states.DataProductVersionEventState;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdProbesClient;
import org.opendatamesh.platform.up.metaservice.blindata.configurations.BdProbesUploadConfig;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.eventcontents.DataProductVersionPublishedEventContentResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.internal.EventTypeV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.internal.EventV2;
import org.opendatamesh.platform.up.metaservice.blindata.services.DataProductPortAssetAnalyzer;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCaseDryRunFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCaseFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ProbesUploadFactory implements UseCaseFactory, UseCaseDryRunFactory {

    @Autowired
    private BdProbesClient bdProbesClient;
    @Autowired
    private DataProductPortAssetAnalyzer dataProductPortAssetAnalyzer;
    @Autowired
    private BdProbesUploadConfig probesUploadConfig;
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
            throw new UseCaseInitException("Failed to init ProbesUpload use case, unsupported event type: " + event.getEventType());
        }
        try {
            ProbesUploadBlindataOutboundPort blindataOutboundPort = new ProbesUploadBlindataOutboundPortImpl(bdProbesClient);
            ProbesUploadOdmOutboundPort odmOutboundPort = initOdmOutboundPort(event);
            return new ProbesUpload(blindataOutboundPort, odmOutboundPort);
        } catch (Exception e) {
            throw new UseCaseInitException("Failed to init ProbesUpload use case.", e);
        }
    }

    @Override
    public UseCase getUseCaseV2(EventV2 event) throws UseCaseInitException {
        if (!supportedEventTypesV2.contains(event.getEventType())) {
            throw new UseCaseInitException("Failed to init ProbesUpload use case, unsupported event type: " + event.getEventType());
        }
        try {
            ProbesUploadBlindataOutboundPort blindataOutboundPort = new ProbesUploadBlindataOutboundPortImpl(bdProbesClient);
            ProbesUploadOdmOutboundPort odmOutboundPort = initOdmOutboundPortV2(event);
            return new ProbesUpload(blindataOutboundPort, odmOutboundPort);
        } catch (Exception e) {
            throw new UseCaseInitException("Failed to init ProbesUpload use case.", e);
        }
    }

    @Override
    public UseCase getUseCaseDryRun(Event event) throws UseCaseInitException {
        if (!supportedEventTypes.contains(event.getEventType())) {
            throw new UseCaseInitException("Failed to init ProbesUpload use case, unsupported event type: " + event.getEventType());
        }
        try {
            ProbesUploadBlindataOutboundPort blindataOutboundPort = new ProbesUploadBlindataOutboundPortDryRunImpl(
                    new ProbesUploadBlindataOutboundPortImpl(bdProbesClient)
            );
            ProbesUploadOdmOutboundPort odmOutboundPort = initOdmOutboundPort(event);
            return new ProbesUpload(blindataOutboundPort, odmOutboundPort);
        } catch (Exception e) {
            throw new UseCaseInitException("Failed to init ProbesUpload use case.", e);
        }
    }

    private ProbesUploadOdmOutboundPort initOdmOutboundPort(Event event) throws UseCaseInitException {
        switch (event.getEventType()) {
            case DATA_PRODUCT_ACTIVITY_COMPLETED: {
                ActivityEventState state = castState(event.getAfterState(), ActivityEventState.class, event.getEventType(), "afterState");
                return new ProbesUploadOdmOutboundPortImpl(
                        dataProductPortAssetAnalyzer,
                        state.getDataProductVersion(),
                        probesUploadConfig
                );
            }
            case DATA_PRODUCT_VERSION_CREATED: {
                DataProductVersionEventState state = castState(event.getAfterState(), DataProductVersionEventState.class, event.getEventType(), "afterState");
                return new ProbesUploadOdmOutboundPortImpl(
                        dataProductPortAssetAnalyzer,
                        state.getDataProductVersion(),
                        probesUploadConfig
                );
            }
            default:
                throw new UseCaseInitException("Failed to init odmOutboundPort on ProbesUpload use case.");
        }
    }

    private ProbesUploadOdmOutboundPort initOdmOutboundPortV2(EventV2 event) throws UseCaseInitException {
        Object eventContent = event.getEventContent();
        if (eventContent == null) {
            throw new UseCaseInitException("Event content is null for event type: " + event.getEventType());
        }
        try {
            switch (event.getEventType()) {
                case DATA_PRODUCT_VERSION_PUBLISHED: {
                    DataProductVersionPublishedEventContentResource eventContentResource =
                            objectMapper.readValue(eventContent.toString(), DataProductVersionPublishedEventContentResource.class);
                    DataProductVersion dataProductVersion = objectMapper.readValue(
                            eventContentResource.getDataProductVersion().getContent().toString(),
                            DataProductVersion.class
                    );
                    return new ProbesUploadOdmOutboundPortImpl(dataProductPortAssetAnalyzer, dataProductVersion, probesUploadConfig);
                }
                default:
                    throw new UseCaseInitException("Unsupported event type: " + event.getEventType());
            }
        } catch (JsonProcessingException e) {
            throw new UseCaseInitException("Failed to parse event content.", e);
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
