package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.eventstates;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.devops.OdmActivityResource;

public class OdmDataProductActivityEventState {
    private OdmActivityResource activity;
    private JsonNode dataProductVersion;

    public OdmDataProductActivityEventState() {
    }

    public OdmDataProductActivityEventState(OdmActivityResource activity, JsonNode dataProductVersion) {
        this.activity = activity;
        this.dataProductVersion = dataProductVersion;
    }

    public OdmActivityResource getActivity() {
        return activity;
    }

    public void setActivity(OdmActivityResource activity) {
        this.activity = activity;
    }

    public JsonNode getDataProductVersion() {
        return dataProductVersion;
    }

    public void setDataProductVersion(JsonNode dataProductVersion) {
        this.dataProductVersion = dataProductVersion;
    }
}
