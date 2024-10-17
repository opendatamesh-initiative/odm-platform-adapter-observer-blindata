package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductversion_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductStageRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDProductPortAssetsRes;

import java.util.List;
import java.util.Optional;

public interface DataProductVersionUploadBlindataOutputPort {

    Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName);

    void updateDataProductPorts(BDDataProductRes dataProduct);

    void createDataProductAssets(BDProductPortAssetsRes dataProductPortsAssets);

    void uploadDataProductStages(String dataProductUuid, List<BDDataProductStageRes> stages);
}

