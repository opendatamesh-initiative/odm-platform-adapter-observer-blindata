package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.eventstates;

import org.opendatamesh.dpds.model.DataProductVersionDPDS;

public class DataProductVersionEventState {
    private DataProductVersionDPDS dataProductVersion;

    public DataProductVersionEventState() {
    }

    public DataProductVersionEventState(DataProductVersionDPDS dataProductVersion) {
        this.dataProductVersion = dataProductVersion;
    }

    public DataProductVersionDPDS getDataProductVersion() {
        return dataProductVersion;
    }

    public void setDataProductVersion(DataProductVersionDPDS dataProductVersion) {
        this.dataProductVersion = dataProductVersion;
    }
}
