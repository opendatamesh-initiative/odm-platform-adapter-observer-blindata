package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.probes_upload;

import java.util.Map;

public class PhysicalBinding {
    private String schema;
    private String object;
    private String property;

    public PhysicalBinding() {
        //DO NOTHING
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
