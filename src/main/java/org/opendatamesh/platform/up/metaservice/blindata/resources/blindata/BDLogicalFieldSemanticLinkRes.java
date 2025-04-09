package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata;

import io.swagger.v3.oas.annotations.media.Schema;

public class BDLogicalFieldSemanticLinkRes {

    @Schema(description = "The LogicalField resource identifier")
    private String uuid;

    @Schema(description = "The semantic link associated to the logical field")
    private BDSemanticLinkHeaderRes semanticLink;

    public BDSemanticLinkHeaderRes getSemanticLink() {
        return semanticLink;
    }

    public void setSemanticLink(BDSemanticLinkHeaderRes semanticLink) {
        this.semanticLink = semanticLink;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
