package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.devops;

import java.util.Date;

public class OdmTaskResource {

    private Long id;
    private String activityId;
    private String executorRef;
    private String callbackRef;
    private String template;
    private String configurations;
    private OdmTaskStatus status;
    private String results;
    private String errors;
    private Date createdAt;
    private Date startedAt;
    private Date finishedAt;

    public OdmTaskResource() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getExecutorRef() {
        return executorRef;
    }

    public void setExecutorRef(String executorRef) {
        this.executorRef = executorRef;
    }

    public String getCallbackRef() {
        return callbackRef;
    }

    public void setCallbackRef(String callbackRef) {
        this.callbackRef = callbackRef;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getConfigurations() {
        return configurations;
    }

    public void setConfigurations(String configurations) {
        this.configurations = configurations;
    }

    public OdmTaskStatus getStatus() {
        return status;
    }

    public void setStatus(OdmTaskStatus status) {
        this.status = status;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }
}
