package org.opendatamesh.platform.up.metaservice.blindata.resources;

import java.util.List;

public class BlindataDataProductRes {

    private String uuid;

    private String name;

    private String displayName;

    private String identifier;

    private String description;

    private String version;

    private String domain;

    private List<AdditionalPropertyResource> additionalProperties;

    private List<BlindataDataProductPortRes> ports;

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

    public List<AdditionalPropertyResource> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(List<AdditionalPropertyResource> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public List<BlindataDataProductPortRes> getPorts() {
        return ports;
    }

    public void setPorts(List<BlindataDataProductPortRes> ports) {
        this.ports = ports;
    }
}

