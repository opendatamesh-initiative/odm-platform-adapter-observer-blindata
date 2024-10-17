package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductversion_upload;

import lombok.Data;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductPortAssetDetailRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductStageRes;

import java.util.List;

@Data
public class DataProductVersionUploadInitialState {
    private BDDataProductRes existentDataProduct;
    private DataProductVersionDPDS dataProductDescriptor;
    private List<BDDataProductPortAssetDetailRes> extractedAssets;
    private List<BDDataProductStageRes> dataProductVersionStages;
}
