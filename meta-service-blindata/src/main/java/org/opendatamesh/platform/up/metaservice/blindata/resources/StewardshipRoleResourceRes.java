package org.opendatamesh.platform.up.metaservice.blindata.resources;


public class StewardshipRoleResourceRes {
    private org.opendatamesh.platform.up.metaservice.blindata.resources.ResourceType resourceType;

    private boolean canWrite;

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public boolean isCanWrite() {
        return canWrite;
    }

    public void setCanWrite(boolean canWrite) {
        this.canWrite = canWrite;
    }
}
