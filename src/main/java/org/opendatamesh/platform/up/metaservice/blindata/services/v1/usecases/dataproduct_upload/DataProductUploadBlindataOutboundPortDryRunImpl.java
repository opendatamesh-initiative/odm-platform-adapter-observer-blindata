package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.dataproduct_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDShortUserRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDStewardshipResponsibilityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDStewardshipRoleRes;

import java.util.Optional;

class DataProductUploadBlindataOutboundPortDryRunImpl implements DataProductUploadBlindataOutboundPort {
    private final DataProductUploadBlindataOutboundPort outboundPort;


    public DataProductUploadBlindataOutboundPortDryRunImpl(DataProductUploadBlindataOutboundPort outboundPort) {
        this.outboundPort = outboundPort;
    }

    @Override
    public String getDefaultRoleUuid() {
        return this.outboundPort.getDefaultRoleUuid();
    }

    @Override
    public Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName) {
        return outboundPort.findDataProduct(fullyQualifiedName);
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
        return outboundPort.findDataProductRole(roleUuid);
    }

    @Override
    public Optional<BDShortUserRes> findUser(String username) {
        return outboundPort.findUser(username);
    }

    @Override
    public Optional<BDStewardshipResponsibilityRes> findDataProductResponsibilities(String userUuid, String dataProductUuid) {
        return outboundPort.findDataProductResponsibilities(userUuid, dataProductUuid);
    }

    @Override
    public void createDataProductResponsibility(BDStewardshipRoleRes role, BDShortUserRes user, BDDataProductRes dataProduct) {
        //DO NOTHING
    }

    @Override
    public String getDataProductAdditionalPropertiesRegex() {
        return outboundPort.getDataProductAdditionalPropertiesRegex();
    }
}
