package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.stages_upload;

import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDDataProductStageRes;

import java.util.List;

interface StagesUploadOdmOutboundPort {
    List<BDDataProductStageRes> extractDataProductStages();

    DataProductVersionDPDS getDataProductVersion();
}
