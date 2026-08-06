package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes;

public class QualityProbesDefinitionSearchOptions {
    private String search;
    private String projectUuid;
    private Boolean lastVersion = Boolean.TRUE;

    public QualityProbesDefinitionSearchOptions() {
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

    public Boolean getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(Boolean lastVersion) {
        this.lastVersion = lastVersion;
    }
}
