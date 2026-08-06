package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.probes_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes.BDQualityProbesConnectionRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes.BDQualityProbesDefinitionRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes.BDQualityProbesProjectRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes.BDQualityProbesTagRes;

import java.util.Optional;

interface ProbesUploadBlindataOutboundPort {

    Optional<BDQualityProbesProjectRes> findProbeProjectByName(String projectName);

    BDQualityProbesProjectRes createProbeProject(BDQualityProbesProjectRes project);

    Optional<BDQualityProbesDefinitionRes> findProbeDefinitionByProjectAndName(String projectUuid, String probeName);

    BDQualityProbesDefinitionRes createProbeDefinition(BDQualityProbesDefinitionRes definition);

    BDQualityProbesDefinitionRes overwriteProbeDefinition(String rootUuid, BDQualityProbesDefinitionRes definition);

    Optional<BDQualityProbesConnectionRes> findProbeConnectionByName(String connectionName);

    Optional<BDQualityProbesTagRes> findTagByProjectAndName(String projectUuid, String tagName);

    void deleteTag(String tagUuid);

    BDQualityProbesTagRes createTag(BDQualityProbesTagRes tag);
}
