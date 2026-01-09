package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt;

import java.util.Date;


public class BDIssuePolicyRes {
    private String uuid;
    private String name;
    private String issueDescription;
    private String resourceIdentifier;
    private BDResourceTypeRes resourceType;
    private String resourceName;
    private BDIssuePolicyType policyType;
    private Object policyContent;
    private BDIssueRes issueTemplate;
    private boolean active;
    private Date createdAt;
    private Date updatedAt;

    public BDIssuePolicyRes() {
        //DO NOTHING
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public String getResourceIdentifier() {
        return resourceIdentifier;
    }

    public void setResourceIdentifier(String resourceIdentifier) {
        this.resourceIdentifier = resourceIdentifier;
    }

    public BDResourceTypeRes getResourceType() {
        return resourceType;
    }

    public void setResourceType(BDResourceTypeRes resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public BDIssuePolicyType getPolicyType() {
        return policyType;
    }

    public void setPolicyType(BDIssuePolicyType policyType) {
        this.policyType = policyType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Object getPolicyContent() {
        return policyContent;
    }

    public void setPolicyContent(Object policyContent) {
        this.policyContent = policyContent;
    }

    public BDIssueRes getIssueTemplate() {
        return issueTemplate;
    }

    public void setIssueTemplate(BDIssueRes issueTemplate) {
        this.issueTemplate = issueTemplate;
    }

    @Override
    public String toString() {
        return "BDIssuePolicyRes{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", issueDescription='" + issueDescription + '\'' +
                ", resourceIdentifier='" + resourceIdentifier + '\'' +
                ", resourceType=" + resourceType +
                ", resourceName='" + resourceName + '\'' +
                ", policyType=" + policyType +
                ", policyContent=" + policyContent +
                ", issueTemplate=" + issueTemplate +
                ", active=" + active +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
