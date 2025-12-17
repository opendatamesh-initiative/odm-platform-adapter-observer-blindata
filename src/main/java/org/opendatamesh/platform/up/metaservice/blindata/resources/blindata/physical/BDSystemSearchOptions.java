package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical;

public class BDSystemSearchOptions {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BDSystemSearchOptions(String search) {
        this.name = search;
    }
}