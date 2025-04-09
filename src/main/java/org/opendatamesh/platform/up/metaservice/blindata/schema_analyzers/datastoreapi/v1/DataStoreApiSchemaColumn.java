package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
class DataStoreApiSchemaColumn {

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

    @JsonCreator
    public DataStoreApiSchemaColumn(
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
            @JsonProperty("ordinalPosition") Integer ordinalPosition) {
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
    }


    public DataStoreApiSchemaColumn() {
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

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof DataStoreApiSchemaColumn)) return false;
        final DataStoreApiSchemaColumn other = (DataStoreApiSchemaColumn) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$type = this.getType();
        final Object other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
        final Object this$description = this.getDescription();
        final Object other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description))
            return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$kind = this.getKind();
        final Object other$kind = other.getKind();
        if (this$kind == null ? other$kind != null : !this$kind.equals(other$kind)) return false;
        final Object this$required = this.getRequired();
        final Object other$required = other.getRequired();
        if (this$required == null ? other$required != null : !this$required.equals(other$required)) return false;
        final Object this$displayName = this.getDisplayName();
        final Object other$displayName = other.getDisplayName();
        if (this$displayName == null ? other$displayName != null : !this$displayName.equals(other$displayName))
            return false;
        final Object this$summary = this.getSummary();
        final Object other$summary = other.getSummary();
        if (this$summary == null ? other$summary != null : !this$summary.equals(other$summary)) return false;
        final Object this$comments = this.getComments();
        final Object other$comments = other.getComments();
        if (this$comments == null ? other$comments != null : !this$comments.equals(other$comments)) return false;
        final Object this$examples = this.getExamples();
        final Object other$examples = other.getExamples();
        if (this$examples == null ? other$examples != null : !this$examples.equals(other$examples)) return false;
        final Object this$status = this.getStatus();
        final Object other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) return false;
        final Object this$tags = this.getTags();
        final Object other$tags = other.getTags();
        if (this$tags == null ? other$tags != null : !this$tags.equals(other$tags)) return false;
        final Object this$externalDocs = this.getExternalDocs();
        final Object other$externalDocs = other.getExternalDocs();
        if (this$externalDocs == null ? other$externalDocs != null : !this$externalDocs.equals(other$externalDocs))
            return false;
        final Object this$defaultValue = this.getDefaultValue();
        final Object other$defaultValue = other.getDefaultValue();
        if (this$defaultValue == null ? other$defaultValue != null : !this$defaultValue.equals(other$defaultValue))
            return false;
        final Object this$isClassified = this.getIsClassified();
        final Object other$isClassified = other.getIsClassified();
        if (this$isClassified == null ? other$isClassified != null : !this$isClassified.equals(other$isClassified))
            return false;
        final Object this$classificationLevel = this.getClassificationLevel();
        final Object other$classificationLevel = other.getClassificationLevel();
        if (this$classificationLevel == null ? other$classificationLevel != null : !this$classificationLevel.equals(other$classificationLevel))
            return false;
        final Object this$isUnique = this.getIsUnique();
        final Object other$isUnique = other.getIsUnique();
        if (this$isUnique == null ? other$isUnique != null : !this$isUnique.equals(other$isUnique)) return false;
        final Object this$isNullable = this.getIsNullable();
        final Object other$isNullable = other.getIsNullable();
        if (this$isNullable == null ? other$isNullable != null : !this$isNullable.equals(other$isNullable))
            return false;
        final Object this$pattern = this.getPattern();
        final Object other$pattern = other.getPattern();
        if (this$pattern == null ? other$pattern != null : !this$pattern.equals(other$pattern)) return false;
        final Object this$format = this.getFormat();
        final Object other$format = other.getFormat();
        if (this$format == null ? other$format != null : !this$format.equals(other$format)) return false;
        final Object this$enumValues = this.getEnumValues();
        final Object other$enumValues = other.getEnumValues();
        if (this$enumValues == null ? other$enumValues != null : !this$enumValues.equals(other$enumValues))
            return false;
        final Object this$minLength = this.getMinLength();
        final Object other$minLength = other.getMinLength();
        if (this$minLength == null ? other$minLength != null : !this$minLength.equals(other$minLength)) return false;
        final Object this$maxLength = this.getMaxLength();
        final Object other$maxLength = other.getMaxLength();
        if (this$maxLength == null ? other$maxLength != null : !this$maxLength.equals(other$maxLength)) return false;
        final Object this$contentEncoding = this.getContentEncoding();
        final Object other$contentEncoding = other.getContentEncoding();
        if (this$contentEncoding == null ? other$contentEncoding != null : !this$contentEncoding.equals(other$contentEncoding))
            return false;
        final Object this$contentMediaType = this.getContentMediaType();
        final Object other$contentMediaType = other.getContentMediaType();
        if (this$contentMediaType == null ? other$contentMediaType != null : !this$contentMediaType.equals(other$contentMediaType))
            return false;
        final Object this$precision = this.getPrecision();
        final Object other$precision = other.getPrecision();
        if (this$precision == null ? other$precision != null : !this$precision.equals(other$precision)) return false;
        final Object this$scale = this.getScale();
        final Object other$scale = other.getScale();
        if (this$scale == null ? other$scale != null : !this$scale.equals(other$scale)) return false;
        final Object this$minimum = this.getMinimum();
        final Object other$minimum = other.getMinimum();
        if (this$minimum == null ? other$minimum != null : !this$minimum.equals(other$minimum)) return false;
        final Object this$exclusiveMinimum = this.getExclusiveMinimum();
        final Object other$exclusiveMinimum = other.getExclusiveMinimum();
        if (this$exclusiveMinimum == null ? other$exclusiveMinimum != null : !this$exclusiveMinimum.equals(other$exclusiveMinimum))
            return false;
        final Object this$maximum = this.getMaximum();
        final Object other$maximum = other.getMaximum();
        if (this$maximum == null ? other$maximum != null : !this$maximum.equals(other$maximum)) return false;
        final Object this$exclusiveMaximum = this.getExclusiveMaximum();
        final Object other$exclusiveMaximum = other.getExclusiveMaximum();
        if (this$exclusiveMaximum == null ? other$exclusiveMaximum != null : !this$exclusiveMaximum.equals(other$exclusiveMaximum))
            return false;
        final Object this$readOnly = this.getReadOnly();
        final Object other$readOnly = other.getReadOnly();
        if (this$readOnly == null ? other$readOnly != null : !this$readOnly.equals(other$readOnly)) return false;
        final Object this$writeOnly = this.getWriteOnly();
        final Object other$writeOnly = other.getWriteOnly();
        if (this$writeOnly == null ? other$writeOnly != null : !this$writeOnly.equals(other$writeOnly)) return false;
        final Object this$physicalType = this.getPhysicalType();
        final Object other$physicalType = other.getPhysicalType();
        if (this$physicalType == null ? other$physicalType != null : !this$physicalType.equals(other$physicalType))
            return false;
        final Object this$partitionStatus = this.getPartitionStatus();
        final Object other$partitionStatus = other.getPartitionStatus();
        if (this$partitionStatus == null ? other$partitionStatus != null : !this$partitionStatus.equals(other$partitionStatus))
            return false;
        final Object this$partitionKeyPosition = this.getPartitionKeyPosition();
        final Object other$partitionKeyPosition = other.getPartitionKeyPosition();
        if (this$partitionKeyPosition == null ? other$partitionKeyPosition != null : !this$partitionKeyPosition.equals(other$partitionKeyPosition))
            return false;
        final Object this$clusterStatus = this.getClusterStatus();
        final Object other$clusterStatus = other.getClusterStatus();
        if (this$clusterStatus == null ? other$clusterStatus != null : !this$clusterStatus.equals(other$clusterStatus))
            return false;
        final Object this$clusterKeyPosition = this.getClusterKeyPosition();
        final Object other$clusterKeyPosition = other.getClusterKeyPosition();
        if (this$clusterKeyPosition == null ? other$clusterKeyPosition != null : !this$clusterKeyPosition.equals(other$clusterKeyPosition))
            return false;
        final Object this$ordinalPosition = this.getOrdinalPosition();
        final Object other$ordinalPosition = other.getOrdinalPosition();
        if (this$ordinalPosition == null ? other$ordinalPosition != null : !this$ordinalPosition.equals(other$ordinalPosition))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof DataStoreApiSchemaColumn;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        final Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $kind = this.getKind();
        result = result * PRIME + ($kind == null ? 43 : $kind.hashCode());
        final Object $required = this.getRequired();
        result = result * PRIME + ($required == null ? 43 : $required.hashCode());
        final Object $displayName = this.getDisplayName();
        result = result * PRIME + ($displayName == null ? 43 : $displayName.hashCode());
        final Object $summary = this.getSummary();
        result = result * PRIME + ($summary == null ? 43 : $summary.hashCode());
        final Object $comments = this.getComments();
        result = result * PRIME + ($comments == null ? 43 : $comments.hashCode());
        final Object $examples = this.getExamples();
        result = result * PRIME + ($examples == null ? 43 : $examples.hashCode());
        final Object $status = this.getStatus();
        result = result * PRIME + ($status == null ? 43 : $status.hashCode());
        final Object $tags = this.getTags();
        result = result * PRIME + ($tags == null ? 43 : $tags.hashCode());
        final Object $externalDocs = this.getExternalDocs();
        result = result * PRIME + ($externalDocs == null ? 43 : $externalDocs.hashCode());
        final Object $defaultValue = this.getDefaultValue();
        result = result * PRIME + ($defaultValue == null ? 43 : $defaultValue.hashCode());
        final Object $isClassified = this.getIsClassified();
        result = result * PRIME + ($isClassified == null ? 43 : $isClassified.hashCode());
        final Object $classificationLevel = this.getClassificationLevel();
        result = result * PRIME + ($classificationLevel == null ? 43 : $classificationLevel.hashCode());
        final Object $isUnique = this.getIsUnique();
        result = result * PRIME + ($isUnique == null ? 43 : $isUnique.hashCode());
        final Object $isNullable = this.getIsNullable();
        result = result * PRIME + ($isNullable == null ? 43 : $isNullable.hashCode());
        final Object $pattern = this.getPattern();
        result = result * PRIME + ($pattern == null ? 43 : $pattern.hashCode());
        final Object $format = this.getFormat();
        result = result * PRIME + ($format == null ? 43 : $format.hashCode());
        final Object $enumValues = this.getEnumValues();
        result = result * PRIME + ($enumValues == null ? 43 : $enumValues.hashCode());
        final Object $minLength = this.getMinLength();
        result = result * PRIME + ($minLength == null ? 43 : $minLength.hashCode());
        final Object $maxLength = this.getMaxLength();
        result = result * PRIME + ($maxLength == null ? 43 : $maxLength.hashCode());
        final Object $contentEncoding = this.getContentEncoding();
        result = result * PRIME + ($contentEncoding == null ? 43 : $contentEncoding.hashCode());
        final Object $contentMediaType = this.getContentMediaType();
        result = result * PRIME + ($contentMediaType == null ? 43 : $contentMediaType.hashCode());
        final Object $precision = this.getPrecision();
        result = result * PRIME + ($precision == null ? 43 : $precision.hashCode());
        final Object $scale = this.getScale();
        result = result * PRIME + ($scale == null ? 43 : $scale.hashCode());
        final Object $minimum = this.getMinimum();
        result = result * PRIME + ($minimum == null ? 43 : $minimum.hashCode());
        final Object $exclusiveMinimum = this.getExclusiveMinimum();
        result = result * PRIME + ($exclusiveMinimum == null ? 43 : $exclusiveMinimum.hashCode());
        final Object $maximum = this.getMaximum();
        result = result * PRIME + ($maximum == null ? 43 : $maximum.hashCode());
        final Object $exclusiveMaximum = this.getExclusiveMaximum();
        result = result * PRIME + ($exclusiveMaximum == null ? 43 : $exclusiveMaximum.hashCode());
        final Object $readOnly = this.getReadOnly();
        result = result * PRIME + ($readOnly == null ? 43 : $readOnly.hashCode());
        final Object $writeOnly = this.getWriteOnly();
        result = result * PRIME + ($writeOnly == null ? 43 : $writeOnly.hashCode());
        final Object $physicalType = this.getPhysicalType();
        result = result * PRIME + ($physicalType == null ? 43 : $physicalType.hashCode());
        final Object $partitionStatus = this.getPartitionStatus();
        result = result * PRIME + ($partitionStatus == null ? 43 : $partitionStatus.hashCode());
        final Object $partitionKeyPosition = this.getPartitionKeyPosition();
        result = result * PRIME + ($partitionKeyPosition == null ? 43 : $partitionKeyPosition.hashCode());
        final Object $clusterStatus = this.getClusterStatus();
        result = result * PRIME + ($clusterStatus == null ? 43 : $clusterStatus.hashCode());
        final Object $clusterKeyPosition = this.getClusterKeyPosition();
        result = result * PRIME + ($clusterKeyPosition == null ? 43 : $clusterKeyPosition.hashCode());
        final Object $ordinalPosition = this.getOrdinalPosition();
        result = result * PRIME + ($ordinalPosition == null ? 43 : $ordinalPosition.hashCode());
        return result;
    }

    public String toString() {
        return "DataStoreApiSchemaColumn(type=" + this.getType() + ", description=" + this.getDescription() + ", name=" + this.getName() + ", kind=" + this.getKind() + ", required=" + this.getRequired() + ", displayName=" + this.getDisplayName() + ", summary=" + this.getSummary() + ", comments=" + this.getComments() + ", examples=" + this.getExamples() + ", status=" + this.getStatus() + ", tags=" + this.getTags() + ", externalDocs=" + this.getExternalDocs() + ", defaultValue=" + this.getDefaultValue() + ", isClassified=" + this.getIsClassified() + ", classificationLevel=" + this.getClassificationLevel() + ", isUnique=" + this.getIsUnique() + ", isNullable=" + this.getIsNullable() + ", pattern=" + this.getPattern() + ", format=" + this.getFormat() + ", enumValues=" + this.getEnumValues() + ", minLength=" + this.getMinLength() + ", maxLength=" + this.getMaxLength() + ", contentEncoding=" + this.getContentEncoding() + ", contentMediaType=" + this.getContentMediaType() + ", precision=" + this.getPrecision() + ", scale=" + this.getScale() + ", minimum=" + this.getMinimum() + ", exclusiveMinimum=" + this.getExclusiveMinimum() + ", maximum=" + this.getMaximum() + ", exclusiveMaximum=" + this.getExclusiveMaximum() + ", readOnly=" + this.getReadOnly() + ", writeOnly=" + this.getWriteOnly() + ", physicalType=" + this.getPhysicalType() + ", partitionStatus=" + this.getPartitionStatus() + ", partitionKeyPosition=" + this.getPartitionKeyPosition() + ", clusterStatus=" + this.getClusterStatus() + ", clusterKeyPosition=" + this.getClusterKeyPosition() + ", ordinalPosition=" + this.getOrdinalPosition() + ")";
    }
}
