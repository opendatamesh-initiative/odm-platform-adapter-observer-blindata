package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    public OdmEventResource(OdmEventResource other) {
        if (other == null) return;

        this.id = other.id;
        this.type = other.type;
        this.entityId = other.entityId;
        this.time = (other.time != null) ? new Date(other.time.getTime()) : null;

        // Deep copy JsonNode using ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.beforeState = (other.beforeState != null) ? mapper.readTree(other.beforeState.toString()) : null;
            this.afterState = (other.afterState != null) ? mapper.readTree(other.afterState.toString()) : null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy JsonNode in OdmEventResource", e);
        }
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

    @Override
    public String toString() {
        return "OdmEventResource{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", entityId='" + entityId + '\'' +
                ", beforeState=" + beforeState +
                ", afterState=" + afterState +
                ", time=" + time +
                '}';
    }
}
