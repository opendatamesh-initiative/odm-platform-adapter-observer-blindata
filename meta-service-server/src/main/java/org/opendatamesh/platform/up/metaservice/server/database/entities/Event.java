package org.opendatamesh.platform.up.metaservice.server.database.entities;


import javax.persistence.*;
import java.util.Date;

@Embeddable
public class Event {

    @Column(name = "event_id")
    private Long id;

    @Column(name = "event_type")
    private String type;

    @Column(name = "event_entity_id")
    private String entityId;

    @Column(name = "event_before_state")
    private String beforeState;

    @Column(name = "event_after_state")
    private String afterState;

    @Column(name = "event_time")
    private Date time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getBeforeState() {
        return beforeState;
    }

    public void setBeforeState(String beforeState) {
        this.beforeState = beforeState;
    }

    public String getAfterState() {
        return afterState;
    }

    public void setAfterState(String afterState) {
        this.afterState = afterState;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
