package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDPhysicalEntityRes;

import java.util.List;

public interface PortStandardDefinitionAnalyzer {

    boolean supportsPortStandardDefinition(PortStandardDefinition portStandardDefinition);

    List<BDPhysicalEntityRes> getBDAssetsFromPortStandardDefinition(
            PortStandardDefinition portStandardDefinition
    );
}
