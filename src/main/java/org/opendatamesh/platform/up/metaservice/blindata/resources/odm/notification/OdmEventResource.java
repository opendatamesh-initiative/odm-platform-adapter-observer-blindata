package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Date;

public class OdmEventResource {
    private Long id;
    private String type;
    private String entityId;
    private JsonNode beforeState;
    private JsonNode afterState;
    private Date time;

    public OdmEventResource() {
    }

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

    public JsonNode getBeforeState() {
        return beforeState;
    }

    public void setBeforeState(JsonNode beforeState) {
        this.beforeState = beforeState;
    }

    public JsonNode getAfterState() {
        return afterState;
    }

    public void setAfterState(JsonNode afterState) {
        this.afterState = afterState;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
