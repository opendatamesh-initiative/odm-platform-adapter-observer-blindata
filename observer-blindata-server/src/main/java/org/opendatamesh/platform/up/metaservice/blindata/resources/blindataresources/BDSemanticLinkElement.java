package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

public class BDSemanticLinkElement {

    private Long sequenceId;

    private BDSemanticLinkHeader baseLink;

    private String resourceType;

    private String resourceIdentifier;

    private String resourceName;

    private Integer ordinalPosition;

    public Long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(Long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public BDSemanticLinkHeader getBaseLink() {
        return baseLink;
    }

    public void setBaseLink(BDSemanticLinkHeader baseLink) {
        this.baseLink = baseLink;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceIdentifier() {
        return resourceIdentifier;
    }

    public void setResourceIdentifier(String resourceIdentifier) {
        this.resourceIdentifier = resourceIdentifier;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Integer getOrdinalPosition() {
        return ordinalPosition;
    }

    public void setOrdinalPosition(Integer ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }
}
