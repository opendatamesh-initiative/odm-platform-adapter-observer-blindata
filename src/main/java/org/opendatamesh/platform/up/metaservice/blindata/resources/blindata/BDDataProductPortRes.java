package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata;

import java.util.List;

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

    private BDSystemRes dependsOnSystem;

    private List<BDAdditionalPropertiesRes> additionalProperties;


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

    public List<BDAdditionalPropertiesRes> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(List<BDAdditionalPropertiesRes> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public BDSystemRes getDependsOnSystem() {
        return dependsOnSystem;
    }

    public void setDependsOnSystem(BDSystemRes dependsOnSystem) {
        this.dependsOnSystem = dependsOnSystem;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof BDDataProductPortRes)) return false;
        final BDDataProductPortRes other = (BDDataProductPortRes) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$uuid = this.getUuid();
        final Object other$uuid = other.getUuid();
        if (this$uuid == null ? other$uuid != null : !this$uuid.equals(other$uuid)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$displayName = this.getDisplayName();
        final Object other$displayName = other.getDisplayName();
        if (this$displayName == null ? other$displayName != null : !this$displayName.equals(other$displayName))
            return false;
        final Object this$identifier = this.getIdentifier();
        final Object other$identifier = other.getIdentifier();
        if (this$identifier == null ? other$identifier != null : !this$identifier.equals(other$identifier))
            return false;
        final Object this$description = this.getDescription();
        final Object other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description))
            return false;
        final Object this$version = this.getVersion();
        final Object other$version = other.getVersion();
        if (this$version == null ? other$version != null : !this$version.equals(other$version)) return false;
        final Object this$servicesType = this.getServicesType();
        final Object other$servicesType = other.getServicesType();
        if (this$servicesType == null ? other$servicesType != null : !this$servicesType.equals(other$servicesType))
            return false;
        final Object this$entityType = this.getEntityType();
        final Object other$entityType = other.getEntityType();
        if (this$entityType == null ? other$entityType != null : !this$entityType.equals(other$entityType))
            return false;
        final Object this$dependsOnIdentifier = this.getDependsOnIdentifier();
        final Object other$dependsOnIdentifier = other.getDependsOnIdentifier();
        if (this$dependsOnIdentifier == null ? other$dependsOnIdentifier != null : !this$dependsOnIdentifier.equals(other$dependsOnIdentifier))
            return false;
        final Object this$dependsOnSystem = this.getDependsOnSystem();
        final Object other$dependsOnSystem = other.getDependsOnSystem();
        if (this$dependsOnSystem == null ? other$dependsOnSystem != null : !this$dependsOnSystem.equals(other$dependsOnSystem))
            return false;
        final Object this$additionalProperties = this.getAdditionalProperties();
        final Object other$additionalProperties = other.getAdditionalProperties();
        if (this$additionalProperties == null ? other$additionalProperties != null : !this$additionalProperties.equals(other$additionalProperties))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BDDataProductPortRes;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $uuid = this.getUuid();
        result = result * PRIME + ($uuid == null ? 43 : $uuid.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $displayName = this.getDisplayName();
        result = result * PRIME + ($displayName == null ? 43 : $displayName.hashCode());
        final Object $identifier = this.getIdentifier();
        result = result * PRIME + ($identifier == null ? 43 : $identifier.hashCode());
        final Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        final Object $version = this.getVersion();
        result = result * PRIME + ($version == null ? 43 : $version.hashCode());
        final Object $servicesType = this.getServicesType();
        result = result * PRIME + ($servicesType == null ? 43 : $servicesType.hashCode());
        final Object $entityType = this.getEntityType();
        result = result * PRIME + ($entityType == null ? 43 : $entityType.hashCode());
        final Object $dependsOnIdentifier = this.getDependsOnIdentifier();
        result = result * PRIME + ($dependsOnIdentifier == null ? 43 : $dependsOnIdentifier.hashCode());
        final Object $dependsOnSystem = this.getDependsOnSystem();
        result = result * PRIME + ($dependsOnSystem == null ? 43 : $dependsOnSystem.hashCode());
        final Object $additionalProperties = this.getAdditionalProperties();
        result = result * PRIME + ($additionalProperties == null ? 43 : $additionalProperties.hashCode());
        return result;
    }
}
