package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.v2;

class AsyncApiOperation {
    private String operationId;
    private String summary;
    private String description;
    private AsyncApiMessage message;
    /*
    private List<Map<String, List<String>>> security;
    private List<Tag> tags;
    private ExternalDocumentation externalDocs;
    private Map<String, Object> bindings;
    private List<Object> traits;
    */

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AsyncApiMessage getMessage() {
        return message;
    }

    public void setMessage(AsyncApiMessage message) {
        this.message = message;
    }
}
