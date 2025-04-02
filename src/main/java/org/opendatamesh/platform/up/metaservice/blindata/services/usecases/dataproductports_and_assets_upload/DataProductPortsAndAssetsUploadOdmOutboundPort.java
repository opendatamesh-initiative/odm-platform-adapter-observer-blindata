package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload;

import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDDataProductPortAssetDetailRes;

import java.util.List;

interface DataProductPortsAndAssetsUploadOdmOutboundPort {
    DataProductVersionDPDS getDataProductVersion();

    List<BDDataProductPortAssetDetailRes> extractBDAssetsFromPorts(List<PortDPDS> ports);
}
