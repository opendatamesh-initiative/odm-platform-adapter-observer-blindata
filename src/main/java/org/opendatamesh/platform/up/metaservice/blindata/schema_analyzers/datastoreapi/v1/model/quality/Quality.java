package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1.model.quality;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.dpds.model.core.ComponentBase;

public class Quality extends ComponentBase {
    /**
     * Stable rule id (ODCS); when present, odcs31 mapping uses this for the short segment of the Quality Check code.
     */
    private String id;
    private String name;
    private String description;
    private String dimension;
    private String unit;
    private String type;
    private String engine;
    /** ODCS library metric name (see ODCS 3.1 data-quality). */
    private String metric;
    private JsonNode arguments;
    private String query;
    /** Custom rule body; ODCS allows string or structured content. */
    private JsonNode implementation;
    /**
     * ODCS comparison operators are separate keys (not a single {@code operator} field); see
     * <a href="https://bitol-io.github.io/open-data-contract-standard/v3.1.0/data-quality/">ODCS Data Quality</a>.
     */
    private Float mustBe;
    private Float mustNotBe;
    private Float mustBeGreaterThan;
    private Float mustBeGreaterOrEqualTo;
    private Float mustBeLessThan;
    private Float mustBeLessOrEqualTo;
    private JsonNode mustBeBetween;
    private JsonNode mustNotBeBetween;
    private QualityCustomProperty customProperties;

    public Quality() {
        //DO NOTHING
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public JsonNode getArguments() {
        return arguments;
    }

    public void setArguments(JsonNode arguments) {
        this.arguments = arguments;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public JsonNode getImplementation() {
        return implementation;
    }

    public void setImplementation(JsonNode implementation) {
        this.implementation = implementation;
    }

    public Float getMustBe() {
        return mustBe;
    }

    public void setMustBe(Float mustBe) {
        this.mustBe = mustBe;
    }

    public Float getMustNotBe() {
        return mustNotBe;
    }

    public void setMustNotBe(Float mustNotBe) {
        this.mustNotBe = mustNotBe;
    }

    public Float getMustBeGreaterThan() {
        return mustBeGreaterThan;
    }

    public void setMustBeGreaterThan(Float mustBeGreaterThan) {
        this.mustBeGreaterThan = mustBeGreaterThan;
    }

    public Float getMustBeGreaterOrEqualTo() {
        return mustBeGreaterOrEqualTo;
    }

    public void setMustBeGreaterOrEqualTo(Float mustBeGreaterOrEqualTo) {
        this.mustBeGreaterOrEqualTo = mustBeGreaterOrEqualTo;
    }

    public Float getMustBeLessThan() {
        return mustBeLessThan;
    }

    public void setMustBeLessThan(Float mustBeLessThan) {
        this.mustBeLessThan = mustBeLessThan;
    }

    public Float getMustBeLessOrEqualTo() {
        return mustBeLessOrEqualTo;
    }

    public void setMustBeLessOrEqualTo(Float mustBeLessOrEqualTo) {
        this.mustBeLessOrEqualTo = mustBeLessOrEqualTo;
    }

    public JsonNode getMustBeBetween() {
        return mustBeBetween;
    }

    public void setMustBeBetween(JsonNode mustBeBetween) {
        this.mustBeBetween = mustBeBetween;
    }

    public JsonNode getMustNotBeBetween() {
        return mustNotBeBetween;
    }

    public void setMustNotBeBetween(JsonNode mustNotBeBetween) {
        this.mustNotBeBetween = mustNotBeBetween;
    }

    public QualityCustomProperty getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(QualityCustomProperty customProperties) {
        this.customProperties = customProperties;
    }

    @Override
    public String toString() {
        return "Quality{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", dimension='" + dimension + '\'' +
                ", unit='" + unit + '\'' +
                ", type='" + type + '\'' +
                ", engine='" + engine + '\'' +
                ", metric='" + metric + '\'' +
                ", arguments=" + arguments +
                ", query='" + query + '\'' +
                ", implementation=" + implementation +
                ", mustBe=" + mustBe +
                ", mustNotBe=" + mustNotBe +
                ", mustBeGreaterThan=" + mustBeGreaterThan +
                ", mustBeGreaterOrEqualTo=" + mustBeGreaterOrEqualTo +
                ", mustBeLessThan=" + mustBeLessThan +
                ", mustBeLessOrEqualTo=" + mustBeLessOrEqualTo +
                ", mustBeBetween=" + mustBeBetween +
                ", mustNotBeBetween=" + mustNotBeBetween +
                ", customProperties=" + customProperties +
                '}';
    }
}
