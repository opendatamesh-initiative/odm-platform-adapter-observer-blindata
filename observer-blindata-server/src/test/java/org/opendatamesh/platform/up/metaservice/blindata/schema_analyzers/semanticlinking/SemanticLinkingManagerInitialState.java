package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDPhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.LogicalFieldSemanticLinkRes;

import java.util.Map;

@Data
public class SemanticLinkingManagerInitialState {

    private BDPhysicalEntityRes physicalEntityRes;

    @JsonProperty("s-context")
    private Map<String, Object> sContext;

    private LogicalFieldSemanticLinkRes semanticLinkRes;
}