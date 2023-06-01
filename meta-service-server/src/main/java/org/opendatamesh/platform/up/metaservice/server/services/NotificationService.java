package org.opendatamesh.platform.up.metaservice.server.services;

import org.opendatamesh.platform.up.metaservice.blindata.services.BlindataService;
import org.opendatamesh.platform.up.metaservice.resources.v1.NotificationResource;
import org.opendatamesh.platform.up.metaservice.resources.v1.NotificationStatus;
import org.opendatamesh.platform.up.metaservice.server.database.entities.Notification;
import org.opendatamesh.platform.up.metaservice.server.database.repositories.NotificationRepository;
import org.opendatamesh.platform.up.metaservice.server.resources.v1.mappers.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private BlindataService blindataService;

    @Autowired
    private NotificationRepository notificationRepository;

    public NotificationResource createNotification(NotificationResource notificationRes) {
        Notification notification = notificationMapper.toEntity(notificationRes);

        switch (notification.getEvent().getType()){
            case "DATA_PRODUCT_VERSION_DELETED":
                notification.setStatus(NotificationStatus.PROCESSING);
                notificationRepository.save(notification);
                notificationRes = blindataService.handleDataProductDelete(notificationRes);
                notification = notificationMapper.toEntity(notificationRes);
                notification = notificationRepository.save(notification);
                break;
            case "DATA_PRODUCT_UPDATED":
                notification.setStatus(NotificationStatus.PROCESSING);
                notificationRepository.save(notification);
                notificationRes = blindataService.handleDataProductUpdate(notificationRes);
                notification = notificationMapper.toEntity(notificationRes);
                notification = notificationRepository.save(notification);
                break;
            case "DATA_PRODUCT_VERSION_CREATED":
                notification.setStatus(NotificationStatus.PROCESSING);
                notificationRepository.save(notification);
                notificationRes = blindataService.handleDataProductCreated(notificationRes);
                notification = notificationMapper.toEntity(notificationRes);
                notification = notificationRepository.save(notification);
                break;
            default:
                notification.setStatus(NotificationStatus.UNPROCESSABLE);
                notification = notificationRepository.save(notification);
        }
        return notificationMapper.toResource(notification);
    }


    public NotificationResource readOneNotification(Long notificationId) {
        Notification notification = null;
        Optional<Notification> findResult = notificationRepository.findById(notificationId);
        if (findResult.isPresent()) {
            notification = findResult.get();
        }

        return notificationMapper.toResource(notification);
    }


    public List<NotificationResource> readAllNotifications() {
        return notificationMapper.toResources(notificationRepository.findAll());
    }

    // TODO
    public List<NotificationResource> searchNotificationsByEventAndStatus(String eventType, String notificationStatus) {
        throw new UnsupportedOperationException();
    }

    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
