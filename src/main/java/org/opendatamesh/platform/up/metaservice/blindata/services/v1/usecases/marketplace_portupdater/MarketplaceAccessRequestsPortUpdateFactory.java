package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.marketplace_portupdater;

import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.states.MarketplaceAccessRequestEventState;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdMarketplaceAccessRequestsUploadResultClient;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.UseCaseFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.exceptions.UseCaseInitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class MarketplaceAccessRequestsPortUpdateFactory implements UseCaseFactory {

    @Autowired
    private BdMarketplaceAccessRequestsUploadResultClient bdMarketplaceAccessRequestsUploadResultClient;

    private final Set<EventType> supportedEventTypes = Set.of(
            EventType.MARKETPLACE_EXECUTOR_RESULT_RECEIVED
    );

    @Override
    public UseCase getUseCase(Event event) throws UseCaseInitException {
        if (!supportedEventTypes.contains(event.getEventType())) {
            throw new UseCaseInitException("Failed to init MarketplaceAccessRequestsPortUpdater use case, unsupported event type: " + event.getEventType());
        }
        try {
            MarketplaceAccessRequestsPortUpdaterOdmOutboundPort odmOutboundPort = initOdmOutboundPort(event);
            MarketplaceAccessRequestsPortUpdaterBlindataOutboundPort bdOutboundPort =
                    new MarketplaceAccessRequestsPortUpdateBlindataOutboundPortImpl(
                            bdMarketplaceAccessRequestsUploadResultClient
                    );
            return new MarketplaceAccessRequestsPortUpdate(
                    odmOutboundPort,
                    bdOutboundPort
            );
        } catch (Exception e) {
            throw new UseCaseInitException("Failed to init MarketplaceAccessRequestsPortUpdater use case.", e);
        }
    }

    private MarketplaceAccessRequestsPortUpdaterOdmOutboundPort initOdmOutboundPort(Event event) throws UseCaseInitException {
        switch (event.getEventType()) {
            case MARKETPLACE_EXECUTOR_RESULT_RECEIVED: {
                MarketplaceAccessRequestEventState state = castState(event.getAfterState(), MarketplaceAccessRequestEventState.class, event.getEventType(), "afterState");
                return new MarketplaceAccessRequestsPortUpdaterOdmOutboundPortImpl(state);
            }
            default:
                throw new UseCaseInitException("Failed to init OdmOutboundPort on MarketplaceAccessRequestsPortUpdater use case.");
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
