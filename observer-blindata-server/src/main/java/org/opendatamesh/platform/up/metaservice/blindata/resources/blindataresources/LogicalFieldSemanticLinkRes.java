package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

import io.swagger.v3.oas.annotations.media.Schema;

public class LogicalFieldSemanticLinkRes {

    @Schema(description = "The LogicalField resource identifier")
    private String uuid;

    @Schema(description = "The semantic link associated to the logical field")
    private SemanticLinkHeaderRes semanticLink;

    public SemanticLinkHeaderRes getSemanticLink() {
        return semanticLink;
    }

    public void setSemanticLink(SemanticLinkHeaderRes semanticLink) {
        this.semanticLink = semanticLink;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
