package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification;

public class OdmObserverResource {
    private Long id;
    private String name;
    private String displayName;
    private String observerServerBaseUrl;

    public OdmObserverResource() {
    }

    public OdmObserverResource(OdmObserverResource other) {
        if (other == null) return;

        this.id = other.id;
        this.name = other.name;
        this.displayName = other.displayName;
        this.observerServerBaseUrl = other.observerServerBaseUrl;
    }

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

    @Override
    public String toString() {
        return "OdmObserverResource{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", observerServerBaseUrl='" + observerServerBaseUrl + '\'' +
                '}';
    }
}
