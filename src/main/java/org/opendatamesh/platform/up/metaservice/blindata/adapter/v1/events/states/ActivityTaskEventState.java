package org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.states;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.devops.OdmTaskResource;

public class ActivityTaskEventState implements EventState {
    private OdmTaskResource task;

    public ActivityTaskEventState() {
    }

    public ActivityTaskEventState(OdmTaskResource task) {
        this.task = task;
    }

    public OdmTaskResource getTask() {
        return task;
    }

    public void setTask(OdmTaskResource task) {
        this.task = task;
    }

}
