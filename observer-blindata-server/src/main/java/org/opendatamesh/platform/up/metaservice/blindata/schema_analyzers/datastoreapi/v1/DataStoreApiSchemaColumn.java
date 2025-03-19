package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
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


}
