package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode
public class BDDataProductRes {

    private String uuid;

    private String name;

    private String displayName;

    private String identifier;

    private String description;

    private String version;

    private String domain;

    private List<AdditionalPropertiesRes> additionalProperties;

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

    public List<AdditionalPropertiesRes> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(List<AdditionalPropertiesRes> additionalProperties) {
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
        Map<String, AdditionalPropertiesRes> propertyMap = new LinkedHashMap<>();
        for (AdditionalPropertiesRes prop : oldDataProduct.getAdditionalProperties()) {
            propertyMap.put(prop.getName(), prop);
        }
        if (this.getAdditionalProperties() != null) {
            for (AdditionalPropertiesRes prop : this.getAdditionalProperties()) {
                propertyMap.put(prop.getName(), prop);
            }
        }
        this.additionalProperties = new ArrayList<>(propertyMap.values());
    }
}

