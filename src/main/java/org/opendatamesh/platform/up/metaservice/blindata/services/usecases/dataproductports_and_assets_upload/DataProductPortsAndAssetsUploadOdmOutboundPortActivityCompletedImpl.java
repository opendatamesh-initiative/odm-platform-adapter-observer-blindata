package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload;

import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.client.odm.OdmRegistryClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDDataProductPortAssetDetailRes;
import org.opendatamesh.platform.up.metaservice.blindata.services.DataProductPortAssetAnalyzer;

import java.util.List;

class DataProductPortsAndAssetsUploadOdmOutboundPortActivityCompletedImpl implements DataProductPortsAndAssetsUploadOdmOutboundPort {
    private final DataProductPortAssetAnalyzer dataProductPortAssetAnalyzer;
    private final DataProductVersionDPDS initialDataProductVersion;
    private DataProductVersionDPDS loadedDataProductVersion;
    private final OdmRegistryClient registryClient;

    public DataProductPortsAndAssetsUploadOdmOutboundPortActivityCompletedImpl(DataProductPortAssetAnalyzer dataProductPortAssetAnalyzer, DataProductVersionDPDS dataProductVersion, OdmRegistryClient registryClient) {
        this.dataProductPortAssetAnalyzer = dataProductPortAssetAnalyzer;
        this.initialDataProductVersion = dataProductVersion;
        this.registryClient = registryClient;
    }

    @Override
    public DataProductVersionDPDS getDataProductVersion() {
        if (loadedDataProductVersion == null) {
            loadedDataProductVersion = registryClient.getDataProductVersion(initialDataProductVersion.getInfo().getDataProductId(), initialDataProductVersion.getInfo().getVersionNumber());
        }
        return loadedDataProductVersion;
    }

    @Override
    public List<BDDataProductPortAssetDetailRes> extractBDAssetsFromPorts(List<PortDPDS> ports) {
        return dataProductPortAssetAnalyzer.extractPhysicalResourcesFromPorts(ports);
    }
}
