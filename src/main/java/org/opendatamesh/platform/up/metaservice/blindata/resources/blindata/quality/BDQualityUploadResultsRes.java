package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality;

import java.util.ArrayList;
import java.util.List;

public class BDQualityUploadResultsRes {
    private String message;
    private int rowCreated;
    private int rowUpdated;
    private int rowDiscarded;
    private String qualitySuiteUuid;
    private List<String> qualityChecksUuids = new ArrayList<>();

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

    public String getQualitySuiteUuid() {
        return qualitySuiteUuid;
    }

    public void setQualitySuiteUuid(String qualitySuiteUuid) {
        this.qualitySuiteUuid = qualitySuiteUuid;
    }

    public List<String> getQualityChecksUuids() {
        return qualityChecksUuids;
    }

    public void setQualityChecksUuids(List<String> qualityChecksUuids) {
        this.qualityChecksUuids = qualityChecksUuids;
    }
}
