package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.stages_upload;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDDataProductStageRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDDataProductStagesUploadRes;

import java.util.List;
import java.util.Optional;

class StagesUploadBlindataOutboundPortImpl implements StagesUploadBlindataOutboundPort {
    private final BDDataProductClient bdDataProductClient;

    StagesUploadBlindataOutboundPortImpl(BDDataProductClient bdDataProductClient) {
        this.bdDataProductClient = bdDataProductClient;
    }

    public void uploadDataProductStages(String dataProductUuid, List<BDDataProductStageRes> stages) {
        BDDataProductStagesUploadRes stagesUploadRes = new BDDataProductStagesUploadRes();
        stagesUploadRes.setDataProductUuid(dataProductUuid);
        stagesUploadRes.setStages(stages);
        bdDataProductClient.uploadStages(stagesUploadRes);
    }

    @Override
    public Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName) {
        return bdDataProductClient.getDataProduct(fullyQualifiedName);
    }
}
