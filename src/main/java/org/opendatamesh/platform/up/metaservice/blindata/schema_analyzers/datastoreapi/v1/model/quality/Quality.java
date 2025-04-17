package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1.model.quality;

import org.opendatamesh.dpds.model.core.ComponentBase;

public class Quality extends ComponentBase {
    private String name;
    private String description;
    private String dimension;
    private String unit;
    private String type;
    private String engine;
    private Float mustBeGreaterOrEqualTo;
    private Float mustBeLessOrEqualTo;
    private Float mustBe;
    private QualityCustomProperty customProperties;

    public Quality() {
        //DO NOTHING
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public Float getMustBeGreaterOrEqualTo() {
        return mustBeGreaterOrEqualTo;
    }

    public void setMustBeGreaterOrEqualTo(Float mustBeGreaterOrEqualTo) {
        this.mustBeGreaterOrEqualTo = mustBeGreaterOrEqualTo;
    }

    public Float getMustBeLessOrEqualTo() {
        return mustBeLessOrEqualTo;
    }

    public void setMustBeLessOrEqualTo(Float mustBeLessOrEqualTo) {
        this.mustBeLessOrEqualTo = mustBeLessOrEqualTo;
    }

    public Float getMustBe() {
        return mustBe;
    }

    public void setMustBe(Float mustBe) {
        this.mustBe = mustBe;
    }

    public QualityCustomProperty getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(QualityCustomProperty customProperties) {
        this.customProperties = customProperties;
    }
}
