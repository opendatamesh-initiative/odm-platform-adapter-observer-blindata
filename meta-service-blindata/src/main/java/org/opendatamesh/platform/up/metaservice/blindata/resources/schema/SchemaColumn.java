package org.opendatamesh.platform.up.metaservice.blindata.resources.schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SchemaColumn {


    private String type;
    private String description;
    private String name;
    private String kind;
    private boolean required;
    private String displayName;
    private String summary;
    private String comments;
    private List<String> examples;
    private String status;
    private List<String> tags;
    private String externalDocs;
    private String defaultValue;
    private boolean isClassified;
    private String classificationLevel;
    private boolean isUnique;
    private boolean isNullable;
    private String pattern;
    private String format;
    private List<String> enumValues;
    private int minLength;
    private int maxLength;
    private String contentEncoding;
    private String contentMediaType;
    private int precision;
    private int scale;
    private int minimum;
    private boolean exclusiveMinimum;
    private int maximum;
    private boolean exclusiveMaximum;
    private boolean readOnly;
    private boolean writeOnly;
    private String physicalType;
    private boolean partitionStatus;
    private int partitionKeyPosition;
    private boolean clusterStatus;
    private int clusterKeyPosition;

    @JsonCreator
    public SchemaColumn(
            @JsonProperty("type") String type,
            @JsonProperty("description") String description,
            @JsonProperty("name") String name,
            @JsonProperty("kind") String kind,
            @JsonProperty("required") boolean required,
            @JsonProperty("displayName") String displayName,
            @JsonProperty("summary") String summary,
            @JsonProperty("comments") String comments,
            @JsonProperty("examples") List<String> examples,
            @JsonProperty("status") String status,
            @JsonProperty("tags") List<String> tags,
            @JsonProperty("externalDocs") String externalDocs,
            @JsonProperty("default") String defaultValue,
            @JsonProperty("isClassified") boolean isClassified,
            @JsonProperty("classificationLevel") String classificationLevel,
            @JsonProperty("isUnique") boolean isUnique,
            @JsonProperty("isNullable") boolean isNullable,
            @JsonProperty("pattern") String pattern,
            @JsonProperty("format") String format,
            @JsonProperty("enum") List<String> enumValues,
            @JsonProperty("minLength") int minLength,
            @JsonProperty("maxLength") int maxLength,
            @JsonProperty("contentEncoding") String contentEncoding,
            @JsonProperty("contentMediaType") String contentMediaType,
            @JsonProperty("precision") int precision,
            @JsonProperty("scale") int scale,
            @JsonProperty("minimum") int minimum,
            @JsonProperty("exclusiveMinimum") boolean exclusiveMinimum,
            @JsonProperty("maximum") int maximum,
            @JsonProperty("exclusiveMaximum") boolean exclusiveMaximum,
            @JsonProperty("readOnly") boolean readOnly,
            @JsonProperty("writeOnly") boolean writeOnly,
            @JsonProperty("physicalType") String physicalType,
            @JsonProperty("partitionStatus") boolean partitionStatus,
            @JsonProperty("partitionKeyPosition") int partitionKeyPosition,
            @JsonProperty("clusterStatus") boolean clusterStatus,
            @JsonProperty("clusterKeyPosition") int clusterKeyPosition) {
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
    }


}
