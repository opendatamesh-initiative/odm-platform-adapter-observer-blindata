package org.opendatamesh.platform.up.metaservice.api.services;

import java.util.List;

import org.opendatamesh.platform.up.metaservice.api.database.entities.Notification;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {
    Notification createNotification(Notification notification);
    List<Notification> readAllNotifications();
    List<Notification> searchNotificationsByEventAndStatus(String eventType, String notificationStatus);
    Notification readOneNotification(Long notificationId);
    void deleteNotification(Long notificationId);
}
