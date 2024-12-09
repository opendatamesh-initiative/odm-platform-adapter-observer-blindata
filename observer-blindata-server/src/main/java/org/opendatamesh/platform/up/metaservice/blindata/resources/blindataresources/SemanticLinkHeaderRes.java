package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

public class SemanticLinkHeaderRes {

    @Schema(description = "The namespace identifier")
    private String defaultNamespaceIdentifier;

    @Schema(description = "A string representation of the semantic link")
    private String semanticLinkString;

    @Schema(description = "The set of resources that composes the semantic link")
    private Set<BDSemanticLinkElement> semanticLinkElements;

    public String getDefaultNamespaceIdentifier() {
        return defaultNamespaceIdentifier;
    }

    public void setDefaultNamespaceIdentifier(String defaultNamespaceIdentifier) {
        this.defaultNamespaceIdentifier = defaultNamespaceIdentifier;
    }

    public String getSemanticLinkString() {
        return semanticLinkString;
    }

    public void setSemanticLinkString(String semanticLinkString) {
        this.semanticLinkString = semanticLinkString;
    }

    public Set<BDSemanticLinkElement> getSemanticLinkElements() {
        return semanticLinkElements;
    }

    public void setSemanticLinkElements(Set<BDSemanticLinkElement> semanticLinkElements) {
        this.semanticLinkElements = semanticLinkElements;
    }
}
