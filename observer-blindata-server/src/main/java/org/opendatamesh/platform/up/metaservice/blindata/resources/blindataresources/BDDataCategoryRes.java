package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

public class BDDataCategoryRes {

    @Schema(description = "The DataCategory resource identifier")
    private String uuid;

    @Schema(description = "The logical namespace for this resource")
    private BDLogicalNamespaceRes namespace;

    @Schema(description = "The DataCategory name")
    private String name;

    @Schema(description = "The display name for this resource")
    private String displayName;

    @Schema(description = "The DataCategory description")
    private String description;

    @Schema(description = "The class of this category")
    private String dataClass;

    @Schema(description = "List of user defined properties")
    private List<AdditionalPropertiesRes> additionalProperties =  new ArrayList<>(0);

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public BDLogicalNamespaceRes getNamespace() {
        return namespace;
    }

    public void setNamespace(BDLogicalNamespaceRes namespace) {
        this.namespace = namespace;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDataClass() {
        return dataClass;
    }

    public void setDataClass(String dataClass) {
        this.dataClass = dataClass;
    }

    public List<AdditionalPropertiesRes> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(List<AdditionalPropertiesRes> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }
}
