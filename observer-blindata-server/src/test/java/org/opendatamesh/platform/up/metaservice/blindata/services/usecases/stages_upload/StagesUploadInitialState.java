package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.stages_upload;

import lombok.Data;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductStageRes;

import java.util.List;

@Data
public class StagesUploadInitialState {
    private DataProductVersionDPDS dataProductDescriptor;
    private BDDataProductRes existentDataProduct;
    private List<BDDataProductStageRes> dataProductVersionStages;
}
