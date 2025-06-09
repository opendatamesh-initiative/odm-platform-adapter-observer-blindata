package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product;

import java.util.List;


public class BDMarketplaceAccessRequestPortStatusUploadResultsRes {
    private String message;
    private int rowCreated;
    private int rowUpdated;
    private int rowDiscarded;
    private List<UploadErrorMessageRes> errors;

    public BDMarketplaceAccessRequestPortStatusUploadResultsRes() {
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<UploadErrorMessageRes> getErrors() {
        return errors;
    }

    public void setErrors(List<UploadErrorMessageRes> errors) {
        this.errors = errors;
    }

    public static class UploadErrorMessageRes {
        private String message;

        public UploadErrorMessageRes() {
        }

        public UploadErrorMessageRes(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
