package org.opendatamesh.platform.up.metaservice.blindata.rest.v1;

public enum RoutesV1 {
    CONSUME("/api/v1/up/observer/notifications");

    private final String path;

    RoutesV1(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
