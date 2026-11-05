package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.eventcontents;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.devops.v2.OdmActivityResourceV2;

public class ActivityEventContentResource {
    private OdmActivityResourceV2 activity;

    ActivityEventContentResource() {
        //DO NOTHING
    }

    public OdmActivityResourceV2 getActivity() {
        return activity;
    }

    public void setActivity(OdmActivityResourceV2 activity) {
        this.activity = activity;
    }
}
