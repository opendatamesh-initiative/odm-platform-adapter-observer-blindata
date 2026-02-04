package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.events;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.v2.OdmDataProductVersionResourceV2;

public class DataProductVersionPublicationRequestedEventContentResource {
    private OdmDataProductVersionResourceV2 dataProductVersion;
    private OdmDataProductVersionResourceV2 previousDataProductVersion;

    DataProductVersionPublicationRequestedEventContentResource() {
        //DO NOTHING
    }

    public OdmDataProductVersionResourceV2 getDataProductVersion() {
        return dataProductVersion;
    }
    
    public void setDataProductVersion(OdmDataProductVersionResourceV2 dataProductVersion) {
        this.dataProductVersion = dataProductVersion;
    }

    public OdmDataProductVersionResourceV2 getPreviousDataProductVersion() {
        return previousDataProductVersion;
    }
    
    public void setPreviousDataProductVersion(OdmDataProductVersionResourceV2 previousDataProductVersion) {
        this.previousDataProductVersion = previousDataProductVersion;
    }
}
