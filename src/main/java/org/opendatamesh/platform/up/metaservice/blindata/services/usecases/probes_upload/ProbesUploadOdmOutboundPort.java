package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.probes_upload;

import org.opendatamesh.dpds.model.DataProductVersion;

import java.util.List;

interface ProbesUploadOdmOutboundPort {

    DataProductVersion getDataProductVersion();

    List<ProbeCandidate> extractProbeCandidates();
}
