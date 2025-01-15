package org.opendatamesh.platform.up.metaservice.blindata.services.usecases;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.OBEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;

public interface UseCaseDryRunFactory {

    UseCase getUseCaseDryRun(OBEventNotificationResource event) throws UseCaseInitException;
}
