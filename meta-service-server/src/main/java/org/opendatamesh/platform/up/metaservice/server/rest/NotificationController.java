package org.opendatamesh.platform.up.metaservice.server.rest;

import java.util.List;

import javax.validation.Valid;

import org.opendatamesh.platform.up.metaservice.resources.v1.NotificationResource;
import org.opendatamesh.platform.up.metaservice.server.services.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@Validated
public class NotificationController {

    @Autowired
    private NotificationService notificationService;


    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    public NotificationController() { 
        logger.debug("Notification controller succesfully started");
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResource createNotification(
        @Valid @RequestBody NotificationResource notificationRes
    ) {
        return notificationService.createNotification(notificationRes);
    }


    @GetMapping("/{notificationId}")
    @ResponseStatus(HttpStatus.OK)
    public NotificationResource readOneNotification(
        @Valid @PathVariable(value = "notificationId", required = true) Long notificationId
    ) {
       return notificationService.readOneNotification(notificationId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<NotificationResource> searchNotifications(
        @Valid @RequestParam(required = false, name = "eventType") String eventType,
        @Valid @RequestParam(required = false, name = "notificationStatus") String notificationStatus
        
    ) {
        List<NotificationResource> notificationResources = null;
        if(!StringUtils.hasText(eventType) && !StringUtils.hasText(notificationStatus)) {
            notificationResources = notificationService.readAllNotifications();
        } else {
            notificationResources = notificationService.searchNotificationsByEventAndStatus(eventType, notificationStatus);
        }
       
        return notificationResources;
    }

    @DeleteMapping("/{notificationId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDataProduct(
        @Valid @PathVariable(value = "notificationId", required = true) Long notificationId
    ) {
        notificationService.deleteNotification(notificationId);
    }
}
