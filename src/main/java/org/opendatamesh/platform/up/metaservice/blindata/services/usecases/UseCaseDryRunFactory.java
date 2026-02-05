package org.opendatamesh.platform.up.metaservice.blindata.services.usecases;

import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;

public interface UseCaseDryRunFactory {

    UseCase getUseCaseDryRun(Event event) throws UseCaseInitException;
}
