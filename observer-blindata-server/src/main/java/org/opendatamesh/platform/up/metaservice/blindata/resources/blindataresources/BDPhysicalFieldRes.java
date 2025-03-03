package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
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
    private List<AdditionalPropertiesRes> additionalProperties = new ArrayList<>();

    @Schema(description = "A list of associated logical fields with corresponding semantic link path")
    private List<LogicalFieldSemanticLinkRes> logicalFields = new ArrayList<>();

    public BDPhysicalFieldRes() {
    }

    public BDPhysicalFieldRes(String uuid, String name, String type, Integer ordinalPosition, String description, Date creationDate, Date modificationDate, List<AdditionalPropertiesRes> additionalProperties, List<LogicalFieldSemanticLinkRes> logicalFields) {
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
}
