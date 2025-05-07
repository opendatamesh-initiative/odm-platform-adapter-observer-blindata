package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt;

import java.util.List;

public class BDIssueCampaignsSearchOptions {

    private String search;
    private String name;
    private String creatorUuid;
    private List<String> status;

    public BDIssueCampaignsSearchOptions() {
        //DO NOTHING
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getCreatorUuid() {
        return creatorUuid;
    }

    public void setCreatorUuid(String creatorUuid) {
        this.creatorUuid = creatorUuid;
    }

    public List<String> getStatus() {
        return status;
    }

    public void setStatus(List<String> status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
