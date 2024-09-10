package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductversion_upload;

import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductPortAssetDetailRes;
import org.opendatamesh.platform.up.metaservice.blindata.services.DataProductPortAssetAnalyzer;

import java.util.List;

class DataProductVersionUploadOdmOutputPortImpl implements DataProductVersionUploadOdmOutputPort {

    private final DataProductPortAssetAnalyzer dataProductPortAssetAnalyzer;

    private final DataProductVersionDPDS dataProductVersion;

    public DataProductVersionUploadOdmOutputPortImpl(DataProductPortAssetAnalyzer dataProductPortAssetAnalyzer, DataProductVersionDPDS dataProductVersion) {
        this.dataProductPortAssetAnalyzer = dataProductPortAssetAnalyzer;
        this.dataProductVersion = dataProductVersion;
    }

    @Override
    public DataProductVersionDPDS getDataProductVersion() {
        return dataProductVersion;
    }

    @Override
    public List<BDDataProductPortAssetDetailRes> extractBDAssetsFromPorts(List<PortDPDS> ports) {
        return dataProductPortAssetAnalyzer.extractPhysicalResourcesFromPorts(ports);
    }
}
