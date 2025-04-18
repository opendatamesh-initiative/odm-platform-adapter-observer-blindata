package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.opendatamesh.dpds.model.core.ComponentBase;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1.model.quality.Quality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStoreApiBlindataDefinition extends ComponentBase {
    private String name;
    private String kind;
    private String type;
    private String comments;
    private String status;
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> tags = new ArrayList<>();
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
    private Map<String, DataStoreApiBlindataDefinitionProperty> properties = new HashMap<>();

    @JsonProperty("s-context")
    private Map<String, Object> sContext;

    private List<Quality> quality = new ArrayList<>();

    public DataStoreApiBlindataDefinition() {
        //DO NOTHING
    }

    public Map<String, DataStoreApiBlindataDefinitionProperty> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, DataStoreApiBlindataDefinitionProperty> properties) {
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getContactPoints() {
        return contactPoints;
    }

    public void setContactPoints(String contactPoints) {
        this.contactPoints = contactPoints;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public void setFullyQualifiedName(String fullyQualifiedName) {
        this.fullyQualifiedName = fullyQualifiedName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhysicalType() {
        return physicalType;
    }

    public void setPhysicalType(String physicalType) {
        this.physicalType = physicalType;
    }

    public String getExternalDocs() {
        return externalDocs;
    }

    public void setExternalDocs(String externalDocs) {
        this.externalDocs = externalDocs;
    }

    public Map<String, Object> getsContext() {
        return sContext;
    }

    public void setsContext(Map<String, Object> sContext) {
        this.sContext = sContext;
    }

    public List<Quality> getQuality() {
        return quality;
    }

    public void setQuality(List<Quality> quality) {
        this.quality = quality;
    }
}
