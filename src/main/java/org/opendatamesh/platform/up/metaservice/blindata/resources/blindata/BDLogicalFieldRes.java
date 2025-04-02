package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

public class BDLogicalFieldRes {


    @Schema(description = "The LogicalField resource identifier")
    private String uuid;

    @Schema(description = "The logical namespace for this resource")
    private BDLogicalNamespaceRes namespace;

    @Schema(description = "The LogicalField name")
    private String name;

    @Schema(description = "The display name for this resource")
    private String displayName;

    @Schema(description = "The LogicalField description")
    private String description;

    @Schema(description = "The DataCategory associated to this LogicalField")
    private BDDataCategoryRes dataCategory;

    @Schema(description = "The type of data associated to this LogicalField (Ex. Varchar)")
    private String dataType;

    @Schema(description = "The alias associated to this LogicalField")
    private List<String> aliases;

    @Schema(description = "The rules to calculate attribute")
    private String computationalRules;

    @Schema(description = "The constraints given by business glossary on this field ")
    private String constraintDescription;

    @Schema(description = "Patterns regarding this logical field")
    private String patterns;

    @Schema(description = "List of user defined properties")
    private List<BDAdditionalPropertiesRes> additionalProperties = new ArrayList<>(0);

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

    public BDDataCategoryRes getDataCategory() {
        return dataCategory;
    }

    public void setDataCategory(BDDataCategoryRes dataCategory) {
        this.dataCategory = dataCategory;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public String getComputationalRules() {
        return computationalRules;
    }

    public void setComputationalRules(String computationalRules) {
        this.computationalRules = computationalRules;
    }

    public String getConstraintDescription() {
        return constraintDescription;
    }

    public void setConstraintDescription(String constraintDescription) {
        this.constraintDescription = constraintDescription;
    }

    public String getPatterns() {
        return patterns;
    }

    public void setPatterns(String patterns) {
        this.patterns = patterns;
    }

    public List<BDAdditionalPropertiesRes> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(List<BDAdditionalPropertiesRes> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }
}
