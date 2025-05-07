package org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDAdditionalPropertiesRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt.BDIssuePolicyRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalFieldRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualitySemaphoreRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityStrategyRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualitySuiteRes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class QualityCheck {
    private String uuid;
    private String code;
    private String name;
    private String description;
    private BigDecimal warningThreshold;
    private BigDecimal successThreshold;
    private Boolean isEnabled;
    private BDQualitySuiteRes qualitySuite;
    private BDQualitySemaphoreRes lastSemaphore;
    private BDQualityStrategyRes scoreStrategy;
    private BigDecimal scoreExpectedValue;
    private BigDecimal scoreLeftValue;
    private BigDecimal scoreRightValue;
    private Boolean isManual;
    private String checkGroupCode;

    private List<BDPhysicalEntityRes> physicalEntities = new ArrayList<>();
    private List<BDPhysicalFieldRes> physicalFields = new ArrayList<>();

    private List<BDAdditionalPropertiesRes> additionalProperties = new ArrayList<>();

    //Fields used only internally to the Observer
    private boolean isReference;
    private List<BDIssuePolicyRes> issuePolicies = new ArrayList<>();

    public QualityCheck() {
        //DO NOTHING
    }

    public List<BDIssuePolicyRes> getIssuePolicies() {
        return issuePolicies;
    }

    public void setIssuePolicies(List<BDIssuePolicyRes> issuePolicies) {
        this.issuePolicies = issuePolicies;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getWarningThreshold() {
        return warningThreshold;
    }

    public void setWarningThreshold(BigDecimal warningThreshold) {
        this.warningThreshold = warningThreshold;
    }

    public BigDecimal getSuccessThreshold() {
        return successThreshold;
    }

    public void setSuccessThreshold(BigDecimal successThreshold) {
        this.successThreshold = successThreshold;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    public BDQualitySuiteRes getQualitySuite() {
        return qualitySuite;
    }

    public void setQualitySuite(BDQualitySuiteRes qualitySuite) {
        this.qualitySuite = qualitySuite;
    }

    public BDQualitySemaphoreRes getLastSemaphore() {
        return lastSemaphore;
    }

    public void setLastSemaphore(BDQualitySemaphoreRes lastSemaphore) {
        this.lastSemaphore = lastSemaphore;
    }

    public BDQualityStrategyRes getScoreStrategy() {
        return scoreStrategy;
    }

    public void setScoreStrategy(BDQualityStrategyRes scoreStrategy) {
        this.scoreStrategy = scoreStrategy;
    }

    public BigDecimal getScoreExpectedValue() {
        return scoreExpectedValue;
    }

    public void setScoreExpectedValue(BigDecimal scoreExpectedValue) {
        this.scoreExpectedValue = scoreExpectedValue;
    }

    public BigDecimal getScoreLeftValue() {
        return scoreLeftValue;
    }

    public void setScoreLeftValue(BigDecimal scoreLeftValue) {
        this.scoreLeftValue = scoreLeftValue;
    }

    public BigDecimal getScoreRightValue() {
        return scoreRightValue;
    }

    public void setScoreRightValue(BigDecimal scoreRightValue) {
        this.scoreRightValue = scoreRightValue;
    }

    public Boolean getManual() {
        return isManual;
    }

    public void setManual(Boolean manual) {
        isManual = manual;
    }

    public String getCheckGroupCode() {
        return checkGroupCode;
    }

    public void setCheckGroupCode(String checkGroupCode) {
        this.checkGroupCode = checkGroupCode;
    }


    public List<BDPhysicalEntityRes> getPhysicalEntities() {
        return physicalEntities;
    }

    public void setPhysicalEntities(List<BDPhysicalEntityRes> physicalEntities) {
        this.physicalEntities = physicalEntities;
    }

    public List<BDPhysicalFieldRes> getPhysicalFields() {
        return physicalFields;
    }

    public void setPhysicalFields(List<BDPhysicalFieldRes> physicalFields) {
        this.physicalFields = physicalFields;
    }

    public List<BDAdditionalPropertiesRes> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(List<BDAdditionalPropertiesRes> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public boolean isReference() {
        return isReference;
    }

    public void setReference(boolean reference) {
        isReference = reference;
    }

    @Override
    public String toString() {
        return "QualityCheck{" +
                "uuid='" + uuid + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", warningThreshold=" + warningThreshold +
                ", successThreshold=" + successThreshold +
                ", isEnabled=" + isEnabled +
                ", qualitySuite=" + qualitySuite +
                ", lastSemaphore=" + lastSemaphore +
                ", scoreStrategy=" + scoreStrategy +
                ", scoreExpectedValue=" + scoreExpectedValue +
                ", scoreLeftValue=" + scoreLeftValue +
                ", scoreRightValue=" + scoreRightValue +
                ", isManual=" + isManual +
                ", checkGroupCode='" + checkGroupCode + '\'' +
                ", physicalEntities=" + physicalEntities +
                ", physicalFields=" + physicalFields +
                ", additionalProperties=" + additionalProperties +
                ", isReference=" + isReference +
                '}';
    }
}
