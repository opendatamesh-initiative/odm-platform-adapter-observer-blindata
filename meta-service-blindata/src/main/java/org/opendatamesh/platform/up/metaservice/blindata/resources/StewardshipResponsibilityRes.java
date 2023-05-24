package org.opendatamesh.platform.up.metaservice.blindata.resources;

import java.util.Date;

public class StewardshipResponsibilityRes {

    private Long sequenceId;

    private ShortUserRes user;

    private StewardshipRoleRes stewardshipRole;

    private ResourceType resourceType = ResourceType.DATA_PRODUCT;

    private String resourceIdentifier;

    private String resourceName;

    private String description;

    private Date startDate;

    private Date endDate;

    public Long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(Long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public ShortUserRes getUser() {
        return user;
    }

    public void setUser(ShortUserRes user) {
        this.user = user;
    }

    public StewardshipRoleRes getStewardshipRole() {
        return stewardshipRole;
    }

    public void setStewardshipRole(StewardshipRoleRes stewardshipRole) {
        this.stewardshipRole = stewardshipRole;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
