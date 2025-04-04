package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

public class BDSystemRes {

    @Schema(description = "The name of the system")
    private String name;

    @Schema(description = "the description of the system")
    private String description;


    @Schema(description = "List of user defined properties")
    private List<BDAdditionalPropertiesRes> additionalProperties = new ArrayList<>();

    @Schema(description = "The system's technology")
    private String technology;

    public BDSystemRes() {
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public List<BDAdditionalPropertiesRes> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public String getTechnology() {
        return this.technology;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAdditionalProperties(List<BDAdditionalPropertiesRes> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof BDSystemRes)) return false;
        final BDSystemRes other = (BDSystemRes) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$description = this.getDescription();
        final Object other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description))
            return false;
        final Object this$additionalProperties = this.getAdditionalProperties();
        final Object other$additionalProperties = other.getAdditionalProperties();
        if (this$additionalProperties == null ? other$additionalProperties != null : !this$additionalProperties.equals(other$additionalProperties))
            return false;
        final Object this$technology = this.getTechnology();
        final Object other$technology = other.getTechnology();
        if (this$technology == null ? other$technology != null : !this$technology.equals(other$technology))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BDSystemRes;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        final Object $additionalProperties = this.getAdditionalProperties();
        result = result * PRIME + ($additionalProperties == null ? 43 : $additionalProperties.hashCode());
        final Object $technology = this.getTechnology();
        result = result * PRIME + ($technology == null ? 43 : $technology.hashCode());
        return result;
    }

    public String toString() {
        return "BDSystemRes(name=" + this.getName() + ", description=" + this.getDescription() + ", additionalProperties=" + this.getAdditionalProperties() + ", technology=" + this.getTechnology() + ")";
    }
}
