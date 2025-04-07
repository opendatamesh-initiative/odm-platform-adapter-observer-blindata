package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.eventstates;

import com.fasterxml.jackson.databind.JsonNode;

public class OdmDataProductVersionEventState {
    private JsonNode dataProductVersion;

    public OdmDataProductVersionEventState() {
        //DO NOTHING
    }

    public JsonNode getDataProductVersion() {
        return dataProductVersion;
    }

    public void setDataProductVersion(JsonNode dataProductVersion) {
        this.dataProductVersion = dataProductVersion;
    }
}
