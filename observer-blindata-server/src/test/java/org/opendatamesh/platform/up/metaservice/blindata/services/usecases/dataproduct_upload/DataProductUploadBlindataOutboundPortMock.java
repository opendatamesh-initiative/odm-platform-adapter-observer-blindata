package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDShortUserRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDStewardshipResponsibilityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDStewardshipRoleRes;

import java.util.Optional;

public class DataProductUploadBlindataOutboundPortMock implements DataProductUploadBlindataOutboundPort {

    private final DataProductUploadInitialState initialState;

    DataProductUploadBlindataOutboundPortMock(DataProductUploadInitialState initialState) {
        this.initialState = initialState;
    }

    @Override
    public String getDefaultRoleUuid() {
        return initialState.getDefaultRoleUuid();
    }

    @Override
    public Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName) {
        return Optional.ofNullable(initialState.getExistentDataProduct());
    }

    @Override
    public BDDataProductRes createDataProduct(BDDataProductRes dataProduct) {
        return dataProduct;
    }

    @Override
    public BDDataProductRes updateDataProduct(BDDataProductRes dataProduct) {
        return dataProduct;
    }

    @Override
    public BDStewardshipRoleRes findDataProductRole(String roleUuid) {
        return initialState.getDataProductRole();
    }

    @Override
    public Optional<BDShortUserRes> findUser(String username) {
        return Optional.ofNullable(initialState.getUser());
    }

    @Override
    public Optional<BDStewardshipResponsibilityRes> findDataProductResponsibilities(String userUuid, String dataProductUuid) {
        return Optional.ofNullable(initialState.getDataProductResponsibility());
    }

    @Override
    public void createDataProductResponsibility(BDStewardshipRoleRes role, BDShortUserRes user, BDDataProductRes dataProduct) {
        //DO NOTHING
    }
}
