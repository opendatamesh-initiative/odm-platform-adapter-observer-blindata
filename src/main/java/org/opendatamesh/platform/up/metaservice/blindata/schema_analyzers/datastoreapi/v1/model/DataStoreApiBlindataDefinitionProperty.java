package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.opendatamesh.dpds.model.core.ComponentBase;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1.model.quality.Quality;

import java.util.List;

public class DataStoreApiBlindataDefinitionProperty extends ComponentBase {

    private String type;
    private String description;
    private String name;
    private String kind;
    private Boolean required;
    private String displayName;
    private String summary;
    private String comments;
    private List<String> examples;
    private String status;
    private List<String> tags;
    private String externalDocs;
    private String defaultValue;
    private Boolean isClassified;
    private String classificationLevel;
    private Boolean isUnique;
    private Boolean isNullable;
    private String pattern;
    private String format;
    private List<String> enumValues;
    private Integer minLength;
    private Integer maxLength;
    private String contentEncoding;
    private String contentMediaType;
    private Integer precision;
    private Integer scale;
    private Integer minimum;
    private Boolean exclusiveMinimum;
    private Integer maximum;
    private Boolean exclusiveMaximum;
    private Boolean readOnly;
    private Boolean writeOnly;
    private String physicalType;
    private Boolean partitionStatus;
    private Integer partitionKeyPosition;
    private Boolean clusterStatus;
    private Integer clusterKeyPosition;
    private Integer ordinalPosition;
    private List<Quality> quality;

    @JsonCreator
    public DataStoreApiBlindataDefinitionProperty(
            @JsonProperty("type") String type,
            @JsonProperty("description") String description,
            @JsonProperty("name") String name,
            @JsonProperty("kind") String kind,
            @JsonProperty("required") Boolean required,
            @JsonProperty("displayName") String displayName,
            @JsonProperty("summary") String summary,
            @JsonProperty("comments") String comments,
            @JsonProperty("examples") List<String> examples,
            @JsonProperty("status") String status,
            @JsonProperty("tags") List<String> tags,
            @JsonProperty("externalDocs") String externalDocs,
            @JsonProperty("default") String defaultValue,
            @JsonProperty("isClassified") Boolean isClassified,
            @JsonProperty("classificationLevel") String classificationLevel,
            @JsonProperty("isUnique") Boolean isUnique,
            @JsonProperty("isNullable") Boolean isNullable,
            @JsonProperty("pattern") String pattern,
            @JsonProperty("format") String format,
            @JsonProperty("enum") List<String> enumValues,
            @JsonProperty("minLength") Integer minLength,
            @JsonProperty("maxLength") Integer maxLength,
            @JsonProperty("contentEncoding") String contentEncoding,
            @JsonProperty("contentMediaType") String contentMediaType,
            @JsonProperty("precision") Integer precision,
            @JsonProperty("scale") Integer scale,
            @JsonProperty("minimum") Integer minimum,
            @JsonProperty("exclusiveMinimum") Boolean exclusiveMinimum,
            @JsonProperty("maximum") Integer maximum,
            @JsonProperty("exclusiveMaximum") Boolean exclusiveMaximum,
            @JsonProperty("readOnly") Boolean readOnly,
            @JsonProperty("writeOnly") Boolean writeOnly,
            @JsonProperty("physicalType") String physicalType,
            @JsonProperty("partitionStatus") Boolean partitionStatus,
            @JsonProperty("partitionKeyPosition") Integer partitionKeyPosition,
            @JsonProperty("clusterStatus") Boolean clusterStatus,
            @JsonProperty("clusterKeyPosition") Integer clusterKeyPosition,
            @JsonProperty("ordinalPosition") Integer ordinalPosition,
            @JsonProperty("quality") List<Quality> quality) {
        this.type = type;
        this.description = description;
        this.name = name;
        this.kind = kind;
        this.required = required;
        this.displayName = displayName;
        this.summary = summary;
        this.comments = comments;
        this.examples = examples;
        this.status = status;
        this.tags = tags;
        this.externalDocs = externalDocs;
        this.defaultValue = defaultValue;
        this.isClassified = isClassified;
        this.classificationLevel = classificationLevel;
        this.isUnique = isUnique;
        this.isNullable = isNullable;
        this.pattern = pattern;
        this.format = format;
        this.enumValues = enumValues;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.contentEncoding = contentEncoding;
        this.contentMediaType = contentMediaType;
        this.precision = precision;
        this.scale = scale;
        this.minimum = minimum;
        this.exclusiveMinimum = exclusiveMinimum;
        this.maximum = maximum;
        this.exclusiveMaximum = exclusiveMaximum;
        this.readOnly = readOnly;
        this.writeOnly = writeOnly;
        this.physicalType = physicalType;
        this.partitionStatus = partitionStatus;
        this.partitionKeyPosition = partitionKeyPosition;
        this.clusterStatus = clusterStatus;
        this.clusterKeyPosition = clusterKeyPosition;
        this.ordinalPosition = ordinalPosition;
        this.quality = quality;
    }


