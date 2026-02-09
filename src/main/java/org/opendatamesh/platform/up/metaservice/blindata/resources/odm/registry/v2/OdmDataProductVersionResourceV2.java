package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.v2;

import com.fasterxml.jackson.databind.JsonNode;

public class OdmDataProductVersionResourceV2 {
    private String uuid;
    private OdmDataProductResourceV2 dataProduct;
    private String name;
    private String description;
    private String tag;
    private String validationState;
    private String spec;
    private String specVersion;
    private String versionNumber;
    private JsonNode content;

    OdmDataProductVersionResourceV2() {
        //DO NOTHING
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public OdmDataProductResourceV2 getDataProduct() {
        return dataProduct;
    }

    public void setDataProduct(OdmDataProductResourceV2 dataProduct) {
        this.dataProduct = dataProduct;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getValidationState() {
        return validationState;
    }

    public void setValidationState(String validationState) {
        this.validationState = validationState;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getSpecVersion() {
        return specVersion;
    }

    public void setSpecVersion(String specVersion) {
        this.specVersion = specVersion;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public JsonNode getContent() {
        return content;
    }

    public void setContent(JsonNode content) {
        this.content = content;
    }
}
