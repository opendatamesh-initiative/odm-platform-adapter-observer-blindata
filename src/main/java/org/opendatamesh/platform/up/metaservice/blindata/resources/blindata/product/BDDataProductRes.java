package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDAdditionalPropertiesRes;

import java.util.*;

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

    private String productType;

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

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public void addOldAdditionalProperties(BDDataProductRes oldDataProduct) {
        if (oldDataProduct == null) return;
        List<BDAdditionalPropertiesRes> merged = new ArrayList<>();
        if (oldDataProduct.getAdditionalProperties() != null) {
            merged.addAll(oldDataProduct.getAdditionalProperties());
        }
        if (this.getAdditionalProperties() != null) {
            for (BDAdditionalPropertiesRes prop : this.getAdditionalProperties()) {
                boolean exists = merged.stream().anyMatch(p ->
                        Objects.equals(p.getName(), prop.getName()) &&
                                Objects.equals(p.getValue(), prop.getValue())
                );
                if (!exists) {
                    merged.add(prop);
                }
            }
        }
        this.additionalProperties = merged;
    }

}

