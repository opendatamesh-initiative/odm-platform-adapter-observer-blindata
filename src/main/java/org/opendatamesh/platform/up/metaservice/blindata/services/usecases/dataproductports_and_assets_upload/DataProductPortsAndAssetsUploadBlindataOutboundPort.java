package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDProductPortAssetsRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDSystemRes;

import java.util.Optional;

interface DataProductPortsAndAssetsUploadBlindataOutboundPort {

    Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName);

    void updateDataProductPorts(BDDataProductRes dataProduct);

    void createDataProductAssets(BDProductPortAssetsRes dataProductPortsAssets);

    Optional<BDSystemRes> getSystemDependency(String portDependency);
}

