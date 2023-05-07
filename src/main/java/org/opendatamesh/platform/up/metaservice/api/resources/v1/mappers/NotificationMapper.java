package org.opendatamesh.platform.up.metaservice.api.resources.v1.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.up.metaservice.api.database.entities.Notification;
import org.opendatamesh.platform.up.metaservice.api.resources.v1.NotificationResource;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    Notification toEntity(NotificationResource resource);

    NotificationResource toResource(Notification entity);

    List<Notification> toEntities(List<NotificationResource> resources);

    List<NotificationResource> toResources(List<Notification> entities);

}
