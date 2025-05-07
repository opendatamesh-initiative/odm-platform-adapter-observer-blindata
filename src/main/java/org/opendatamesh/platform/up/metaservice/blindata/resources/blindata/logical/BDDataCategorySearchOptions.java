package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical;

import java.util.List;

public class BDDataCategorySearchOptions {

    private String search;

    private List<String> namespaceUuid;

    public List<String> getNamespaceUuid() {
        return namespaceUuid;
    }

    public void setNamespaceUuid(List<String> namespaceUuid) {
        this.namespaceUuid = namespaceUuid;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