    public DataStoreApiBlindataDefinitionProperty() {
        //DO NOTHING
    }

    public String getType() {
        return this.type;
    }

    public String getDescription() {
        return this.description;
    }

    public String getName() {
        return this.name;
    }

    public String getKind() {
        return this.kind;
    }

    public Boolean getRequired() {
        return this.required;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getSummary() {
        return this.summary;
    }

    public String getComments() {
        return this.comments;
    }

    public List<String> getExamples() {
        return this.examples;
    }

    public String getStatus() {
        return this.status;
    }

    public List<String> getTags() {
        return this.tags;
    }

    public String getExternalDocs() {
        return this.externalDocs;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public Boolean getIsClassified() {
        return this.isClassified;
    }

    public String getClassificationLevel() {
        return this.classificationLevel;
    }

    public Boolean getIsUnique() {
        return this.isUnique;
    }

    public Boolean getIsNullable() {
        return this.isNullable;
    }

    public String getPattern() {
        return this.pattern;
    }

    public String getFormat() {
        return this.format;
    }

    public List<String> getEnumValues() {
        return this.enumValues;
    }

    public Integer getMinLength() {
        return this.minLength;
    }

    public Integer getMaxLength() {
        return this.maxLength;
    }

    public String getContentEncoding() {
        return this.contentEncoding;
    }

    public String getContentMediaType() {
        return this.contentMediaType;
    }

    public Integer getPrecision() {
        return this.precision;
    }

    public Integer getScale() {
        return this.scale;
    }

    public Integer getMinimum() {
        return this.minimum;
    }

    public Boolean getExclusiveMinimum() {
        return this.exclusiveMinimum;
    }

    public Integer getMaximum() {
        return this.maximum;
    }

    public Boolean getExclusiveMaximum() {
        return this.exclusiveMaximum;
    }

    public Boolean getReadOnly() {
        return this.readOnly;
    }

    public Boolean getWriteOnly() {
        return this.writeOnly;
    }

    public String getPhysicalType() {
        return this.physicalType;
    }

    public Boolean getPartitionStatus() {
        return this.partitionStatus;
    }

    public Integer getPartitionKeyPosition() {
        return this.partitionKeyPosition;
    }

    public Boolean getClusterStatus() {
        return this.clusterStatus;
    }

    public Integer getClusterKeyPosition() {
        return this.clusterKeyPosition;
    }

    public Integer getOrdinalPosition() {
        return this.ordinalPosition;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setExamples(List<String> examples) {
        this.examples = examples;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setExternalDocs(String externalDocs) {
        this.externalDocs = externalDocs;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setIsClassified(Boolean isClassified) {
        this.isClassified = isClassified;
    }

    public void setClassificationLevel(String classificationLevel) {
        this.classificationLevel = classificationLevel;
    }

    public void setIsUnique(Boolean isUnique) {
        this.isUnique = isUnique;
    }

    public void setIsNullable(Boolean isNullable) {
        this.isNullable = isNullable;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setEnumValues(List<String> enumValues) {
        this.enumValues = enumValues;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public void setContentMediaType(String contentMediaType) {
        this.contentMediaType = contentMediaType;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public void setMinimum(Integer minimum) {
        this.minimum = minimum;
    }

    public void setExclusiveMinimum(Boolean exclusiveMinimum) {
        this.exclusiveMinimum = exclusiveMinimum;
    }

    public void setMaximum(Integer maximum) {
        this.maximum = maximum;
    }

    public void setExclusiveMaximum(Boolean exclusiveMaximum) {
        this.exclusiveMaximum = exclusiveMaximum;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public void setWriteOnly(Boolean writeOnly) {
        this.writeOnly = writeOnly;
    }

    public void setPhysicalType(String physicalType) {
        this.physicalType = physicalType;
    }

    public void setPartitionStatus(Boolean partitionStatus) {
        this.partitionStatus = partitionStatus;
    }

    public void setPartitionKeyPosition(Integer partitionKeyPosition) {
        this.partitionKeyPosition = partitionKeyPosition;
    }

    public void setClusterStatus(Boolean clusterStatus) {
        this.clusterStatus = clusterStatus;
    }

    public void setClusterKeyPosition(Integer clusterKeyPosition) {
        this.clusterKeyPosition = clusterKeyPosition;
    }

    public void setOrdinalPosition(Integer ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    public List<Quality> getQuality() {
        return quality;
    }

    public void setQuality(List<Quality> quality) {
        this.quality = quality;
    }
}
