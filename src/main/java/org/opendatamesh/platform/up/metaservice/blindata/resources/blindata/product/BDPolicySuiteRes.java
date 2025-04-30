package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDAdditionalPropertiesRes;

import java.util.List;

public class BDPolicySuiteRes {
    private String uuid;
    private String displayName;
    private String name;
    private String description;
    private List<BDAdditionalPropertiesRes> additionalProperties;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<BDAdditionalPropertiesRes> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(List<BDAdditionalPropertiesRes> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }
}
