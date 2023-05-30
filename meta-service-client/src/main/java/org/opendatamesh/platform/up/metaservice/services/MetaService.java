package org.opendatamesh.platform.up.metaservice.services;

import org.opendatamesh.platform.up.metaservice.resources.v1.NotificationResource;
import org.springframework.stereotype.Service;

@Service
public interface MetaService {

    public NotificationResource handleDataProductCreated(NotificationResource notificationRes) throws MetaServiceException;

    public NotificationResource handleDataProductUpdate(NotificationResource notificationResource) throws MetaServiceException;

    public NotificationResource handleDataProductDelete(NotificationResource notificationRes) throws MetaServiceException;
    
}
