package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_removal;

import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseRecoverableExceptionContext.getExceptionHandler;


class DataProductRemoval implements UseCase {

    private final String USE_CASE_PREFIX = "[DataProductRemoval]";

    private final Logger log;
    private final DataProductRemovalOdmOutboundPort odmOutboundPort;
    private final DataProductRemovalBlindataOutboundPort blindataOutboundPort;

    DataProductRemoval(DataProductRemovalOdmOutboundPort odmOutboundPort, DataProductRemovalBlindataOutboundPort blindataOutboundPort) {
        this.odmOutboundPort = odmOutboundPort;
        this.blindataOutboundPort = blindataOutboundPort;
        this.log = LoggerFactory.getLogger(this.getClass());
        getExceptionHandler().setLogger(log);
    }

    DataProductRemoval(DataProductRemovalOdmOutboundPort odmOutboundPort, DataProductRemovalBlindataOutboundPort blindataOutboundPort, Logger logger) {
        this.odmOutboundPort = odmOutboundPort;
        this.blindataOutboundPort = blindataOutboundPort;
        this.log = logger;
        getExceptionHandler().setLogger(log);
    }

    @Override
    public void execute() throws UseCaseExecutionException {
        String fullyQualifiedName = odmOutboundPort.getDataProductFullyQualifiedName();

        log.info("{} Deleting data product {} on Blindata.", USE_CASE_PREFIX, fullyQualifiedName);
        blindataOutboundPort.deleteDataProduct(fullyQualifiedName);
    }
}
