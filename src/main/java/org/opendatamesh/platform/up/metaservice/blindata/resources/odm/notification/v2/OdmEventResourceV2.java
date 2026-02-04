package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;

public class OdmEventResourceV2 {
    private Long sequenceId;
    private String resourceType;
    private String resourceIdentifier;
    private String type;
    private String eventTypeVersion;
    private JsonNode eventContent; // TODO: or String?
    private Date time;

    public OdmEventResourceV2() {
    }

    public OdmEventResourceV2(OdmEventResourceV2 other) {
        if (other == null) return;

        this.sequenceId = other.sequenceId;
        this.resourceType = other.resourceType;
        this.resourceIdentifier = other.resourceIdentifier;
        this.type = other.type;
        this.eventTypeVersion = other.eventTypeVersion;
        this.time = (other.time != null) ? new Date(other.time.getTime()) : null;

        // Deep copy JsonNode using ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.eventContent = (other.eventContent != null) ? mapper.readTree(other.eventContent.toString()) : null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy JsonNode in OdmEventResourceV2", e);
        }
    }

    public Long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(Long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceIdentifier() {
        return resourceIdentifier;
    }

    public void setResourceIdentifier(String resourceIdentifier) {
        this.resourceIdentifier = resourceIdentifier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEventTypeVersion() {
        return eventTypeVersion;
    }

    public void setEventTypeVersion(String eventTypeVersion) {
        this.eventTypeVersion = eventTypeVersion;
    }

    public JsonNode getEventContent() {
        return eventContent;
    }

    public void setEventContent(JsonNode eventContent) {
        this.eventContent = eventContent;
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
                "sequenceId=" + sequenceId +
                ", resourceType='" + resourceType + '\'' +
                ", resourceIdentifier='" + resourceIdentifier + '\'' +
                ", eventTypeVersion='" + eventTypeVersion + '\'' +
                ", type='" + type + '\'' +
                ", eventContent=" + eventContent +
                ", time=" + time +
                '}';
    }
}
