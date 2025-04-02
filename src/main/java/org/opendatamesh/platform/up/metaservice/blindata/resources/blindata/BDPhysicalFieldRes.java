package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BDPhysicalFieldRes {

    @Schema(description = "The PhysicalField resource identifier")
    private String uuid;

    @Schema(description = "The name of the physical field")
    private String name;

    @Schema(description = "The type definition of this PhysicalField")
    private String type;

    @Schema(description = "The ordinal position for this PhysicalField")
    private Integer ordinalPosition;

    @Schema(description = "The description of this PhysicalField")
    private String description;

    @Schema(description = "The date of creation of this entity in the system")
    private Date creationDate;

    @Schema(description = "The date of modification of this entity in the system")
    private Date modificationDate;

    @Schema(description = "List of user defined properties")
    private List<BDAdditionalPropertiesRes> additionalProperties = new ArrayList<>();

    @Schema(description = "A list of associated logical fields with corresponding semantic link path")
    private List<BDLogicalFieldSemanticLinkRes> logicalFields = new ArrayList<>();

    public BDPhysicalFieldRes() {
    }

    public BDPhysicalFieldRes(String uuid, String name, String type, Integer ordinalPosition, String description, Date creationDate, Date modificationDate, List<BDAdditionalPropertiesRes> additionalProperties, List<BDLogicalFieldSemanticLinkRes> logicalFields) {
        this.uuid = uuid;
        this.name = name;
        this.type = type;
        this.ordinalPosition = ordinalPosition;
        this.description = description;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.additionalProperties = additionalProperties;
        this.logicalFields = logicalFields;
    }

    public String getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public Integer getOrdinalPosition() {
        return this.ordinalPosition;
    }

    public String getDescription() {
        return this.description;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public Date getModificationDate() {
        return this.modificationDate;
    }

    public List<BDAdditionalPropertiesRes> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public List<BDLogicalFieldSemanticLinkRes> getLogicalFields() {
        return this.logicalFields;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOrdinalPosition(Integer ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public void setAdditionalProperties(List<BDAdditionalPropertiesRes> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public void setLogicalFields(List<BDLogicalFieldSemanticLinkRes> logicalFields) {
        this.logicalFields = logicalFields;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof BDPhysicalFieldRes)) return false;
        final BDPhysicalFieldRes other = (BDPhysicalFieldRes) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$uuid = this.getUuid();
        final Object other$uuid = other.getUuid();
        if (this$uuid == null ? other$uuid != null : !this$uuid.equals(other$uuid)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$type = this.getType();
        final Object other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
        final Object this$ordinalPosition = this.getOrdinalPosition();
        final Object other$ordinalPosition = other.getOrdinalPosition();
        if (this$ordinalPosition == null ? other$ordinalPosition != null : !this$ordinalPosition.equals(other$ordinalPosition))
            return false;
        final Object this$description = this.getDescription();
        final Object other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description))
            return false;
        final Object this$creationDate = this.getCreationDate();
        final Object other$creationDate = other.getCreationDate();
        if (this$creationDate == null ? other$creationDate != null : !this$creationDate.equals(other$creationDate))
            return false;
        final Object this$modificationDate = this.getModificationDate();
        final Object other$modificationDate = other.getModificationDate();
        if (this$modificationDate == null ? other$modificationDate != null : !this$modificationDate.equals(other$modificationDate))
            return false;
        final Object this$additionalProperties = this.getAdditionalProperties();
        final Object other$additionalProperties = other.getAdditionalProperties();
        if (this$additionalProperties == null ? other$additionalProperties != null : !this$additionalProperties.equals(other$additionalProperties))
            return false;
        final Object this$logicalFields = this.getLogicalFields();
        final Object other$logicalFields = other.getLogicalFields();
        if (this$logicalFields == null ? other$logicalFields != null : !this$logicalFields.equals(other$logicalFields))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BDPhysicalFieldRes;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $uuid = this.getUuid();
        result = result * PRIME + ($uuid == null ? 43 : $uuid.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        final Object $ordinalPosition = this.getOrdinalPosition();
        result = result * PRIME + ($ordinalPosition == null ? 43 : $ordinalPosition.hashCode());
        final Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        final Object $creationDate = this.getCreationDate();
        result = result * PRIME + ($creationDate == null ? 43 : $creationDate.hashCode());
        final Object $modificationDate = this.getModificationDate();
        result = result * PRIME + ($modificationDate == null ? 43 : $modificationDate.hashCode());
        final Object $additionalProperties = this.getAdditionalProperties();
        result = result * PRIME + ($additionalProperties == null ? 43 : $additionalProperties.hashCode());
        final Object $logicalFields = this.getLogicalFields();
        result = result * PRIME + ($logicalFields == null ? 43 : $logicalFields.hashCode());
        return result;
    }

    public String toString() {
        return "BDPhysicalFieldRes(uuid=" + this.getUuid() + ", name=" + this.getName() + ", type=" + this.getType() + ", ordinalPosition=" + this.getOrdinalPosition() + ", description=" + this.getDescription() + ", creationDate=" + this.getCreationDate() + ", modificationDate=" + this.getModificationDate() + ", additionalProperties=" + this.getAdditionalProperties() + ", logicalFields=" + this.getLogicalFields() + ")";
    }
}
