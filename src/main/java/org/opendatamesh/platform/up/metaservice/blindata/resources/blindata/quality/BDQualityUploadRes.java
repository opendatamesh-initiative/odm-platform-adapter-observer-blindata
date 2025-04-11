package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality;

import java.util.List;

public class BDQualityUploadRes {
    private BDQualitySuiteRes qualitySuite;
    private List<BDQualityCheckRes> qualityChecks;

    public BDQualityUploadRes() {
    }

    public BDQualityUploadRes(BDQualitySuiteRes qualitySuite, List<BDQualityCheckRes> qualityChecks) {
        this.qualitySuite = qualitySuite;
        this.qualityChecks = qualityChecks;
    }

    public BDQualitySuiteRes getQualitySuite() {
        return qualitySuite;
    }

    public void setQualitySuite(BDQualitySuiteRes qualitySuite) {
        this.qualitySuite = qualitySuite;
    }

    public List<BDQualityCheckRes> getQualityChecks() {
        return qualityChecks;
    }

    public void setQualityChecks(List<BDQualityCheckRes> qualityChecks) {
        this.qualityChecks = qualityChecks;
    }
}
