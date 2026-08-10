package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes;

public class QualityProbesTagSearchOptions {
    private String search;
    private String projectUuid;

    public QualityProbesTagSearchOptions() {
        //DO NOTHING
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getProjectUuid() {
        return projectUuid;
    }

    public void setProjectUuid(String projectUuid) {
        this.projectUuid = projectUuid;
    }
}
