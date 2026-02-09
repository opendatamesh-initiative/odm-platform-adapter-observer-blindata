package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_version_removal;

import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.internal.EventTypeV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.internal.EventV2;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCaseFactory;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataProductVersionRemovalFactory implements UseCaseFactory {

    private final Set<EventType> supportedEventTypes = Set.of(
            EventType.DATA_PRODUCT_VERSION_DELETED
    );

    private final Set<EventTypeV2> supportedEventTypesV2 = Set.of(
            EventTypeV2.DATA_PRODUCT_VERSION_DELETED
    );

    @Override
    public UseCase getUseCase(Event event) throws UseCaseInitException {
        if (!supportedEventTypes.contains(event.getEventType())) {
            throw new UseCaseInitException("Failed to init DataProductVersionRemoval use case, unsupported event type: " + event.getEventType().name());
        }
        return new DataProductVersionRemoval(event);
    }

    @Override
    public UseCase getUseCaseV2(EventV2 event) throws UseCaseInitException {
        if (!supportedEventTypesV2.contains(event.getEventType())) {
            throw new UseCaseInitException("Failed to init DataProductVersionRemoval use case, unsupported event type: " + event.getEventType().name());
        }
        Event eventV1 = new Event();
        eventV1.setEventType(EventType.DATA_PRODUCT_VERSION_DELETED);
        return new DataProductVersionRemoval(eventV1);
    }
}


