package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.dataproduct_upload;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdStewardshipClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdUserClient;
import org.opendatamesh.platform.up.metaservice.blindata.configurations.BdDataProductConfig;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDShortUserRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDStewardshipResponsibilityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDStewardshipRoleRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;

import java.util.Date;
import java.util.Optional;

class DataProductUploadBlindataOutboundPortImpl implements DataProductUploadBlindataOutboundPort {
    private final BdUserClient bdUserClient;
    private final BdDataProductClient bdDataProductClient;
    private final BdStewardshipClient bdStewardshipClient;
    private final BdDataProductConfig dataProductConfig;
    private final String defaultRoleUuid;

    public DataProductUploadBlindataOutboundPortImpl(BdUserClient bdUserClient, BdDataProductClient bdDataProductClient, BdStewardshipClient bdStewardshipClient, BdDataProductConfig dataProductConfig, String defaultRoleUuid) {
        this.bdUserClient = bdUserClient;
        this.bdDataProductClient = bdDataProductClient;
        this.bdStewardshipClient = bdStewardshipClient;
        this.dataProductConfig = dataProductConfig;
        this.defaultRoleUuid = defaultRoleUuid;
    }

    @Override
    public String getDefaultRoleUuid() {
        return this.defaultRoleUuid;
    }

    @Override
    public Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName) {
        return bdDataProductClient.getDataProduct(fullyQualifiedName);
    }

    @Override
    public BDDataProductRes createDataProduct(BDDataProductRes dataProduct) {
        return bdDataProductClient.createDataProduct(dataProduct);
    }

    @Override
    public BDDataProductRes updateDataProduct(BDDataProductRes dataProduct) {
        return bdDataProductClient.patchDataProduct(dataProduct);
    }

    @Override
    public BDStewardshipRoleRes findDataProductRole(String roleUuid) {
        return bdStewardshipClient.getRole(roleUuid);
    }

    @Override
    public Optional<BDShortUserRes> findUser(String username) {
        return bdUserClient.getBlindataUser(username);
    }

    @Override
    public Optional<BDStewardshipResponsibilityRes> findDataProductResponsibilities(String userUuid, String dataProductUuid) {
        return bdStewardshipClient.getActiveResponsibility(userUuid, dataProductUuid);
    }

    @Override
    public void createDataProductResponsibility(BDStewardshipRoleRes role, BDShortUserRes user, BDDataProductRes dataProduct) {
        BDStewardshipResponsibilityRes responsibilityRes = new BDStewardshipResponsibilityRes();
        responsibilityRes.setStewardshipRole(role);
        responsibilityRes.setUser(user);
        responsibilityRes.setResourceIdentifier(dataProduct.getUuid());
        responsibilityRes.setResourceName(dataProduct.getName());
        responsibilityRes.setStartDate(new Date());
        bdStewardshipClient.createResponsibility(responsibilityRes);
    }

    @Override
    public String getDataProductAdditionalPropertiesRegex() {
        return dataProductConfig.getAdditionalPropertiesRegex();
    }
}
