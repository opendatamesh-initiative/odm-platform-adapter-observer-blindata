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
    private String comments;
    private String status;
    private String tags;
    private String owner;
    private String domain;
    private String contactpoints;
    private String scope;
    private String version;
    private String fullyQualifiedName;
    private String displayName;
    private String description;
    private String physicalType;


    private Map<String, SchemaColumn> properties;

    @JsonCreator
    public SchemaEntity(
            @JsonProperty("name") String name,
            @JsonProperty("kind") String kind,
            @JsonProperty("comments") String comments,
            @JsonProperty("status") String status,
            @JsonProperty("tags") String tags,
            @JsonProperty("owner") String owner,
            @JsonProperty("domain") String domain,
            @JsonProperty("contactpoints") String contactpoints,
            @JsonProperty("scope") String scope,
            @JsonProperty("version") String version,
            @JsonProperty("fullyQualifiedName") String fullyQualifiedName,
            @JsonProperty("displayName") String displayName,
            @JsonProperty("description") String description,
            @JsonProperty("physicalType") String physicalType,
            @JsonProperty("properties") Map<String, SchemaColumn> properties
    ) {
        this.name = name;
        this.kind = kind;
        this.comments = comments;
        this.status = status;
        this.tags = tags;
        this.owner = owner;
        this.domain = domain;
        this.contactpoints = contactpoints;
        this.scope = scope;
        this.version = version;
        this.fullyQualifiedName = fullyQualifiedName;
        this.displayName = displayName;
        this.description = description;
        this.physicalType = physicalType;
        this.properties = properties;
    }
}


