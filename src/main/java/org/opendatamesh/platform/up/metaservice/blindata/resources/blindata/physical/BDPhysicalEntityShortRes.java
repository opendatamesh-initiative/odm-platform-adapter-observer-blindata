package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PhysicalEntityShortRes", description = "Projection of a PhysicalEntity")
public class BDPhysicalEntityShortRes {

    @Schema(description = "The PhysicalEntity resource identifier")
    private String uuid;

    @Schema(description = "The PhysicalEntity schema")
    private String schema;

    @Schema(description = "The PhysicalEntity name")
    private String name;

    @Schema(description = "The System this PhysicalEntity belongs to")
    private BDSystemRes system;

    public BDPhysicalEntityShortRes(BDPhysicalEntityRes physicalEntityRes) {
        this.uuid = physicalEntityRes.getUuid();
        this.name = physicalEntityRes.getName();
        this.schema = physicalEntityRes.getSchema();
    }

    public BDPhysicalEntityShortRes() {

    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BDSystemRes getSystem() {
        return system;
    }

    public void setSystem(BDSystemRes system) {
        this.system = system;
    }
}
