package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDShortUserRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDStewardshipResponsibilityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDStewardshipRoleRes;

import java.util.Optional;

interface DataProductUploadBlindataOutboundPort {

    String getDefaultRoleUuid();

    Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName);

    BDDataProductRes createDataProduct(BDDataProductRes dataProduct);

    BDDataProductRes updateDataProduct(BDDataProductRes dataProduct);

    BDStewardshipRoleRes findDataProductRole(String roleUuid);

    Optional<BDShortUserRes> findUser(String username);

    Optional<BDStewardshipResponsibilityRes> findDataProductResponsibilities(String userUuid, String dataProductUuid);

    void createDataProductResponsibility(BDStewardshipRoleRes role, BDShortUserRes user, BDDataProductRes dataProduct);
}
