package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

public class BDSemanticLink {

    private String defaultNamespaceIdentifier;
    private String semanticLinkString;

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
}
