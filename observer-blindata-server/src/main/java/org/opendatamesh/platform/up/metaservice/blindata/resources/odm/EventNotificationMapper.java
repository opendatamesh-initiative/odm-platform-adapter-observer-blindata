package org.opendatamesh.platform.up.metaservice.blindata.resources.odm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.springframework.util.StringUtils;

@Mapper(componentModel = "spring")
public interface EventNotificationMapper {

    @Mapping(source = "event.beforeState", target = "event.beforeState", qualifiedByName = "jsonNodeToString")
    @Mapping(source = "event.afterState", target = "event.afterState", qualifiedByName = "jsonNodeToString")
    EventNotificationResource toPlatformResource(OBEventNotificationResource observerResource);

    @Mapping(source = "event.beforeState", target = "event.beforeState", qualifiedByName = "stringToJsonNode")
    @Mapping(source = "event.afterState", target = "event.afterState", qualifiedByName = "stringToJsonNode")
    OBEventNotificationResource toObserverResource(EventNotificationResource platformResource);

    @Named("stringToJsonNode")
    static JsonNode stringToJsonNode(String value) {
        try {
            if (!StringUtils.hasText(value)) {
                return null;
            }
            return new ObjectMapper().readTree(value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert String to JsonNode", e);
        }
    }

    @Named("jsonNodeToString")
    static String jsonNodeToString(JsonNode jsonNode) {
        try {
            if (jsonNode == null) {
                return null;
            }
            return new ObjectMapper().writeValueAsString(jsonNode);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JsonNode to String", e);
        }
    }
}