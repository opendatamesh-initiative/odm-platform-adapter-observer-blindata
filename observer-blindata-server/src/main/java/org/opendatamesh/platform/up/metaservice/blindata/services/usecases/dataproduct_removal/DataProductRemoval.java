package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_removal;

import lombok.extern.slf4j.Slf4j;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;

@Slf4j
class DataProductRemoval implements UseCase {

    private final String USE_CASE_PREFIX = "[DataProductRemoval]";

    private final DataProductRemovalOdmOutboundPort odmOutboundPort;
    private final DataProductRemovalBlindataOutboundPort blindataOutboundPort;

    public DataProductRemoval(DataProductRemovalOdmOutboundPort odmOutboundPort, DataProductRemovalBlindataOutboundPort blindataOutboundPort) {
        this.odmOutboundPort = odmOutboundPort;
        this.blindataOutboundPort = blindataOutboundPort;
    }

    @Override
    public void execute() throws UseCaseExecutionException {
        String fullyQualifiedName = odmOutboundPort.getDataProductFullyQualifiedName();

        log.info("{} Deleting data product {} on Blindata.", USE_CASE_PREFIX, fullyQualifiedName);
        blindataOutboundPort.deleteDataProduct(fullyQualifiedName);
    }
}
