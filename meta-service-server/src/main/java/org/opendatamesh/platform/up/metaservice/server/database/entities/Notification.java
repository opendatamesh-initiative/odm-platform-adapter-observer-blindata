package org.opendatamesh.platform.up.metaservice.server.database.entities;


import javax.persistence.*;

import org.opendatamesh.platform.up.metaservice.resources.v1.NotificationStatus;

import lombok.Data;

import java.util.Date;

@Data
@Entity(name = "Notification")
@Table( name = "NOTIFICATION", schema="PUBLIC")
public class Notification {

    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="ID")
    private Long id;

    @Embedded
    Event event;

    @Column(name="STATUS")
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Column(name="PROCESSING_OUTPUT")
    private String processingOutput;

    @Column(name="RECEIVED_AT")
    private Date receivedAt;

    @Column(name="PROCESSED_AT")
    private Date processedAt;

    @PrePersist
    protected void onCreate() {
        receivedAt = new Date();
    }
}