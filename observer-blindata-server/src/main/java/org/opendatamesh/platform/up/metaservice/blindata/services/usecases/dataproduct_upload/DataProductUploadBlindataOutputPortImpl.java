package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDStewardshipClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDUserClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDShortUserRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDStewardshipResponsibilityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDStewardshipRoleRes;

import java.util.Date;
import java.util.Optional;

class DataProductUploadBlindataOutputPortImpl implements DataProductUploadBlindataOutputPort {
    private final BDUserClient bdUserClient;
    private final BDDataProductClient bdDataProductClient;
    private final BDStewardshipClient bdStewardshipClient;
    private final String defaultRoleUuid;

    public DataProductUploadBlindataOutputPortImpl(BDUserClient bdUserClient, BDDataProductClient bdDataProductClient, BDStewardshipClient bdStewardshipClient, String defaultRoleUuid) {
        this.bdUserClient = bdUserClient;
        this.bdDataProductClient = bdDataProductClient;
        this.bdStewardshipClient = bdStewardshipClient;
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
        return bdDataProductClient.updateDataProduct(dataProduct);
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
}
