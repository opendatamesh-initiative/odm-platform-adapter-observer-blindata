package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.quality_upload;

import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.dpds.model.interfaces.Port;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.DataProductPortAssetAnalyzer;

import java.util.List;

class QualityUploadOdmOutboundPortImpl implements QualityUploadOdmOutboundPort {

    private final DataProductPortAssetAnalyzer dataProductPortAssetAnalyzer;
    private final DataProductVersion dataProductVersion;

    QualityUploadOdmOutboundPortImpl(DataProductPortAssetAnalyzer dataProductPortAssetAnalyzer, DataProductVersion dataProductVersion) {
        this.dataProductPortAssetAnalyzer = dataProductPortAssetAnalyzer;
        this.dataProductVersion = dataProductVersion;
    }

    @Override
    public DataProductVersion getDataProductVersion() {
        return dataProductVersion;
    }

    @Override
    public List<QualityCheck> extractQualityChecks(List<Port> ports) {
        return dataProductPortAssetAnalyzer.extractQualityChecksFromPorts(ports);
    }
}


