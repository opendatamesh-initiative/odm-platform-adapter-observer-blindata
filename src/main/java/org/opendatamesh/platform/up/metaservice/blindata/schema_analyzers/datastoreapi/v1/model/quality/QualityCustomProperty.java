package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1.model.quality;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.opendatamesh.dpds.model.core.ComponentBase;

import java.util.ArrayList;
import java.util.List;

public class QualityCustomProperty extends ComponentBase {
    private String displayName;
    private String scoreStrategy;
    private Float scoreWarningThreshold;
    private Float scoreSuccessThreshold;
    @JsonProperty("isCheckEnabled")
    private Boolean isCheckEnabled;
    private List<QualityIssuePolicy> issuePolicies = new ArrayList<>();
    private String refName;

    public QualityCustomProperty() {
        //DO NOTHING
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getScoreStrategy() {
        return scoreStrategy;
    }

    public void setScoreStrategy(String scoreStrategy) {
        this.scoreStrategy = scoreStrategy;
    }

    public Float getScoreWarningThreshold() {
        return scoreWarningThreshold;
    }

    public void setScoreWarningThreshold(Float scoreWarningThreshold) {
        this.scoreWarningThreshold = scoreWarningThreshold;
    }

    public Float getScoreSuccessThreshold() {
        return scoreSuccessThreshold;
    }

    public void setScoreSuccessThreshold(Float scoreSuccessThreshold) {
        this.scoreSuccessThreshold = scoreSuccessThreshold;
    }

    public Boolean getCheckEnabled() {
        return isCheckEnabled;
    }

    public void setCheckEnabled(Boolean checkEnabled) {
        isCheckEnabled = checkEnabled;
    }

    public String getRefName() {
        return refName;
    }

    public void setRefName(String refName) {
        this.refName = refName;
    }

    public List<QualityIssuePolicy> getIssuePolicies() {
        return issuePolicies;
    }

    public void setIssuePolicies(List<QualityIssuePolicy> issuePolicies) {
        this.issuePolicies = issuePolicies;
    }

    @Override
    public String toString() {
        return "QualityCustomProperty{" +
                "displayName='" + displayName + '\'' +
                ", scoreStrategy='" + scoreStrategy + '\'' +
                ", scoreWarningThreshold=" + scoreWarningThreshold +
                ", scoreSuccessThreshold=" + scoreSuccessThreshold +
                ", isCheckEnabled=" + isCheckEnabled +
                ", issuePolicies=" + issuePolicies +
                ", refName='" + refName + '\'' +
                '}';
    }
}
