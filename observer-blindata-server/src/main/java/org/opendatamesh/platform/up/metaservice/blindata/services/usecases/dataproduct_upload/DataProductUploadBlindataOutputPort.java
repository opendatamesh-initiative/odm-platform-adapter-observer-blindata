package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDShortUserRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDStewardshipResponsibilityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDStewardshipRoleRes;

import java.util.Optional;

interface DataProductUploadBlindataOutputPort {

    String getDefaultRoleUuid();

    Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName);

    BDDataProductRes createDataProduct(BDDataProductRes dataProduct);

    BDDataProductRes updateDataProduct(BDDataProductRes dataProduct);

    BDStewardshipRoleRes findDataProductRole(String roleUuid);

    Optional<BDShortUserRes> findUser(String username);

    Optional<BDStewardshipResponsibilityRes> findDataProductResponsibilities(String userUuid, String dataProductUuid);

    void createDataProductResponsibility(BDStewardshipRoleRes role, BDShortUserRes user, BDDataProductRes dataProduct);
}
