package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.stages_upload;

import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductStageRes;

import java.util.List;

interface StagesUploadOdmOutboundPort {
    List<BDDataProductStageRes> extractDataProductStages();

    DataProductVersion getDataProductVersion();
}
