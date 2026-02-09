package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2;


public class OdmEventNotificationResourceV2 {
    private Long sequenceId;
    private OdmEventNotificationStatusV2 status;
    private OdmEventResourceV2 event;
    private OdmSubscriptionResourceV2 subscription;
    private String errorMessage;

    public OdmEventNotificationResourceV2() {
    }

    public OdmEventNotificationResourceV2(OdmEventNotificationResourceV2 other) {
        if (other == null) return;

        this.sequenceId = other.sequenceId;
        this.status = other.status;

        this.event = (other.event != null) ? new OdmEventResourceV2(other.event) : null;
        this.subscription = (other.subscription != null) ? new OdmSubscriptionResourceV2(other.subscription) : null;
    }

    public Long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(Long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public OdmEventResourceV2 getEvent() {
        return event;
    }

    public void setEvent(OdmEventResourceV2 event) {
        this.event = event;
    }

    public OdmEventNotificationStatusV2 getStatus() {
        return status;
    }

    public void setStatus(OdmEventNotificationStatusV2 status) {
        this.status = status;
    }

    public OdmSubscriptionResourceV2 getSubscription() {
        return subscription;
    }

    public void setSubscription(OdmSubscriptionResourceV2 subscription) {
        this.subscription = subscription;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "OdmEventNotificationResource{" +
                "sequenceId=" + sequenceId +
                ", status=" + status +
                ", event=" + event +
                ", subscription=" + subscription.toString() +
                ", errorMessage=" + errorMessage +
                '}';
    }
}
