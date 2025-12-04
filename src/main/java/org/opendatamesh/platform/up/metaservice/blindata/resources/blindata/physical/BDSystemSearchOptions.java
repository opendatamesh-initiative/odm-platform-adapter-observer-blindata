package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical;

import io.swagger.v3.oas.annotations.Parameter;

public class BDSystemSearchOptions {

    @Parameter(description = "Search by name")
    private String search;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public BDSystemSearchOptions(String search) {
        this.search = search;
    }
}
