package org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.states;

import org.opendatamesh.dpds.model.DataProductVersion;

public class DataProductVersionEventState implements EventState {
    private DataProductVersion dataProductVersion;

    public DataProductVersionEventState() {
        //DO NOTHING
    }

    public DataProductVersion getDataProductVersion() {
        return dataProductVersion;
    }

    public void setDataProductVersion(DataProductVersion dataProductVersion) {
        this.dataProductVersion = dataProductVersion;
    }

}
