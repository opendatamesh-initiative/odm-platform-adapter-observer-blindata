package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode
public class BDStewardshipResponsibilityRes {

    private Long sequenceId;

    private BDShortUserRes user;

    private BDStewardshipRoleRes stewardshipRole;

    private BDResourceType resourceType = BDResourceType.DATA_PRODUCT;

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

    public BDShortUserRes getUser() {
        return user;
    }

    public void setUser(BDShortUserRes user) {
        this.user = user;
    }

    public BDStewardshipRoleRes getStewardshipRole() {
        return stewardshipRole;
    }

    public void setStewardshipRole(BDStewardshipRoleRes stewardshipRole) {
        this.stewardshipRole = stewardshipRole;
    }

    public BDResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(BDResourceType resourceType) {
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
