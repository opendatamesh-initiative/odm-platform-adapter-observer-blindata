package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.stages_upload;

import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductStageRes;

import java.util.List;

interface StagesUploadOdmOutputPort {
    List<BDDataProductStageRes> extractDataProductStages();

    DataProductVersionDPDS getDataProductVersion();
}
