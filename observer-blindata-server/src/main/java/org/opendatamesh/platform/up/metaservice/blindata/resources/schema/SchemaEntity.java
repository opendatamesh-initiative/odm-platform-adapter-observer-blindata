package org.opendatamesh.platform.up.metaservice.blindata.resources.schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchemaEntity {

    private String name;
    private String kind;
    private String type;
    private String comments;
    private String status;
    private List<String> tags;
    private String owner;
    private String domain;
    private String contactPoints;
    private String scope;
    private String version;
    private String fullyQualifiedName;
    private String displayName;
    private String description;
    private String physicalType;
    private String externalDocs;


    private Map<String, SchemaColumn> properties;

    @JsonCreator
    public SchemaEntity(
            @JsonProperty("name") String name,
            @JsonProperty("kind") String kind,
            @JsonProperty("comments") String comments,
            @JsonProperty("status") String status,
            @JsonProperty("tags") List<String> tags,
            @JsonProperty("owner") String owner,
            @JsonProperty("domain") String domain,
            @JsonProperty("contactPoints") String contactPoints,
            @JsonProperty("scope") String scope,
            @JsonProperty("version") String version,
            @JsonProperty("fullyQualifiedName") String fullyQualifiedName,
            @JsonProperty("displayName") String displayName,
            @JsonProperty("description") String description,
            @JsonProperty("physicalType") String physicalType,
            @JsonProperty("externalDocs") String externalDocs,
            @JsonProperty("type") String type,
            @JsonProperty("properties") Map<String, SchemaColumn> properties
    ) {
        this.name = name;
        this.kind = kind;
        this.comments = comments;
        this.status = status;
        this.tags = tags;
        this.owner = owner;
        this.domain = domain;
        this.contactPoints = contactPoints;
        this.scope = scope;
        this.version = version;
        this.fullyQualifiedName = fullyQualifiedName;
        this.displayName = displayName;
        this.description = description;
        this.physicalType = physicalType;
        this.type = type;
        this.properties = properties;
        this.externalDocs = externalDocs;
    }
}


