package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_removal;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.exceptions.BlindataClientException;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;


class DataProductRemoval implements UseCase {

    private static final String USE_CASE_PREFIX = "[DataProductRemoval]";

    private final DataProductRemovalOdmOutboundPort odmOutboundPort;
    private final DataProductRemovalBlindataOutboundPort blindataOutboundPort;

    DataProductRemoval(DataProductRemovalOdmOutboundPort odmOutboundPort, DataProductRemovalBlindataOutboundPort blindataOutboundPort) {
        this.odmOutboundPort = odmOutboundPort;
        this.blindataOutboundPort = blindataOutboundPort;
    }

    @Override
    public void execute() throws UseCaseExecutionException {
        withErrorHandling(() -> {
            String fullyQualifiedName = odmOutboundPort.getDataProductFullyQualifiedName();
            getUseCaseLogger().info(String.format("%s Deleting data product %s on Blindata.", USE_CASE_PREFIX, fullyQualifiedName));
            Optional<BDDataProductRes> dataProduct = blindataOutboundPort.findDataProduct(fullyQualifiedName);

            if (dataProduct.isPresent()) {
                blindataOutboundPort.deleteDataProduct(dataProduct.get().getUuid());
            } else {
                getUseCaseLogger().warn(String.format("%s Data Product with Fully Qualified Name: %s not found on Blindata.", USE_CASE_PREFIX, fullyQualifiedName));
                return;
            }
        });
    }

    private void withErrorHandling(Runnable runnable) throws UseCaseExecutionException {
        try {
            runnable.run();
        } catch (BlindataClientException e) {
            if (e.getCode() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                throw e;
            } else {
                getUseCaseLogger().warn(e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new UseCaseExecutionException(e.getMessage(), e);
        }
    }
}
