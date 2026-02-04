package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.stages_upload;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductStageRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductStagesUploadRes;

import java.util.List;
import java.util.Optional;

class StagesUploadBlindataOutboundPortImpl implements StagesUploadBlindataOutboundPort {
    private final BdDataProductClient bdDataProductClient;

    StagesUploadBlindataOutboundPortImpl(BdDataProductClient bdDataProductClient) {
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
