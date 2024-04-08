package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode
public class BDDataProductPortRes {

    private String uuid;

    private String name;

    private String displayName;

    private String identifier;

    private String description;

    private String version;

    private String servicesType;

    private String entityType;

    private String dependsOnIdentifier;

    private List<AdditionalPropertiesRes> additionalProperties;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getServicesType() {
        return servicesType;
    }

    public void setServicesType(String servicesType) {
        this.servicesType = servicesType;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getDependsOnIdentifier() {
        return dependsOnIdentifier;
    }

    public void setDependsOnIdentifier(String dependsOnIdentifier) {
        this.dependsOnIdentifier = dependsOnIdentifier;
    }

    public List<AdditionalPropertiesRes> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(List<AdditionalPropertiesRes> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }
}
