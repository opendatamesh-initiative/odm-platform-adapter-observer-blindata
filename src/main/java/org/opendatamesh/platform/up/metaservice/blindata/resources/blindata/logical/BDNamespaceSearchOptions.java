package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical;

import java.util.List;

public class BDNamespaceSearchOptions {

    private List<String> identifiers;

    private String prefix;

    public List<String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
