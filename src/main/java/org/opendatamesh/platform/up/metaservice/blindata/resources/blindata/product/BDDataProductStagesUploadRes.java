package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product;

import java.util.List;

public class BDDataProductStagesUploadRes {
    private List<BDDataProductStageRes> stages;
    private String dataProductUuid;

    public List<BDDataProductStageRes> getStages() {
        return stages;
    }

    public void setStages(List<BDDataProductStageRes> stages) {
        this.stages = stages;
    }

    public String getDataProductUuid() {
        return dataProductUuid;
    }

    public void setDataProductUuid(String dataProductUuid) {
        this.dataProductUuid = dataProductUuid;
    }
}
