package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.dataproductports_and_assets_upload;

import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.dpds.model.interfaces.Port;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductPortAssetDetailRes;

import java.util.List;

interface DataProductPortsAndAssetsUploadOdmOutboundPort {
    DataProductVersion getDataProductVersion();

    List<BDDataProductPortAssetDetailRes> extractBDAssetsFromPorts(List<Port> ports);
}
