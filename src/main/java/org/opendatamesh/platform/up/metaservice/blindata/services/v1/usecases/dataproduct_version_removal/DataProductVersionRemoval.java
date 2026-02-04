package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.dataproduct_version_removal;

import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.exceptions.UseCaseExecutionException;

import static org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;

class DataProductVersionRemoval implements UseCase {

    private static final String USE_CASE_PREFIX = "[DataProductVersionRemoval]";

    private final Event event;

    DataProductVersionRemoval(Event event) {
        this.event = event;
    }

    @Override
    public void execute() throws UseCaseExecutionException {
        getUseCaseLogger().info(String.format(
                "%s Received DATA_PRODUCT_VERSION_DELETED event. No action will be performed on Blindata; data product will be preserved. EventId: %s",
                USE_CASE_PREFIX,
                event.getEventType()
        ));
        // no-op on purpose: Blindata does not manage data product versions via API,
        // and the data product entity must be preserved along with user-entered metadata.
    }
}


