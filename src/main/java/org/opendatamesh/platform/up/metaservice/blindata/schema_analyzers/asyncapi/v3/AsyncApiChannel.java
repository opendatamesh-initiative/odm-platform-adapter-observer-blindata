package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.v3;

import java.util.Map;

class AsyncApiChannel {
    private String title;
    private String address;
    private String summary;
    private String description;
    private Map<String, AsyncApiMessage> messages;

    /*
    private List<Reference> servers;
    private Map<String, Object> parameters;
    private Map<String, Object> bindings;
    private List<Object> tags;
    private Object externalDocs;
    */

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Map<String, AsyncApiMessage> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, AsyncApiMessage> messages) {
        this.messages = messages;
    }
}
