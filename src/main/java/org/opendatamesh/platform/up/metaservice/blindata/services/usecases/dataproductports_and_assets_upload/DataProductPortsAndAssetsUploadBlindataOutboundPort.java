package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDSystemRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDProductPortAssetsRes;

import java.util.Optional;

interface DataProductPortsAndAssetsUploadBlindataOutboundPort {

    Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName);

    void updateDataProduct(BDDataProductRes bdDataProductRes);

    void updateDataProductPorts(BDDataProductRes dataProduct);

    void createDataProductAssets(BDProductPortAssetsRes dataProductPortsAssets);

    Optional<BDSystemRes> findExistingSystem(String systemName);

    Optional<String> findSystemName(String portDependency);

    String getDataProductAdditionalPropertiesRegex();
}

