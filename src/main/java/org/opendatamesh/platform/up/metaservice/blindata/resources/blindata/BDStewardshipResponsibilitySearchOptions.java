package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata;

public class BDStewardshipResponsibilitySearchOptions {
    private String userUuid;
    private String resourceIdentifier;
    private String roleUuid;
    private Boolean endDateIsNull;

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getResourceIdentifier() {
        return resourceIdentifier;
    }

    public void setResourceIdentifier(String resourceIdentifier) {
        this.resourceIdentifier = resourceIdentifier;
    }

    public String getRoleUuid() {
        return roleUuid;
    }

    public void setRoleUuid(String roleUuid) {
        this.roleUuid = roleUuid;
    }

    public Boolean getEndDateIsNull() {
        return endDateIsNull;
    }

    public void setEndDateIsNull(Boolean endDateIsNull) {
        this.endDateIsNull = endDateIsNull;
    }
}
