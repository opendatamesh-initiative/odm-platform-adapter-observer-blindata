package org.opendatamesh.platform.up.metaservice.blindata.adapter.v2.events;

import com.fasterxml.jackson.databind.JsonNode;

public class EventV2 {
    private EventTypeV2 eventType;
    private String resourceType;
    private String resourceIdentifier;
    private String eventTypeVersion;
    private EventStatusV2 eventStatus;
    private JsonNode eventContent;
    private String errorMessage;

    public EventV2() {
        //DO NOTHING
    }

    public EventTypeV2 getEventType() {
        return eventType;
    }

    public void setEventType(EventTypeV2 eventType) {
        this.eventType = eventType;
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

    public String getEventTypeVersion() {
        return eventTypeVersion;
    }

    public void setEventTypeVersion(String eventTypeVersion) {
        this.eventTypeVersion = eventTypeVersion;
    }

    public EventStatusV2 getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(EventStatusV2 eventStatus) {
        this.eventStatus = eventStatus;
    }

    public JsonNode getEventContent() {
        return eventContent;
    }

    public void setEventContent(JsonNode eventContent) {
        this.eventContent = eventContent;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
