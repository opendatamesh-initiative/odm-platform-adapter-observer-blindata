package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

import java.util.List;

public class BDUploadResultsMessage {
    private String message;
    private int rowCreated;
    private int rowUpdated;
    private int rowDiscarded;
    private List<Object> errors;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRowCreated() {
        return rowCreated;
    }

    public void setRowCreated(int rowCreated) {
        this.rowCreated = rowCreated;
    }

    public int getRowUpdated() {
        return rowUpdated;
    }

    public void setRowUpdated(int rowUpdated) {
        this.rowUpdated = rowUpdated;
    }

    public int getRowDiscarded() {
        return rowDiscarded;
    }

    public void setRowDiscarded(int rowDiscarded) {
        this.rowDiscarded = rowDiscarded;
    }

    public List<Object> getErrors() {
        return errors;
    }

    public void setErrors(List<Object> errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "{" +
                "message='" + message + '\'' +
                ", rowCreated=" + rowCreated +
                ", rowUpdated=" + rowUpdated +
                ", rowDiscarded=" + rowDiscarded +
                ", errors=" + errors +
                '}';
    }
}
