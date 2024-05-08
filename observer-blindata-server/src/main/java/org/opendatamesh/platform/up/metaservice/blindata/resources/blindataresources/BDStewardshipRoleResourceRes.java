package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;


public class BDStewardshipRoleResourceRes {
    private BDResourceType resourceType;

    private boolean canWrite;

    public BDResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(BDResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public boolean isCanWrite() {
        return canWrite;
    }

    public void setCanWrite(boolean canWrite) {
        this.canWrite = canWrite;
    }
}
