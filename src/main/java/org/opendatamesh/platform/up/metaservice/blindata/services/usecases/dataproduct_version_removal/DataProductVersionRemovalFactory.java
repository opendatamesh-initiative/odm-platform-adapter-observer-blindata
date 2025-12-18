package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_version_removal;

import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventType;
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

    @Override
    public UseCase getUseCase(Event event) throws UseCaseInitException {
        if (!supportedEventTypes.contains(event.getEventType())) {
            throw new UseCaseInitException("Failed to init DataProductVersionRemoval use case, unsupported event type: " + event.getEventType().name());
        }
        return new DataProductVersionRemoval(event);
    }
}


