package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDAdditionalPropertiesRes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BDDataProductRes {

    private String uuid;

    private String name;

    private String displayName;

    private String identifier;

    private String description;

    private String version;

    private String domain;

    private List<BDAdditionalPropertiesRes> additionalProperties;

    private List<BDDataProductPortRes> ports;

    private String productStatus;

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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<BDAdditionalPropertiesRes> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(List<BDAdditionalPropertiesRes> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public List<BDDataProductPortRes> getPorts() {
        return ports;
    }

    public void setPorts(List<BDDataProductPortRes> ports) {
        this.ports = ports;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    public void addOldAdditionalProperties(BDDataProductRes oldDataProduct) {
        if (oldDataProduct == null) return;
        Map<String, BDAdditionalPropertiesRes> propertyMap = new LinkedHashMap<>();
        if (oldDataProduct.getAdditionalProperties() != null) {
            for (BDAdditionalPropertiesRes prop : oldDataProduct.getAdditionalProperties()) {
                propertyMap.put(prop.getName(), prop);
            }
        }
        if (this.getAdditionalProperties() != null) {
            for (BDAdditionalPropertiesRes prop : this.getAdditionalProperties()) {
                propertyMap.put(prop.getName(), prop);
            }
        }
        this.additionalProperties = new ArrayList<>(propertyMap.values());
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof BDDataProductRes)) return false;
        final BDDataProductRes other = (BDDataProductRes) o;
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
        final Object this$domain = this.getDomain();
        final Object other$domain = other.getDomain();
        if (this$domain == null ? other$domain != null : !this$domain.equals(other$domain)) return false;
        final Object this$additionalProperties = this.getAdditionalProperties();
        final Object other$additionalProperties = other.getAdditionalProperties();
        if (this$additionalProperties == null ? other$additionalProperties != null : !this$additionalProperties.equals(other$additionalProperties))
            return false;
        final Object this$ports = this.getPorts();
        final Object other$ports = other.getPorts();
        if (this$ports == null ? other$ports != null : !this$ports.equals(other$ports)) return false;
        final Object this$productStatus = this.getProductStatus();
        final Object other$productStatus = other.getProductStatus();
        if (this$productStatus == null ? other$productStatus != null : !this$productStatus.equals(other$productStatus))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BDDataProductRes;
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
        final Object $domain = this.getDomain();
        result = result * PRIME + ($domain == null ? 43 : $domain.hashCode());
        final Object $additionalProperties = this.getAdditionalProperties();
        result = result * PRIME + ($additionalProperties == null ? 43 : $additionalProperties.hashCode());
        final Object $ports = this.getPorts();
        result = result * PRIME + ($ports == null ? 43 : $ports.hashCode());
        final Object $productStatus = this.getProductStatus();
        result = result * PRIME + ($productStatus == null ? 43 : $productStatus.hashCode());
        return result;
    }
}

