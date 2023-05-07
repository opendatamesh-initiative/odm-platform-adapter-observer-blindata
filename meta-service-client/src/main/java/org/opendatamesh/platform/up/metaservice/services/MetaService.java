package org.opendatamesh.platform.up.metaservice.services;

import org.opendatamesh.platform.up.metaservice.resources.v1.NotificationResource;
import org.springframework.stereotype.Service;

@Service
public interface MetaService {

    public NotificationResource handleDataProductVersionCreatedEvent(NotificationResource notificationRes);
    
}
