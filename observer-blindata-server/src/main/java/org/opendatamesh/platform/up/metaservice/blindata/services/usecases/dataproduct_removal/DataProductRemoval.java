package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_removal;

import lombok.extern.slf4j.Slf4j;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;

@Slf4j
class DataProductRemoval implements UseCase {

    private final String USE_CASE_PREFIX = "[DataProductRemoval]";

    private final DataProductRemovalOdmOutputPort odmOutputPort;
    private final DataProductRemovalBlindataOutputPort blindataOutputPort;

    public DataProductRemoval(DataProductRemovalOdmOutputPort odmOutputPort, DataProductRemovalBlindataOutputPort blindataOutputPort) {
        this.odmOutputPort = odmOutputPort;
        this.blindataOutputPort = blindataOutputPort;
    }

    @Override
    public void execute() throws UseCaseExecutionException {
        String fullyQualifiedName = odmOutputPort.getDataProductFullyQualifiedName();

        log.info("{} Deleting data product {} on Blindata.", USE_CASE_PREFIX, fullyQualifiedName);
        blindataOutputPort.deleteDataProduct(fullyQualifiedName);
    }
}
