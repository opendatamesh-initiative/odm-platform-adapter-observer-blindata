package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

public class BDUserSearchOptions {
    private String tenantUuid;
    private String search;

    public String getTenantUuid() {
        return tenantUuid;
    }

    public void setTenantUuid(String tenantUuid) {
        this.tenantUuid = tenantUuid;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
