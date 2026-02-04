package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.v2;

public class OdmDataProductResourceV2 {

    // TODO: add other fields from DataProductRes ?
    private String uuid;
    private String fqn;
    private String description;
    private String domain;

    public OdmDataProductResourceV2() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFqn() {
        return fqn;
    }

    public void setFqn(String fqn) {
        this.fqn = fqn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
