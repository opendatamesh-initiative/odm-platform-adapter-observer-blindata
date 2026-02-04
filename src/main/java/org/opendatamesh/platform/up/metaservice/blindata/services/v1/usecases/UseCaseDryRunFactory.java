package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases;

import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.exceptions.UseCaseInitException;

public interface UseCaseDryRunFactory {

    UseCase getUseCaseDryRun(Event event) throws UseCaseInitException;
}
