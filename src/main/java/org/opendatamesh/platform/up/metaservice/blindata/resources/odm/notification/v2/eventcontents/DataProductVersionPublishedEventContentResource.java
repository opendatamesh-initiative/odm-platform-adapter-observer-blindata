package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.eventcontents;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.v2.OdmDataProductVersionResourceV2;

public class DataProductVersionPublishedEventContentResource {
    private OdmDataProductVersionResourceV2 dataProductVersion;

    DataProductVersionPublishedEventContentResource() {
        //DO NOTHING
    }

    public OdmDataProductVersionResourceV2 getDataProductVersion() {
        return dataProductVersion;
    }

    public void setDataProductVersion(OdmDataProductVersionResourceV2 dataProductVersion) {
        this.dataProductVersion = dataProductVersion;
    }
}
