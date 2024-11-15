package org.opendatamesh.platform.up.metaservice.blindata.resources.odm;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;

@Mapper(componentModel = "spring")
public interface EventNotificationMapper {

    EventNotificationResource toPlatformResource(OBEventNotificationResource observerResource);

    OBEventNotificationResource toObserverResource(EventNotificationResource platformResource);
}