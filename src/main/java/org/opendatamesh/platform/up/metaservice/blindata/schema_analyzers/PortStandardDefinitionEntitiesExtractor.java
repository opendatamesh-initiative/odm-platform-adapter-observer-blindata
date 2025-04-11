package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers;

import org.opendatamesh.dpds.model.core.StandardDefinition;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalEntityRes;

import java.util.List;

public interface PortStandardDefinitionEntitiesExtractor {

    boolean supports(StandardDefinition portStandardDefinition);

    List<BDPhysicalEntityRes> extractEntities(StandardDefinition portStandardDefinition);
}
