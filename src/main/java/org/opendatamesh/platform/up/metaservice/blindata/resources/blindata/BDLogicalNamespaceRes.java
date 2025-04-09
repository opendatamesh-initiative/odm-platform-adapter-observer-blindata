package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata;

import io.swagger.v3.oas.annotations.media.Schema;

public class BDLogicalNamespaceRes {

    @Schema(description = "The internal resource identifier")
    private String uuid;

    @Schema(description = "The unique name of the resource.")
    private String name;

    @Schema(description = "The display name of this resource")
    private String displayName;

    @Schema(description = "The unique identifier for this resource")
    private String identifier;

    @Schema(description = "The unique prefix for this namespace")
    private String prefix;

    @Schema(description = "The description for this namespace")
    private String description;

    @Schema(description = "If the namespace is predefined, like RDF, RDFS, OWL ... or not")
    private boolean predefined;

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

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPredefined() {
        return predefined;
    }

    public void setPredefined(boolean predefined) {
        this.predefined = predefined;
    }
}
