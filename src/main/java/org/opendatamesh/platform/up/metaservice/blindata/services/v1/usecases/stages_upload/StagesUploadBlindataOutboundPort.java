package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.stages_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductStageRes;

import java.util.List;
import java.util.Optional;

interface StagesUploadBlindataOutboundPort {
    void uploadDataProductStages(String dataProductUuid, List<BDDataProductStageRes> stages);

    Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName);
}
