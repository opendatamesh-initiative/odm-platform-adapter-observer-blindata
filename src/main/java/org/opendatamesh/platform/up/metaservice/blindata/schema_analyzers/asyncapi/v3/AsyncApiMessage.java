package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.v3;

class AsyncApiMessage {
    private AsyncApiMultiFormatSchema payload;
    private String contentType;
    private String name;
    private String title;
    private String summary;
    private String description;
    /*
    private Object headers;
    private Object correlationId;
    private Map<String, Object> bindings;
    private List<MessageExample> examples;
    private List<Object> traits;
    private List<Object> tags;
    private Object externalDocs;
    */

    public AsyncApiMultiFormatSchema getPayload() {
        return payload;
    }

    public void setPayload(AsyncApiMultiFormatSchema payload) {
        this.payload = payload;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
