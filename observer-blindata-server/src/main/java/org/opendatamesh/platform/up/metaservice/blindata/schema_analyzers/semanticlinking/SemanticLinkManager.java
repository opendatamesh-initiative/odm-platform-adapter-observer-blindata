package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDPhysicalEntityRes;

import java.util.Map;

public interface SemanticLinkManager {

    void enrichPhysicalPropertiesWithSemanticLinks(Map<String, Object> sContext, BDPhysicalEntityRes physicalEntity);
}
