package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.exceptions;


import java.util.Date;

//PAY ATTENTION, COPIED FROM ODM-PLATFORM!!!
public class OdmPlatformErrorRes {

    // HTTP Status code
    int status;

    // Standard error code
    String code;

    // Standard error description
    String description;

    // Exception message.
    // Do not include exception cause's message.
    // It is appended only to the log error message.
    String message;

    // Service endpoint
    String path;

    // Error timestamp
    long timestamp = new Date().getTime();

    public OdmPlatformErrorRes() {

    }

    public OdmPlatformErrorRes(int status, String errorCode, String message, String path) {
        super();
        this.status = status;
        this.code = errorCode;
        this.message = message;
        this.path = path;
    }

    public int getStatus() {
        return this.status;
    }

    public String getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    public String getMessage() {
        return this.message;
    }

    public String getPath() {
        return this.path;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String toString() {
        return "ErrorRes(status=" + this.getStatus() + ", code=" + this.getCode() + ", description=" + this.getDescription() + ", message=" + this.getMessage() + ", path=" + this.getPath() + ", timestamp=" + this.getTimestamp() + ")";
    }
}