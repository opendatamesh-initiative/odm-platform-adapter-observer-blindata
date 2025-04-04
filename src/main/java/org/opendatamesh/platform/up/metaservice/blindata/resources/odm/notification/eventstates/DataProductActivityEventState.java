package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.eventstates;

import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.devops.OdmActivityResource;

public class DataProductActivityEventState {
    private OdmActivityResource activity;
    private DataProductVersionDPDS dataProductVersion;

    public DataProductActivityEventState() {
    }

    public DataProductActivityEventState(OdmActivityResource activity, DataProductVersionDPDS dataProductVersion) {
        this.activity = activity;
        this.dataProductVersion = dataProductVersion;
    }

    public OdmActivityResource getActivity() {
        return activity;
    }

    public void setActivity(OdmActivityResource activity) {
        this.activity = activity;
    }

    public DataProductVersionDPDS getDataProductVersion() {
        return dataProductVersion;
    }

    public void setDataProductVersion(DataProductVersionDPDS dataProductVersion) {
        this.dataProductVersion = dataProductVersion;
    }
}
