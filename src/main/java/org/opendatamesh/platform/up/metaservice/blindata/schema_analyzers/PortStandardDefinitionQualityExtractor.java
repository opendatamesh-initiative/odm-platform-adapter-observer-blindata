package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers;

import org.opendatamesh.dpds.model.core.StandardDefinition;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;

import java.util.List;

public interface PortStandardDefinitionQualityExtractor {

    boolean supports(StandardDefinition portStandardDefinition);

    List<QualityCheck> extractQualityChecks(StandardDefinition portStandardDefinition);
}
