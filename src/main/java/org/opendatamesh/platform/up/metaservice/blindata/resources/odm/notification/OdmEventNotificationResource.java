package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification;


import java.sql.Date;

public class OdmEventNotificationResource {
    private Long id;
    private OdmEventResource event;
    private OdmEventNotificationStatus status;
    private String processingOutput = "";
    private OdmObserverResource observer;
    private Date receivedAt;
    private Date processedAt;

    public OdmEventNotificationResource() {
    }

    public OdmEventNotificationResource(OdmEventNotificationResource other) {
        if (other == null) return;

        this.id = other.id;
        this.status = other.status;
        this.processingOutput = other.processingOutput;

        this.receivedAt = (other.receivedAt != null) ? new Date(other.receivedAt.getTime()) : null;
        this.processedAt = (other.processedAt != null) ? new Date(other.processedAt.getTime()) : null;

        this.event = (other.event != null) ? new OdmEventResource(other.event) : null;
        this.observer = (other.observer != null) ? new OdmObserverResource(other.observer) : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OdmEventResource getEvent() {
        return event;
    }

    public void setEvent(OdmEventResource event) {
        this.event = event;
    }

    public OdmEventNotificationStatus getStatus() {
        return status;
    }

    public void setStatus(OdmEventNotificationStatus status) {
        this.status = status;
    }

    public String getProcessingOutput() {
        return processingOutput;
    }

    public void setProcessingOutput(String processingOutput) {
        this.processingOutput = processingOutput;
    }

    public OdmObserverResource getObserver() {
        return observer;
    }

    public void setObserver(OdmObserverResource observer) {
        this.observer = observer;
    }

    public Date getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Date receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Date getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Date processedAt) {
        this.processedAt = processedAt;
    }

    @Override
    public String toString() {
        return "OdmEventNotificationResource{" +
                "id=" + id +
                ", event=" + event +
                ", status=" + status +
                ", processingOutput='" + processingOutput + '\'' +
                ", observer=" + observer +
                ", receivedAt=" + receivedAt +
                ", processedAt=" + processedAt +
                '}';
    }
}
