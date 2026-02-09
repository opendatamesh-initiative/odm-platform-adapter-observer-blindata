package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1;

public class OdmEventNotificationSearchOptions {

    private OdmEventType eventType;
    private OdmEventNotificationStatus notificationStatus;

    public OdmEventNotificationSearchOptions() {
    }

    public OdmEventType getEventType() {
        return eventType;
    }

    public void setEventType(OdmEventType eventType) {
        this.eventType = eventType;
    }

    public OdmEventNotificationStatus getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(OdmEventNotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
    }
}
