package org.opendatamesh.platform.up.metaservice.blindata.services.usecases;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;

public interface UseCaseDryRunFactory {

    UseCase getUseCaseDryRun(OdmEventNotificationResource event) throws UseCaseInitException;
}
