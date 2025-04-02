package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload;

import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDDataProductPortAssetDetailRes;
import org.opendatamesh.platform.up.metaservice.blindata.services.DataProductPortAssetAnalyzer;

import java.util.List;

class DataProductPortsAndAssetsUploadOdmOutboundPortImpl implements DataProductPortsAndAssetsUploadOdmOutboundPort {

    private final DataProductPortAssetAnalyzer dataProductPortAssetAnalyzer;
    private final DataProductVersionDPDS dataProductVersion;

    public DataProductPortsAndAssetsUploadOdmOutboundPortImpl(DataProductPortAssetAnalyzer dataProductPortAssetAnalyzer, DataProductVersionDPDS dataProductVersion) {
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
