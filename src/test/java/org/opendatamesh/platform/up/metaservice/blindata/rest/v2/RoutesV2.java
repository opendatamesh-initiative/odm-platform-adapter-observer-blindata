package org.opendatamesh.platform.up.metaservice.blindata.rest.v2;

public enum RoutesV2 {
    CONSUME("/api/v2/up/observer/notifications");

    private final String path;

    RoutesV2(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
