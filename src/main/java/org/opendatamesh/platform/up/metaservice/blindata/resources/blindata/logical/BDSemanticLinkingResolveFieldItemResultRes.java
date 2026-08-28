package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical;

public class BDSemanticLinkingResolveFieldItemResultRes {

    private String pathString;
    private String defaultNamespaceIdentifier;
    private BDLogicalFieldSemanticLinkRes logicalField;
    private String errorMessage;

    public String getPathString() {
        return pathString;
    }

    public void setPathString(String pathString) {
        this.pathString = pathString;
    }

    public String getDefaultNamespaceIdentifier() {
        return defaultNamespaceIdentifier;
    }

    public void setDefaultNamespaceIdentifier(String defaultNamespaceIdentifier) {
        this.defaultNamespaceIdentifier = defaultNamespaceIdentifier;
    }

    public BDLogicalFieldSemanticLinkRes getLogicalField() {
        return logicalField;
    }

    public void setLogicalField(BDLogicalFieldSemanticLinkRes logicalField) {
        this.logicalField = logicalField;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isSuccessful() {
        return errorMessage == null && logicalField != null;
    }
}
