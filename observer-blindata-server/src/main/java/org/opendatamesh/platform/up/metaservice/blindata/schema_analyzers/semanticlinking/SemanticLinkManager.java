package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDPhysicalEntityRes;

import java.util.Map;

public interface SemanticLinkManager {

    void enrichPhysicalFieldsWithSemanticLinks(Map<String, Object> sContext, BDPhysicalEntityRes physicalEntity);

    void linkPhysicalEntityToDataCategory(Map<String, Object> sContext, BDPhysicalEntityRes physicalEntity);
}
