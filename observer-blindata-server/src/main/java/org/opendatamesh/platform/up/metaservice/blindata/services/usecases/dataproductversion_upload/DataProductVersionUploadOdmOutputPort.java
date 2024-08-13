package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductversion_upload;

import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductPortAssetDetailRes;

import java.util.List;

public interface DataProductVersionUploadOdmOutputPort {
    DataProductVersionDPDS getDataProductVersion();
    List<BDDataProductPortAssetDetailRes> extractBDAssetsFromPorts(List<PortDPDS> ports);
}
