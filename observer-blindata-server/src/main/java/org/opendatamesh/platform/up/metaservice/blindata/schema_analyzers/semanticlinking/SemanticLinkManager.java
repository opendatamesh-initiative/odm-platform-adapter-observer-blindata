package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDPhysicalEntityRes;

import java.util.Map;
import java.util.Set;

public interface SemanticLinkManager {

    void enrichWithSemanticContext(BDPhysicalEntityRes physicalEntity, Map<String, Object> sContext);

}
