package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical;

import io.swagger.v3.oas.annotations.media.Schema;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDAdditionalPropertiesRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical.BDDataCategoryRes;

import java.util.*;

public class BDPhysicalEntityRes {

    @Schema(description = "The PhysicalEntity resource identifier")
    private String uuid;

    @Schema(description = "The PhysicalEntity schema")
    private String schema;

    @Schema(description = "The PhysicalEntity name")
    private String name;

    @Schema(description = "The PhysicalEntity description")
    private String description;

    @Schema(description = "The dataSet label, a distinguishable name for this entity")
    private String dataSet;

    @Schema(description = "The date of creation of this entity in the system")
    private Date creationDate;

    @Schema(description = "The date of modification of this entity in the system")
    private Date modificationDate;

    @Schema(description = "The set of PhysicalFields associated to this PhysicalEntity", hidden = true)
    private Set<BDPhysicalFieldRes> physicalFields = new HashSet<>();

    @Schema(description = "The set of DataCategories contained in this PhysicalEntity")
    private Set<BDDataCategoryRes> dataCategories;

    @Schema(description = "The System this PhysicalEntity belongs to")
    private BDSystemRes system;

    @Schema(description = "Specifies whether a physical entity is a view")
    private Boolean isConsentView;

    @Schema(description = "Specifies whether a physical entity is hidden or not")
    private Boolean isHidden;

    @Schema(description = "The physical entity type")
    private String tableType;

    @Schema(description = "List of user defined properties")
    private List<BDAdditionalPropertiesRes> additionalProperties = new ArrayList<>();

    public BDPhysicalEntityRes() {
    }

    public String getUuid() {
        return this.uuid;
    }

