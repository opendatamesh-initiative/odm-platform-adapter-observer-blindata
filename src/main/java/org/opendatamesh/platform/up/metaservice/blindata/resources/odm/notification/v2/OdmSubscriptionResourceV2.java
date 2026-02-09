package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2;

public class OdmSubscriptionResourceV2 {
    private String uuid;
    private String name;
    private String displayName;
    private String observerBaseUrl;
    private String observerApiVersion;
    // private List<SubscriptionEventTypeResourceV2> eventTypes;

    public OdmSubscriptionResourceV2() {
    }

    public OdmSubscriptionResourceV2(OdmSubscriptionResourceV2 other) {
        if (other == null) return;

        this.uuid = other.uuid;
        this.name = other.name;
        this.displayName = other.displayName;
        this.observerBaseUrl = other.observerBaseUrl;
        this.observerApiVersion = other.observerApiVersion;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getObserverBaseUrl() {
        return observerBaseUrl;
    }

    public void setObserverBaseUrl(String observerBaseUrl) {
        this.observerBaseUrl = observerBaseUrl;
    }

    public String getObserverApiVersion() {
        return observerApiVersion;
    }

    public void setObserverApiVersion(String observerApiVersion) {
        this.observerApiVersion = observerApiVersion;
    }

    @Override
    public String toString() {
        return "OdmObserverResource{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", observerBaseUrl='" + observerBaseUrl + '\'' +
                ", observerApiVersion='" + observerApiVersion + '\'' +
                '}';
    }
}
