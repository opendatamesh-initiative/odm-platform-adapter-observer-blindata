package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1.model.quality;

import org.opendatamesh.dpds.model.core.ComponentBase;

public class QualityIssuePolicy extends ComponentBase {
    private String name;
    private String issueDescription;
    private String policyType;
    private String semaphoreColor;
    private Integer semaphoresNumber;
    private Boolean autoClose;
    private String severity;
    private String issueOwner;
    private String issueReporter;

    public QualityIssuePolicy() {
        //DO NOTHING
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

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public String getSemaphoreColor() {
        return semaphoreColor;
    }

    public void setSemaphoreColor(String semaphoreColor) {
        this.semaphoreColor = semaphoreColor;
    }

    public Integer getSemaphoresNumber() {
        return semaphoresNumber;
    }

    public void setSemaphoresNumber(Integer semaphoresNumber) {
        this.semaphoresNumber = semaphoresNumber;
    }

    public Boolean getAutoClose() {
        return autoClose;
    }

    public void setAutoClose(Boolean autoClose) {
        this.autoClose = autoClose;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getIssueOwner() {
        return issueOwner;
    }

    public void setIssueOwner(String issueOwner) {
        this.issueOwner = issueOwner;
    }

    public String getIssueReporter() {
        return issueReporter;
    }

    public void setIssueReporter(String issueReporter) {
        this.issueReporter = issueReporter;
    }
}
