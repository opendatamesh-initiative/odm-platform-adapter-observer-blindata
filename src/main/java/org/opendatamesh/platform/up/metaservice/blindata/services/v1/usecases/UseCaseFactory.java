package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases;

import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v2.events.EventV2;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.exceptions.UseCaseInitException;

public interface UseCaseFactory {

    default UseCase getUseCase(Event event) throws UseCaseInitException {
        throw new UnsupportedOperationException("Not implemented");
    }

    default UseCase getUseCaseV2(EventV2 event) throws UseCaseInitException {
        throw new UnsupportedOperationException("Not implemented");
    }
}
