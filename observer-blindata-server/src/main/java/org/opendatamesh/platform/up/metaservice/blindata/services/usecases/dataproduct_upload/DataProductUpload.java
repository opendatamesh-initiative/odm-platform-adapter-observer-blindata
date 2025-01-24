package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload;

import org.opendatamesh.dpds.model.info.InfoDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDShortUserRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDStewardshipResponsibilityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDStewardshipRoleRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.exceptions.BlindataClientException;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.util.Optional;

import static org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseRecoverableExceptionContext.getExceptionHandler;

class DataProductUpload implements UseCase {

    private final String USE_CASE_PREFIX = "[DataProductUpload]";

    private final DataProductUploadOdmOutboundPort odmOutboundPort;
    private final DataProductUploadBlindataOutboundPort blindataOutboundPort;
    private final Logger log;

    DataProductUpload(DataProductUploadOdmOutboundPort odmOutboundPort, DataProductUploadBlindataOutboundPort blindataOutboundPort, Logger log) {
        this.odmOutboundPort = odmOutboundPort;
        this.blindataOutboundPort = blindataOutboundPort;
        this.log = log;
        getExceptionHandler().setLogger(log);
    }

    DataProductUpload(DataProductUploadOdmOutboundPort odmOutboundPort, DataProductUploadBlindataOutboundPort blindataOutboundPort) {
        this.odmOutboundPort = odmOutboundPort;
        this.blindataOutboundPort = blindataOutboundPort;
        this.log = LoggerFactory.getLogger(this.getClass());
        getExceptionHandler().setLogger(log);
    }

    @Override
    public void execute() throws UseCaseExecutionException {
        withErrorHandling(() -> {
            InfoDPDS odmDataProductInfo = odmOutboundPort.getDataProductInfo();
            validateDataProductInfo(odmDataProductInfo);
            Optional<BDDataProductRes> blindataDataProduct = blindataOutboundPort.findDataProduct(odmDataProductInfo.getFullyQualifiedName());
            if (blindataDataProduct.isEmpty()) {
                createDataProduct();
            } else {
                updateDataProduct(blindataDataProduct.get());
            }
        });
    }

    private void createDataProduct() {
        BDDataProductRes blindataDataProduct = odmToBlindataDataProduct(odmOutboundPort.getDataProductInfo());
        blindataDataProduct = blindataOutboundPort.createDataProduct(blindataDataProduct);
        log.info("{} Data product: {} created with uuid: {} on Blindata", USE_CASE_PREFIX, odmOutboundPort.getDataProductInfo().getFullyQualifiedName(), blindataDataProduct.getUuid());
        assignResponsibilityToDataProduct(blindataDataProduct);
    }

    private void assignResponsibilityToDataProduct(BDDataProductRes blindataDataProduct) {
        if (odmOutboundPort.getDataProductInfo().getOwner() == null) {
            log.info("{} Data product: {}, owner not defined, skipping responsibilities assignment.", USE_CASE_PREFIX, odmOutboundPort.getDataProductInfo().getFullyQualifiedName());
            return;
        }
        Optional<BDShortUserRes> blindataUser = blindataOutboundPort.findUser(odmOutboundPort.getDataProductInfo().getOwner().getId());
        if (blindataUser.isEmpty()) {
            getExceptionHandler().warn(String.format("%s Impossible to assign responsibility on data product: %s, user: %s not found on Blindata.", USE_CASE_PREFIX, odmOutboundPort.getDataProductInfo().getFullyQualifiedName(), odmOutboundPort.getDataProductInfo().getOwner().getId()));
            return;
        }

        BDStewardshipRoleRes dataProductRole = blindataOutboundPort.findDataProductRole(blindataOutboundPort.getDefaultRoleUuid());
        if (dataProductRole == null) {
            getExceptionHandler().warn(String.format("%s Impossible to assign responsibility on data product: %s, role: %s not found on Blindata.", USE_CASE_PREFIX, odmOutboundPort.getDataProductInfo().getFullyQualifiedName(), blindataOutboundPort.getDefaultRoleUuid()));
            return;
        }

        Optional<BDStewardshipResponsibilityRes> existentResponsibility = blindataOutboundPort.findDataProductResponsibilities(blindataUser.get().getUuid(), blindataDataProduct.getUuid());
        if (existentResponsibility.isPresent()) {
            log.info("{} Responsibility on data product: {}, for the user: {} is already present on Blindata.", USE_CASE_PREFIX, odmOutboundPort.getDataProductInfo().getFullyQualifiedName(), odmOutboundPort.getDataProductInfo().getOwner().getId());
        } else {
            blindataOutboundPort.createDataProductResponsibility(dataProductRole, blindataUser.get(), blindataDataProduct);
            log.info("{} Assigned responsibility on data product: {}, for the user: {} on Blindata.", USE_CASE_PREFIX, odmOutboundPort.getDataProductInfo().getFullyQualifiedName(), odmOutboundPort.getDataProductInfo().getOwner().getId());
        }
    }

