package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload;

import lombok.Data;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductPortAssetDetailRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;

import java.util.List;

@Data
public class DataProductVersionUploadInitialState {
    private BDDataProductRes existentDataProduct;
    private DataProductVersionDPDS dataProductDescriptor;
    private List<BDDataProductPortAssetDetailRes> extractedAssets;
}
