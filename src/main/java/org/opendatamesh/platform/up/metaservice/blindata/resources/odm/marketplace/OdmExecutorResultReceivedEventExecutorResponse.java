package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.marketplace;

import java.util.Date;
import java.util.List;

public class OdmExecutorResultReceivedEventExecutorResponse {
    private String accessRequestIdentifier;
    private ExecutorResultReceivedEventExecutorResponseStatus status;
    private String message;
    private ExecutorResultReceivedEventProviderInfo provider;
    private Date createdAt;
    private Date updatedAt;

    public OdmExecutorResultReceivedEventExecutorResponse() {
        //DO NOTHING
    }

    public String getAccessRequestIdentifier() {
        return accessRequestIdentifier;
    }

    public void setAccessRequestIdentifier(String accessRequestIdentifier) {
        this.accessRequestIdentifier = accessRequestIdentifier;
    }

    public ExecutorResultReceivedEventExecutorResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutorResultReceivedEventExecutorResponseStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public enum ExecutorResultReceivedEventExecutorResponseStatus {
        PENDING,
        GRANTED,
        DENIED,
        REVOKED,
        ERROR
    }

    public ExecutorResultReceivedEventProviderInfo getProvider() {
        return provider;
    }

    public void setProvider(ExecutorResultReceivedEventProviderInfo provider) {
        this.provider = provider;
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

    public static class ExecutorResultReceivedEventProviderInfo {

        private String dataProductFqn;
        private List<String> dataProductPortsFqn;

        public String getDataProductFqn() {
            return dataProductFqn;
        }

        public void setDataProductFqn(String dataProductFqn) {
            this.dataProductFqn = dataProductFqn;
        }

        public List<String> getDataProductPortsFqn() {
            return dataProductPortsFqn;
        }

        public void setDataProductPortsFqn(List<String> dataProductPortsFqn) {
            this.dataProductPortsFqn = dataProductPortsFqn;
        }
    }

}
