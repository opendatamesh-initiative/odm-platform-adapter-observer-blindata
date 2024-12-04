package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

import io.swagger.v3.oas.annotations.media.Schema;

public class LogicalFieldSemanticLinkRes {

    @Schema
    private String uuid;

    @Schema(description = "The LogicalField name")
    private String name;

    @Schema(description = "The display name for this resource")
    private String displayName;

    @Schema(description = "The LogicalField description")
    private String description;

    @Schema(description = "The type of data associated to this LogicalField (Ex. Varchar)")
    private String dataType;

    @Schema(description = "The DataCategory associated to this LogicalField")
    private BDDataCategoryRes dataCategory;

    @Schema(description = "The logical namespace for this resource")
    private BDLogicalNamespaceRes namespace;

    @Schema(description = "The semantic link associated to the logical field")
    private SemanticLinkHeaderRes semanticLink;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public BDDataCategoryRes getDataCategory() {
        return dataCategory;
    }

    public void setDataCategory(BDDataCategoryRes dataCategory) {
        this.dataCategory = dataCategory;
    }

    public BDLogicalNamespaceRes getNamespace() {
        return namespace;
    }

    public void setNamespace(BDLogicalNamespaceRes namespace) {
        this.namespace = namespace;
    }

    public SemanticLinkHeaderRes getSemanticLink() {
        return semanticLink;
    }

    public void setSemanticLink(SemanticLinkHeaderRes semanticLink) {
        this.semanticLink = semanticLink;
    }
}
