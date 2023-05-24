package org.opendatamesh.platform.up.metaservice.server.database.entities;


import org.opendatamesh.platform.up.metaservice.resources.v1.NotificationStatus;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "notification", schema = "PUBLIC")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sequence_id")
    private Long id;

    @Embedded
    private Event event;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Column(name = "processing_output")
    private String processingOutput;

    @Column(name = "received_at")
    private Date receivedAt;

    @Column(name = "processed_at")
    private Date processedAt;

    @PrePersist
    protected void onCreate() {
        receivedAt = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public String getProcessingOutput() {
        return processingOutput;
    }

    public void setProcessingOutput(String processingOutput) {
        this.processingOutput = processingOutput;
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