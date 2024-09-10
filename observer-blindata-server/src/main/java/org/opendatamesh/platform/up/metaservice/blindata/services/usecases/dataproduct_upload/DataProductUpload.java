package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload;

import lombok.extern.slf4j.Slf4j;
import org.opendatamesh.dpds.model.info.InfoDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDShortUserRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDStewardshipResponsibilityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDStewardshipRoleRes;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;

import java.util.Optional;

@Slf4j
class DataProductUpload implements UseCase {

    private final String USE_CASE_PREFIX = "[DataProductUpload]";

    private final DataProductUploadOdmOutputPort odmOutputPort;
    private final DataProductUploadBlindataOutputPort blindataOutputPort;

    public DataProductUpload(DataProductUploadOdmOutputPort odmOutputPort, DataProductUploadBlindataOutputPort blindataOutputPort) {
        this.odmOutputPort = odmOutputPort;
        this.blindataOutputPort = blindataOutputPort;
    }

    @Override
    public void execute() throws UseCaseExecutionException {
        try {
            InfoDPDS odmDataProductInfo = odmOutputPort.getDataProductInfo();
            if (odmDataProductInfo == null) {
                log.warn("{} Missing odm data product info", USE_CASE_PREFIX);
                return;
            }
            Optional<BDDataProductRes> blindataDataProduct = blindataOutputPort.findDataProduct(odmDataProductInfo.getFullyQualifiedName());
            if (blindataDataProduct.isEmpty()) {
                createDataProduct();
            } else {
                updateDataProduct(blindataDataProduct.get().getUuid());
            }
        } catch (Exception e) {
            throw new UseCaseExecutionException(e.getMessage(), e);
        }
    }

    private void createDataProduct() {
        BDDataProductRes blindataDataProduct = odmToBlindataDataProduct(odmOutputPort.getDataProductInfo());
        blindataDataProduct = blindataOutputPort.createDataProduct(blindataDataProduct);
        log.info("{} Data product: {} created with uuid: {} on Blindata", USE_CASE_PREFIX, odmOutputPort.getDataProductInfo().getFullyQualifiedName(), blindataDataProduct.getUuid());
        assignResponsibilityToDataProduct(blindataDataProduct);
    }

    private void assignResponsibilityToDataProduct(BDDataProductRes blindataDataProduct) {
        if (odmOutputPort.getDataProductInfo().getOwner() == null) {
            log.info("{} Data product: {}, owner not defined, skipping responsibilities assignment.", USE_CASE_PREFIX, odmOutputPort.getDataProductInfo().getFullyQualifiedName());
            return;
        }
        BDStewardshipRoleRes dataProductRole = blindataOutputPort.findDataProductRole(blindataOutputPort.getDefaultRoleUuid());
        Optional<BDShortUserRes> blindataUser = blindataOutputPort.findUser(odmOutputPort.getDataProductInfo().getOwner().getId());
        if (dataProductRole == null) {
            log.warn("{} Impossible to assign responsibility on data product: {}, role: {} not found on Blindata.", USE_CASE_PREFIX, odmOutputPort.getDataProductInfo().getFullyQualifiedName(), blindataOutputPort.getDefaultRoleUuid());
            return;
        }
        if (blindataUser.isEmpty()) {
            log.warn("{} Impossible to assign responsibility on data product: {}, user: {} not found on Blindata.", USE_CASE_PREFIX, odmOutputPort.getDataProductInfo().getFullyQualifiedName(), odmOutputPort.getDataProductInfo().getOwner().getId());
            return;
        }
        Optional<BDStewardshipResponsibilityRes> existentResponsibility = blindataOutputPort.findDataProductResponsibilities(blindataUser.get().getUuid(), blindataDataProduct.getUuid());
        if (existentResponsibility.isPresent()) {
            log.info("{} Responsibility on data product: {}, for the user: {} is already present on Blindata.", USE_CASE_PREFIX, odmOutputPort.getDataProductInfo().getFullyQualifiedName(), odmOutputPort.getDataProductInfo().getOwner().getId());
            return;
        }
        blindataOutputPort.createDataProductResponsibility(dataProductRole, blindataUser.get(), blindataDataProduct);
        log.info("{} Assigned responsibility on data product: {}, for the user: {} on Blindata.", USE_CASE_PREFIX, odmOutputPort.getDataProductInfo().getFullyQualifiedName(), odmOutputPort.getDataProductInfo().getOwner().getId());
    }

    private BDDataProductRes odmToBlindataDataProduct(InfoDPDS odmDataProduct) {
        BDDataProductRes blindataDataProduct = new BDDataProductRes();
        blindataDataProduct.setName(odmDataProduct.getName());
        blindataDataProduct.setDomain(odmDataProduct.getDomain());
        blindataDataProduct.setIdentifier(odmDataProduct.getFullyQualifiedName());
        blindataDataProduct.setVersion(odmDataProduct.getVersionNumber());
        blindataDataProduct.setDisplayName(odmDataProduct.getDisplayName());
        blindataDataProduct.setDescription(odmDataProduct.getDescription());
        return blindataDataProduct;
    }


    private void updateDataProduct(String blindataDataProductUuid) {
        BDDataProductRes newDataProduct = odmToBlindataDataProduct(odmOutputPort.getDataProductInfo());
        newDataProduct.setUuid(blindataDataProductUuid);

        newDataProduct = blindataOutputPort.updateDataProduct(newDataProduct);
        log.info("{} Data product: {} with uuid: {} updated on Blindata", USE_CASE_PREFIX, odmOutputPort.getDataProductInfo().getFullyQualifiedName(), newDataProduct.getUuid());
        assignResponsibilityToDataProduct(newDataProduct);
    }
}
