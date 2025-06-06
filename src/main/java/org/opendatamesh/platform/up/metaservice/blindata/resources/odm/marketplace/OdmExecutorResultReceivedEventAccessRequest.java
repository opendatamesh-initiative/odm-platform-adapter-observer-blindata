package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.marketplace;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OdmExecutorResultReceivedEventAccessRequest {
    private String uuid;
    private String name;
    private String identifier;
    private ExecutorResultReceivedEventAccessRequestOperation operation;
    private String reviewerIdentifier;
    private ExecutorResultReceivedEventAccessRequestConsumerType consumerType;
    private String consumerIdentifier;
    private String providerDataProductFqn;
    private List<String> providerDataProductPortsFqn = new ArrayList<>();
    private ObjectNode properties;
    private Date startDate;
    private Date endDate;
    private Date createdAt;
    private Date updatedAt;

    public OdmExecutorResultReceivedEventAccessRequest() {
        //DO NOTHING
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

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public ExecutorResultReceivedEventAccessRequestOperation getOperation() {
        return operation;
    }

    public void setOperation(ExecutorResultReceivedEventAccessRequestOperation operation) {
        this.operation = operation;
    }

    public String getReviewerIdentifier() {
        return reviewerIdentifier;
    }

    public void setReviewerIdentifier(String reviewerIdentifier) {
        this.reviewerIdentifier = reviewerIdentifier;
    }

    public ExecutorResultReceivedEventAccessRequestConsumerType getConsumerType() {
        return consumerType;
    }

    public void setConsumerType(ExecutorResultReceivedEventAccessRequestConsumerType consumerType) {
        this.consumerType = consumerType;
    }

    public String getConsumerIdentifier() {
        return consumerIdentifier;
    }

    public void setConsumerIdentifier(String consumerIdentifier) {
        this.consumerIdentifier = consumerIdentifier;
    }

    public String getProviderDataProductFqn() {
        return providerDataProductFqn;
    }

    public void setProviderDataProductFqn(String providerDataProductFqn) {
        this.providerDataProductFqn = providerDataProductFqn;
    }

    public List<String> getProviderDataProductPortsFqn() {
        return providerDataProductPortsFqn;
    }

    public void setProviderDataProductPortsFqn(List<String> providerDataProductPortsFqn) {
        this.providerDataProductPortsFqn = providerDataProductPortsFqn;
    }

    public ObjectNode getProperties() {
        return properties;
    }

    public void setProperties(ObjectNode properties) {
        this.properties = properties;
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

    public enum ExecutorResultReceivedEventAccessRequestOperation {
        MARKETPLACE_SUBSCRIBE,
        MARKETPLACE_UNSUBSCRIBE
    }

    public enum ExecutorResultReceivedEventAccessRequestConsumerType {
        USER,
        TEAM,
        DATA_PRODUCT
    }
}
