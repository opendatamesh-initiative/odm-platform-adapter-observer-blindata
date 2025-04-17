package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.quality_upload;

import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.dpds.model.interfaces.Port;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;

import java.util.List;

interface QualityUploadOdmOutboundPort {

    DataProductVersion getDataProductVersion();

    List<QualityCheck> extractQualityChecks(List<Port> ports);
}
