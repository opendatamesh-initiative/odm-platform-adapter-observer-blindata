package org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.states.EventState;

public class Event {
    private EventType eventType;
    private EventState beforeState;
    private EventState afterState;
    private EventStatus eventStatus;
    private JsonNode processingOutput;

    public Event() {
        //DO NOTHING
    }

    public EventState getBeforeState() {
        return beforeState;
    }

    public void setBeforeState(EventState beforeState) {
        this.beforeState = beforeState;
    }

    public EventState getAfterState() {
        return afterState;
    }

    public void setAfterState(EventState afterState) {
        this.afterState = afterState;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public JsonNode getProcessingOutput() {
        return processingOutput;
    }

    public void setProcessingOutput(JsonNode processingOutput) {
        this.processingOutput = processingOutput;
    }

    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }
}
