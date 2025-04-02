package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.eventstates;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.devops.OdmTaskResource;

public class DataProductTaskEventState {
    private OdmTaskResource task;

    public DataProductTaskEventState() {
    }

    public DataProductTaskEventState(OdmTaskResource task) {
        this.task = task;
    }

    public OdmTaskResource getTask() {
        return task;
    }

    public void setTask(OdmTaskResource task) {
        this.task = task;
    }
}
