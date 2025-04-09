package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDPhysicalEntityRes;

import java.util.Map;

public interface SemanticLinkManager {

    void enrichWithSemanticContext(BDPhysicalEntityRes physicalEntity, Map<String, Object> sContext);

}
