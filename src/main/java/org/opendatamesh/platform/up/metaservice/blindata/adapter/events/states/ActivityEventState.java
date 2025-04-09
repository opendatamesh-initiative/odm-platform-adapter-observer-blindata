package org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states;

import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.devops.OdmActivityResource;

public class ActivityEventState implements EventState {
    private OdmActivityResource activity;
    private DataProductVersion dataProductVersion;

    public ActivityEventState() {
    }

    public ActivityEventState(OdmActivityResource activity, DataProductVersion dataProductVersion) {
        this.activity = activity;
        this.dataProductVersion = dataProductVersion;
    }

    public OdmActivityResource getActivity() {
        return activity;
    }

    public void setActivity(OdmActivityResource activity) {
        this.activity = activity;
    }

    public DataProductVersion getDataProductVersion() {
        return dataProductVersion;
    }

    public void setDataProductVersion(DataProductVersion dataProductVersion) {
        this.dataProductVersion = dataProductVersion;
    }
}
