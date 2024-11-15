package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.eventstates;

import org.opendatamesh.platform.up.executor.api.resources.TaskResource;

public class DataProductTaskEventState {
    private TaskResource task;

    public DataProductTaskEventState() {
    }

    public DataProductTaskEventState(TaskResource task) {
        this.task = task;
    }

    public TaskResource getTask() {
        return task;
    }

    public void setTask(TaskResource task) {
        this.task = task;
    }
}
