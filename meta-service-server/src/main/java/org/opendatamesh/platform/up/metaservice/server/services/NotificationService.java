package org.opendatamesh.platform.up.metaservice.server.services;

import org.opendatamesh.platform.up.metaservice.blindata.services.BlindataService;
import org.opendatamesh.platform.up.metaservice.resources.v1.NotificationResource;
import org.opendatamesh.platform.up.metaservice.resources.v1.NotificationStatus;
import org.opendatamesh.platform.up.metaservice.server.database.entities.Notification;
import org.opendatamesh.platform.up.metaservice.server.database.repositories.NotificationRepository;
import org.opendatamesh.platform.up.metaservice.server.resources.v1.mappers.NotificationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public NotificationResource createNotification(NotificationResource notificationRes) {


        Notification notification = notificationMapper.toEntity(notificationRes);

        if (!notificationRes.getEvent().getType().equals("DATA_PRODUCT_VERSION_CREATED")) {
            notification.setStatus(NotificationStatus.UNPROCESSABLE);
            notification = notificationRepository.save(notification);
        } else {
            notification.setStatus(NotificationStatus.PROCESSING);
            notification = notificationRepository.save(notification);

            blindataService.handleDataProductVersionCreatedEvent(notificationRes);
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
