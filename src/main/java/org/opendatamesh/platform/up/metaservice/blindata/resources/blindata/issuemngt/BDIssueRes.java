package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDAdditionalPropertiesRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDShortUserRes;

import java.util.ArrayList;
import java.util.List;

public class BDIssueRes {
    private String name;
    private String description;
    private BDIssueTypeRes issueType;
    private BDIssueCampaignRes campaign;
    private BDIssueStatusRes issueStatus;
    private BDIssueSeverityLevelRes severity;
    private Integer priorityOrder;
    private BDShortUserRes assignee;
    private List<BDAdditionalPropertiesRes> additionalProperties = new ArrayList<>();

    public BDIssueRes() {
        //DO NOTHING
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BDIssueTypeRes getIssueType() {
        return issueType;
    }

    public void setIssueType(BDIssueTypeRes issueType) {
        this.issueType = issueType;
    }

    public BDIssueCampaignRes getCampaign() {
        return campaign;
    }

    public void setCampaign(BDIssueCampaignRes campaign) {
        this.campaign = campaign;
    }

    public BDIssueStatusRes getIssueStatus() {
        return issueStatus;
    }

    public void setIssueStatus(BDIssueStatusRes issueStatus) {
        this.issueStatus = issueStatus;
    }

    public BDIssueSeverityLevelRes getSeverity() {
        return severity;
    }

    public void setSeverity(BDIssueSeverityLevelRes severity) {
        this.severity = severity;
    }

    public List<BDAdditionalPropertiesRes> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(List<BDAdditionalPropertiesRes> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public BDShortUserRes getAssignee() {
        return assignee;
    }

    public void setAssignee(BDShortUserRes assignee) {
        this.assignee = assignee;
    }

    public Integer getPriorityOrder() {
        return priorityOrder;
    }

    public void setPriorityOrder(Integer priorityOrder) {
        this.priorityOrder = priorityOrder;
    }

    @Override
    public String toString() {
        return "BDIssueRes{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", issueType=" + issueType +
                ", campaign=" + campaign +
                ", issueStatus=" + issueStatus +
                ", severity=" + severity +
                ", priorityOrder=" + priorityOrder +
                ", assignee=" + assignee +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}
