package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BdProbesClient {
    Page<BDQualityProbesProjectRes> getProjects(Pageable pageable, QualityProbesProjectSearchOptions filters);

    BDQualityProbesProjectRes createProject(BDQualityProbesProjectRes project);

    Page<BDQualityProbesDefinitionRes> getDefinitions(Pageable pageable, QualityProbesDefinitionSearchOptions filters);

    BDQualityProbesDefinitionRes createDefinition(BDQualityProbesDefinitionRes definition);

    BDQualityProbesDefinitionRes overwriteDefinition(String rootUuid, BDQualityProbesDefinitionRes definition);

    Page<BDQualityProbesConnectionRes> getConnections(Pageable pageable, QualityProbesConnectionSearchOptions filters);

    Page<BDQualityProbesTagRes> getTags(Pageable pageable, QualityProbesTagSearchOptions filters);

    BDQualityProbesTagRes createTag(BDQualityProbesTagRes tag);

    void deleteTag(String uuid);
}
