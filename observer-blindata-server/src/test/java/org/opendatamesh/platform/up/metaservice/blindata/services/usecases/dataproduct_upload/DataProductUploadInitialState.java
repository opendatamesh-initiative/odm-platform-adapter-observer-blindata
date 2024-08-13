package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload;

import lombok.Data;
import org.opendatamesh.dpds.model.info.InfoDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDShortUserRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDStewardshipResponsibilityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDStewardshipRoleRes;

@Data
class DataProductUploadInitialState {
    private InfoDPDS dataProductInfo;
    private BDDataProductRes existentDataProduct;
    private BDShortUserRes user;
    private String defaultRoleUuid;
    private BDStewardshipRoleRes dataProductRole;
    private BDStewardshipResponsibilityRes dataProductResponsibility;
}
