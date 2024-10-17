package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.stages_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductStageRes;

import java.util.List;
import java.util.Optional;

interface StagesUploadBlindataOutputPort {
    void uploadDataProductStages(String dataProductUuid, List<BDDataProductStageRes> stages);

    Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName);
}
