package org.opendatamesh.platform.up.metaservice.blindata.services.usecases;

import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.internal.EventV2;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;

public interface UseCaseFactory {

    default UseCase getUseCase(Event event) throws UseCaseInitException {
        throw new UnsupportedOperationException("Not implemented");
    }

    default UseCase getUseCaseV2(EventV2 event) throws UseCaseInitException {
        throw new UnsupportedOperationException("Not implemented");
    }
}
