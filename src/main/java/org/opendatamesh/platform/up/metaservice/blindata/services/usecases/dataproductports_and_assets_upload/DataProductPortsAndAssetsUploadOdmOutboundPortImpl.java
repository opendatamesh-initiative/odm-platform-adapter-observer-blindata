package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload;

import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.dpds.model.interfaces.Port;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDDataProductPortAssetDetailRes;
import org.opendatamesh.platform.up.metaservice.blindata.services.DataProductPortAssetAnalyzer;

import java.util.List;

class DataProductPortsAndAssetsUploadOdmOutboundPortImpl implements DataProductPortsAndAssetsUploadOdmOutboundPort {

    private final DataProductPortAssetAnalyzer dataProductPortAssetAnalyzer;
    private final DataProductVersion dataProductVersion;

    public DataProductPortsAndAssetsUploadOdmOutboundPortImpl(DataProductPortAssetAnalyzer dataProductPortAssetAnalyzer, DataProductVersion dataProductVersion) {
        this.dataProductPortAssetAnalyzer = dataProductPortAssetAnalyzer;
        this.dataProductVersion = dataProductVersion;
    }

    @Override
    public DataProductVersion getDataProductVersion() {
        return dataProductVersion;
    }

    @Override
    public List<BDDataProductPortAssetDetailRes> extractBDAssetsFromPorts(List<Port> ports) {
        return dataProductPortAssetAnalyzer.extractPhysicalResourcesFromPorts(ports);
    }
}
