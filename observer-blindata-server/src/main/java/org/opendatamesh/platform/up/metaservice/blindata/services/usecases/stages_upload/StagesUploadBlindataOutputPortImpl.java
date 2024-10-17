package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.stages_upload;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductStageRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductStagesUploadRes;

import java.util.List;
import java.util.Optional;

class StagesUploadBlindataOutputPortImpl implements StagesUploadBlindataOutputPort {
    private final BDDataProductClient bdDataProductClient;

    StagesUploadBlindataOutputPortImpl(BDDataProductClient bdDataProductClient) {
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
