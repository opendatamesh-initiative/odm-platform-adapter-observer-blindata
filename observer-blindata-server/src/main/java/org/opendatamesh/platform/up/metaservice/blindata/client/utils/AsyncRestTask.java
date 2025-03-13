package org.opendatamesh.platform.up.metaservice.blindata.client.utils;

import org.springframework.http.ResponseEntity;

/**
 * Represents an asynchronous REST task with a status, retry information, and an identifier.
 */
public class AsyncRestTask {
    /**
     * A unique identifier for the asynchronous task.
     */
    private String id;
    /**
     * The current status of the asynchronous task.
     */
    private Status status;
    /**
     * The number of seconds to wait before retrying the request.
     */
    private int retryAfterSeconds;
    /**
     * The result of the asynchronous request
     */
    private ResponseEntity<byte[]> response;


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getRetryAfterSeconds() {
        return retryAfterSeconds;
    }

    public void setRetryAfterSeconds(int retryAfterSeconds) {
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ResponseEntity<byte[]> getResponse() {
        return response;
    }

    public void setResponse(ResponseEntity<byte[]> response) {
        this.response = response;
    }

    public enum Status {
        IN_PROGRESS,
        DONE,
        FAILED,
        NOT_FOUND
    }
}