    private BDDataProductRes odmToBlindataDataProduct(InfoDPDS odmDataProduct) {
        BDDataProductRes blindataDataProduct = new BDDataProductRes();
        blindataDataProduct.setName(odmDataProduct.getName());
        blindataDataProduct.setDisplayName(odmDataProduct.getDisplayName());
        blindataDataProduct.setDomain(odmDataProduct.getDomain());
        blindataDataProduct.setIdentifier(odmDataProduct.getFullyQualifiedName());
        blindataDataProduct.setVersion(odmDataProduct.getVersionNumber());
        blindataDataProduct.setDescription(odmDataProduct.getDescription());

        if (!StringUtils.hasText(odmDataProduct.getName())) {
            String name = extractNameFromFQN(odmDataProduct.getFullyQualifiedName());
            blindataDataProduct.setName(name);
            blindataDataProduct.setDisplayName(name);
        }

        if (!StringUtils.hasText(odmDataProduct.getVersionNumber())) {
            blindataDataProduct.setVersion("0.0.0");
            blindataDataProduct.setProductStatus("DRAFT");
        }

        return blindataDataProduct;
    }


    private void updateDataProduct(BDDataProductRes oldBdDataProduct) {
        BDDataProductRes newBdDataProduct = odmToBlindataDataProduct(odmOutboundPort.getDataProductInfo());
        newBdDataProduct.setUuid(oldBdDataProduct.getUuid());

        newBdDataProduct.addOldAdditionalProperties(oldBdDataProduct);
        newBdDataProduct = blindataOutboundPort.updateDataProduct(newBdDataProduct);
        log.info("{} Data product: {} with uuid: {} updated on Blindata", USE_CASE_PREFIX, odmOutboundPort.getDataProductInfo().getFullyQualifiedName(), newBdDataProduct.getUuid());
        assignResponsibilityToDataProduct(newBdDataProduct);
    }

    private String extractNameFromFQN(String fullyQualifiedName) {
        String[] parts = fullyQualifiedName.split("[.:/\\\\]");
        // The product name is assumed to be the last part of the fully qualified name
        if (parts.length > 1) {
            return parts[parts.length - 1];
        } else {
            return null;
        }
    }

    private void validateDataProductInfo(InfoDPDS odmDataProductInfo) {
        if (odmDataProductInfo == null) {
            getExceptionHandler().warn(String.format("%s Missing odm data product info", USE_CASE_PREFIX));
            return;
        }
        if (!StringUtils.hasText(odmDataProductInfo.getFullyQualifiedName())) {
            getExceptionHandler().warn(String.format("%s Missing odm data product info fully qualified name.", USE_CASE_PREFIX));
        }
        if (!StringUtils.hasText(odmDataProductInfo.getDomain())) {
            getExceptionHandler().warn(String.format("%s Missing odm data product info domain.", USE_CASE_PREFIX));
        }
    }

    private void withErrorHandling(Runnable runnable) throws UseCaseExecutionException {
        try {
            runnable.run();
        } catch (BlindataClientException e) {
            if (e.getCode() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                throw e;
            } else {
                getExceptionHandler().warn(e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new UseCaseExecutionException(e.getMessage(), e);
        }
    }
}
