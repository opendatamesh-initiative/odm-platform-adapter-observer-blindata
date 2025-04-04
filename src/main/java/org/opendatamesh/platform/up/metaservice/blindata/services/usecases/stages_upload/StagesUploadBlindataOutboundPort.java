package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.stages_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDDataProductStageRes;

import java.util.List;
import java.util.Optional;

interface StagesUploadBlindataOutboundPort {
    void uploadDataProductStages(String dataProductUuid, List<BDDataProductStageRes> stages);

    Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName);
}
