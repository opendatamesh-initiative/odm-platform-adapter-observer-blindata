package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt.BDIssuePolicyRes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BDQualityUploadRes {
    private BDQualitySuiteRes qualitySuite;
    private List<BDQualityCheckRes> qualityChecks;
    private Map<String, List<BDIssuePolicyRes>> issuePoliciesByCheckCodes = new HashMap<>();

    public BDQualityUploadRes() {
    }

    public BDQualityUploadRes(BDQualitySuiteRes qualitySuite, List<BDQualityCheckRes> qualityChecks, Map<String, List<BDIssuePolicyRes>> issuePoliciesByCheckCodes) {
        this.qualitySuite = qualitySuite;
        this.qualityChecks = qualityChecks;
        this.issuePoliciesByCheckCodes = issuePoliciesByCheckCodes;
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

    public Map<String, List<BDIssuePolicyRes>> getIssuePoliciesByCheckCodes() {
        return issuePoliciesByCheckCodes;
    }

    public void setIssuePoliciesByCheckCodes(Map<String, List<BDIssuePolicyRes>> issuePoliciesByCheckCodes) {
        this.issuePoliciesByCheckCodes = issuePoliciesByCheckCodes;
    }
}
