package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality;

import java.util.Set;

public class QualitySuitesSearchOptions {
    private String search;
    private Boolean isPublished;
    private Set<String> teamsCodes;

    public QualitySuitesSearchOptions() {
        //DO NOTHING
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public Boolean getPublished() {
        return isPublished;
    }

    public void setPublished(Boolean published) {
        isPublished = published;
    }

    public Set<String> getTeamsCodes() {
        return teamsCodes;
    }

    public void setTeamsCodes(Set<String> teamsCodes) {
        this.teamsCodes = teamsCodes;
    }
}
