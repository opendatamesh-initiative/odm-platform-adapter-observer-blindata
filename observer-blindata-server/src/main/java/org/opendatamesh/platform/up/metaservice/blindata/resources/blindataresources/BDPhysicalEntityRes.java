package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
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
    private Set<BDPhysicalFieldRes> physicalFields;

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
    private List<AdditionalPropertiesRes> additionalProperties = new ArrayList<>();

}
