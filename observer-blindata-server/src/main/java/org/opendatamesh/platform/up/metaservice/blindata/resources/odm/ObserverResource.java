package org.opendatamesh.platform.up.metaservice.blindata.resources.odm;

public class ObserverResource {
    private Long id;
    private String name;
    private String displayName;
    private String observerServerBaseUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getObserverServerBaseUrl() {
        return observerServerBaseUrl;
    }

    public void setObserverServerBaseUrl(String observerServerBaseUrl) {
        this.observerServerBaseUrl = observerServerBaseUrl;
    }
}
