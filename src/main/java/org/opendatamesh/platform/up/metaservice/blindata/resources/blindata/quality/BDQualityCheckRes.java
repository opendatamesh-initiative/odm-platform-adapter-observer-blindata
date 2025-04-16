package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDAdditionalPropertiesRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalFieldRes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BDQualityCheckRes {
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
    private Date lastRun;
    private BigDecimal scoreExpectedValue;
    private BigDecimal scoreLeftValue;
    private BigDecimal scoreRightValue;
    private Boolean isManual;
    private String checkGroupCode;
    private Integer lastMetric;
    private Integer lastTotalElements;
    private BDQualityTrendIndicatorRes trendIndicator;
    private Double lastScore;

    private List<BDPhysicalEntityRes> physicalEntities = new ArrayList<>();
    private List<BDPhysicalFieldRes> physicalFields = new ArrayList<>();

    private List<BDAdditionalPropertiesRes> additionalProperties = new ArrayList<>();
    private Date creationDate;
    private Date modificationDate;

    public BDQualityCheckRes() {
        //DO NOTHING
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

    @JsonGetter("isEnabled")
    public Boolean getEnabled() {
        return isEnabled;
    }

    @JsonSetter("isEnabled")
    public void setEnabled(Boolean enabled) {
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

    public Date getLastRun() {
        return lastRun;
    }

    public void setLastRun(Date lastRun) {
        this.lastRun = lastRun;
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

    public Integer getLastMetric() {
        return lastMetric;
    }

    public void setLastMetric(Integer lastMetric) {
        this.lastMetric = lastMetric;
    }

    public Integer getLastTotalElements() {
        return lastTotalElements;
    }

    public void setLastTotalElements(Integer lastTotalElements) {
        this.lastTotalElements = lastTotalElements;
    }

    public BDQualityTrendIndicatorRes getTrendIndicator() {
        return trendIndicator;
    }

    public void setTrendIndicator(BDQualityTrendIndicatorRes trendIndicator) {
        this.trendIndicator = trendIndicator;
    }

    public Double getLastScore() {
        return lastScore;
    }

    public void setLastScore(Double lastScore) {
        this.lastScore = lastScore;
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }
}