    public String getSchema() {
        return this.schema;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getDataSet() {
        return this.dataSet;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public Date getModificationDate() {
        return this.modificationDate;
    }

    public Set<BDPhysicalFieldRes> getPhysicalFields() {
        return this.physicalFields;
    }

    public Set<BDDataCategoryRes> getDataCategories() {
        return this.dataCategories;
    }

    public BDSystemRes getSystem() {
        return this.system;
    }

    public Boolean getIsConsentView() {
        return this.isConsentView;
    }

    public Boolean getIsHidden() {
        return this.isHidden;
    }

    public String getTableType() {
        return this.tableType;
    }

    public List<BDAdditionalPropertiesRes> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDataSet(String dataSet) {
        this.dataSet = dataSet;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public void setPhysicalFields(Set<BDPhysicalFieldRes> physicalFields) {
        this.physicalFields = physicalFields;
    }

    public void setDataCategories(Set<BDDataCategoryRes> dataCategories) {
        this.dataCategories = dataCategories;
    }

    public void setSystem(BDSystemRes system) {
        this.system = system;
    }

    public void setIsConsentView(Boolean isConsentView) {
        this.isConsentView = isConsentView;
    }

    public void setIsHidden(Boolean isHidden) {
        this.isHidden = isHidden;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public void setAdditionalProperties(List<BDAdditionalPropertiesRes> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof BDPhysicalEntityRes)) return false;
        final BDPhysicalEntityRes other = (BDPhysicalEntityRes) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$uuid = this.getUuid();
        final Object other$uuid = other.getUuid();
        if (this$uuid == null ? other$uuid != null : !this$uuid.equals(other$uuid)) return false;
        final Object this$schema = this.getSchema();
        final Object other$schema = other.getSchema();
        if (this$schema == null ? other$schema != null : !this$schema.equals(other$schema)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$description = this.getDescription();
        final Object other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description))
            return false;
        final Object this$dataSet = this.getDataSet();
        final Object other$dataSet = other.getDataSet();
        if (this$dataSet == null ? other$dataSet != null : !this$dataSet.equals(other$dataSet)) return false;
        final Object this$creationDate = this.getCreationDate();
        final Object other$creationDate = other.getCreationDate();
        if (this$creationDate == null ? other$creationDate != null : !this$creationDate.equals(other$creationDate))
            return false;
        final Object this$modificationDate = this.getModificationDate();
        final Object other$modificationDate = other.getModificationDate();
        if (this$modificationDate == null ? other$modificationDate != null : !this$modificationDate.equals(other$modificationDate))
            return false;
        final Object this$physicalFields = this.getPhysicalFields();
        final Object other$physicalFields = other.getPhysicalFields();
        if (this$physicalFields == null ? other$physicalFields != null : !this$physicalFields.equals(other$physicalFields))
            return false;
        final Object this$dataCategories = this.getDataCategories();
        final Object other$dataCategories = other.getDataCategories();
        if (this$dataCategories == null ? other$dataCategories != null : !this$dataCategories.equals(other$dataCategories))
            return false;
        final Object this$system = this.getSystem();
        final Object other$system = other.getSystem();
        if (this$system == null ? other$system != null : !this$system.equals(other$system)) return false;
        final Object this$isConsentView = this.getIsConsentView();
        final Object other$isConsentView = other.getIsConsentView();
        if (this$isConsentView == null ? other$isConsentView != null : !this$isConsentView.equals(other$isConsentView))
            return false;
        final Object this$isHidden = this.getIsHidden();
        final Object other$isHidden = other.getIsHidden();
        if (this$isHidden == null ? other$isHidden != null : !this$isHidden.equals(other$isHidden)) return false;
        final Object this$tableType = this.getTableType();
        final Object other$tableType = other.getTableType();
        if (this$tableType == null ? other$tableType != null : !this$tableType.equals(other$tableType)) return false;
        final Object this$additionalProperties = this.getAdditionalProperties();
        final Object other$additionalProperties = other.getAdditionalProperties();
        if (this$additionalProperties == null ? other$additionalProperties != null : !this$additionalProperties.equals(other$additionalProperties))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BDPhysicalEntityRes;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $uuid = this.getUuid();
        result = result * PRIME + ($uuid == null ? 43 : $uuid.hashCode());
        final Object $schema = this.getSchema();
        result = result * PRIME + ($schema == null ? 43 : $schema.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        final Object $dataSet = this.getDataSet();
        result = result * PRIME + ($dataSet == null ? 43 : $dataSet.hashCode());
        final Object $creationDate = this.getCreationDate();
        result = result * PRIME + ($creationDate == null ? 43 : $creationDate.hashCode());
        final Object $modificationDate = this.getModificationDate();
        result = result * PRIME + ($modificationDate == null ? 43 : $modificationDate.hashCode());
        final Object $physicalFields = this.getPhysicalFields();
        result = result * PRIME + ($physicalFields == null ? 43 : $physicalFields.hashCode());
        final Object $dataCategories = this.getDataCategories();
        result = result * PRIME + ($dataCategories == null ? 43 : $dataCategories.hashCode());
        final Object $system = this.getSystem();
        result = result * PRIME + ($system == null ? 43 : $system.hashCode());
        final Object $isConsentView = this.getIsConsentView();
        result = result * PRIME + ($isConsentView == null ? 43 : $isConsentView.hashCode());
        final Object $isHidden = this.getIsHidden();
        result = result * PRIME + ($isHidden == null ? 43 : $isHidden.hashCode());
        final Object $tableType = this.getTableType();
        result = result * PRIME + ($tableType == null ? 43 : $tableType.hashCode());
        final Object $additionalProperties = this.getAdditionalProperties();
        result = result * PRIME + ($additionalProperties == null ? 43 : $additionalProperties.hashCode());
        return result;
    }

    public String toString() {
        return "BDPhysicalEntityRes(uuid=" + this.getUuid() + ", schema=" + this.getSchema() + ", name=" + this.getName() + ", description=" + this.getDescription() + ", dataSet=" + this.getDataSet() + ", creationDate=" + this.getCreationDate() + ", modificationDate=" + this.getModificationDate() + ", physicalFields=" + this.getPhysicalFields() + ", dataCategories=" + this.getDataCategories() + ", system=" + this.getSystem() + ", isConsentView=" + this.getIsConsentView() + ", isHidden=" + this.getIsHidden() + ", tableType=" + this.getTableType() + ", additionalProperties=" + this.getAdditionalProperties() + ")";
    }
}
