package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2;

import java.util.List;


public class OdmObserverSubscribeResponseResourceV2 {
    private OdmObserverSubscribeResourceV2 subscription;

    public OdmObserverSubscribeResponseResourceV2() {
        //DO NOTHING
    }

    public OdmObserverSubscribeResourceV2 getSubscription() {
        return subscription;
    }

    public void setSubscription(OdmObserverSubscribeResourceV2 subscription) {
        this.subscription = subscription;
    }

    @Override
    public String toString() {
        return "OdmObserverSubscribeResponseResourceV2{" +
                "subscription=" + subscription.toString() +
                '}';
    }

    public static class OdmObserverSubscribeResourceV2 {
        String name;
        String displayName;
        String observerBaseUrl;
        String observerApiVersion;
        List<String> eventTypes;

        public OdmObserverSubscribeResourceV2() {
        }

        public OdmObserverSubscribeResourceV2(OdmObserverSubscribeResourceV2 other) {
            if (other == null) return;

            this.name = other.name;
            this.displayName = other.displayName;
            this.observerBaseUrl = other.observerBaseUrl;
            this.observerApiVersion = other.observerApiVersion;
            this.eventTypes = other.eventTypes;
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

        public List<String> getEventTypes() {
            return eventTypes;
        }

        public void setEventTypes(List<String> eventTypes) {
            this.eventTypes = eventTypes;
        }

        @Override
        public String toString() {
            return "OdmObserverSubscribeResourceV2{" +
                    "name='" + name + '\'' +
                    ", displayName='" + displayName + '\'' +
                    ", observerBaseUrl='" + observerBaseUrl + '\'' +
                    ", observerApiVersion='" + observerApiVersion + '\'' +
                    ", eventTypes=" + eventTypes +
                    '}';
        }
    }
}
