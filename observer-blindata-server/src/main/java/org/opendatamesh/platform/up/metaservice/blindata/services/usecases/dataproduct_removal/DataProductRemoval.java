package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_removal;

import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;

import static org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;


class DataProductRemoval implements UseCase {

    private final String USE_CASE_PREFIX = "[DataProductRemoval]";

    private final DataProductRemovalOdmOutboundPort odmOutboundPort;
    private final DataProductRemovalBlindataOutboundPort blindataOutboundPort;

    DataProductRemoval(DataProductRemovalOdmOutboundPort odmOutboundPort, DataProductRemovalBlindataOutboundPort blindataOutboundPort) {
        this.odmOutboundPort = odmOutboundPort;
        this.blindataOutboundPort = blindataOutboundPort;
    }

    @Override
    public void execute() throws UseCaseExecutionException {
        String fullyQualifiedName = odmOutboundPort.getDataProductFullyQualifiedName();
        getUseCaseLogger().info(String.format("%s Deleting data product %s on Blindata.", USE_CASE_PREFIX, fullyQualifiedName));
        blindataOutboundPort.deleteDataProduct(fullyQualifiedName);
    }
}
