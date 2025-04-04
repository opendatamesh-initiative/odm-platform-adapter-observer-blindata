package org.opendatamesh.platform.up.metaservice.blindata.client.utils;

import java.util.List;
import java.util.Map;


class AsyncRestTask {
    private String id;
    private Status status;
    private int responseHttpStatus;
    private Map<String, List<String>> responseHeaders;
    private byte[] responseBody;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getResponseHttpStatus() {
        return responseHttpStatus;
    }

    public void setResponseHttpStatus(int responseHttpStatus) {
        this.responseHttpStatus = responseHttpStatus;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, List<String>> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public byte[] getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(byte[] responseBody) {
        this.responseBody = responseBody;
    }

    public enum Status {
        IN_PROGRESS,
        DONE,
        FAILED,
        NOT_FOUND
    }
}