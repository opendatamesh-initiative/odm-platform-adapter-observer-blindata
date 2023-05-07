package org.opendatamesh.platform.up.metaservice.server.resources.v1.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.up.metaservice.resources.v1.NotificationResource;
import org.opendatamesh.platform.up.metaservice.server.database.entities.Notification;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    Notification toEntity(NotificationResource resource);

    NotificationResource toResource(Notification entity);

    List<Notification> toEntities(List<NotificationResource> resources);

    List<NotificationResource> toResources(List<Notification> entities);

}
