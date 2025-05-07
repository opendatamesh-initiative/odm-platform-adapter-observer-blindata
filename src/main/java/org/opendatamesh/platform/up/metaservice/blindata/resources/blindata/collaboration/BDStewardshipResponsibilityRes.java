package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration;

import java.util.Date;

public class BDStewardshipResponsibilityRes {

    private Long sequenceId;

    private BDShortUserRes user;

    private BDStewardshipRoleRes stewardshipRole;

    private String resourceType = "DATA_PRODUCT";

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

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof BDStewardshipResponsibilityRes)) return false;
        final BDStewardshipResponsibilityRes other = (BDStewardshipResponsibilityRes) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$sequenceId = this.getSequenceId();
        final Object other$sequenceId = other.getSequenceId();
        if (this$sequenceId == null ? other$sequenceId != null : !this$sequenceId.equals(other$sequenceId))
            return false;
        final Object this$user = this.getUser();
        final Object other$user = other.getUser();
        if (this$user == null ? other$user != null : !this$user.equals(other$user)) return false;
        final Object this$stewardshipRole = this.getStewardshipRole();
        final Object other$stewardshipRole = other.getStewardshipRole();
        if (this$stewardshipRole == null ? other$stewardshipRole != null : !this$stewardshipRole.equals(other$stewardshipRole))
            return false;
        final Object this$resourceType = this.getResourceType();
        final Object other$resourceType = other.getResourceType();
        if (this$resourceType == null ? other$resourceType != null : !this$resourceType.equals(other$resourceType))
            return false;
        final Object this$resourceIdentifier = this.getResourceIdentifier();
        final Object other$resourceIdentifier = other.getResourceIdentifier();
        if (this$resourceIdentifier == null ? other$resourceIdentifier != null : !this$resourceIdentifier.equals(other$resourceIdentifier))
            return false;
        final Object this$resourceName = this.getResourceName();
        final Object other$resourceName = other.getResourceName();
        if (this$resourceName == null ? other$resourceName != null : !this$resourceName.equals(other$resourceName))
            return false;
        final Object this$description = this.getDescription();
        final Object other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description))
            return false;
        final Object this$startDate = this.getStartDate();
        final Object other$startDate = other.getStartDate();
        if (this$startDate == null ? other$startDate != null : !this$startDate.equals(other$startDate)) return false;
        final Object this$endDate = this.getEndDate();
        final Object other$endDate = other.getEndDate();
        if (this$endDate == null ? other$endDate != null : !this$endDate.equals(other$endDate)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BDStewardshipResponsibilityRes;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $sequenceId = this.getSequenceId();
        result = result * PRIME + ($sequenceId == null ? 43 : $sequenceId.hashCode());
        final Object $user = this.getUser();
        result = result * PRIME + ($user == null ? 43 : $user.hashCode());
        final Object $stewardshipRole = this.getStewardshipRole();
        result = result * PRIME + ($stewardshipRole == null ? 43 : $stewardshipRole.hashCode());
        final Object $resourceType = this.getResourceType();
        result = result * PRIME + ($resourceType == null ? 43 : $resourceType.hashCode());
        final Object $resourceIdentifier = this.getResourceIdentifier();
        result = result * PRIME + ($resourceIdentifier == null ? 43 : $resourceIdentifier.hashCode());
        final Object $resourceName = this.getResourceName();
        result = result * PRIME + ($resourceName == null ? 43 : $resourceName.hashCode());
        final Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        final Object $startDate = this.getStartDate();
        result = result * PRIME + ($startDate == null ? 43 : $startDate.hashCode());
        final Object $endDate = this.getEndDate();
        result = result * PRIME + ($endDate == null ? 43 : $endDate.hashCode());
        return result;
    }
}
