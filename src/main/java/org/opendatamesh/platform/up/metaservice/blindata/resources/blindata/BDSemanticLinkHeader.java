package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata;

import java.util.HashSet;
import java.util.Set;

public class BDSemanticLinkHeader {

    private BDSemanticLinkHeaderId id;

    private BDPhysicalFieldRes physicalField;

    private BDLogicalFieldRes logicalField;

    private String defaultNamespaceIdentifier;

    private String semanticLinkString;

    private Set<BDSemanticLinkElement> semanticLinkElements = new HashSet<>();

    public BDSemanticLinkHeaderId getId() {
        return id;
    }

    public void setId(BDSemanticLinkHeaderId id) {
        this.id = id;
    }

    public BDPhysicalFieldRes getPhysicalField() {
        return physicalField;
    }

    public void setPhysicalField(BDPhysicalFieldRes physicalField) {
        this.physicalField = physicalField;
    }

    public BDLogicalFieldRes getLogicalField() {
        return logicalField;
    }

    public void setLogicalField(BDLogicalFieldRes logicalField) {
        this.logicalField = logicalField;
    }

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
