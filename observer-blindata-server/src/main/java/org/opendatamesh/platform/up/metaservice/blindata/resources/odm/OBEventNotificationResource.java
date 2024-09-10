package org.opendatamesh.platform.up.metaservice.blindata.resources.odm;


import java.util.Date;

public class OBEventNotificationResource {
    private Long id;
    private OBEventResource event;
    private OBEventNotificationStatus status;
    private String processingOutput = "";
    private ObserverResource observer;
    private Date receivedAt;
    private Date processedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OBEventResource getEvent() {
        return event;
    }

    public void setEvent(OBEventResource event) {
        this.event = event;
    }

    public OBEventNotificationStatus getStatus() {
        return status;
    }

    public void setStatus(OBEventNotificationStatus status) {
        this.status = status;
    }

    public String getProcessingOutput() {
        return processingOutput;
    }

    public void setProcessingOutput(String processingOutput) {
        this.processingOutput = processingOutput;
    }

    public ObserverResource getObserver() {
        return observer;
    }

    public void setObserver(ObserverResource observer) {
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
}
