package org.opendatamesh.platform.up.metaservice.blindata.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.opendatamesh.platform.pp.api.resources.v1.dataproduct.DataProductVersionResource;
import org.opendatamesh.platform.pp.api.resources.v1.dataproduct.InfoResource;
import org.opendatamesh.platform.up.metaservice.api.database.entities.Notification;
import org.opendatamesh.platform.up.metaservice.api.database.entities.NotificationStatus;
import org.opendatamesh.platform.up.metaservice.api.database.repositories.NotificationRepository;
import org.opendatamesh.platform.up.metaservice.api.services.NotificationService;
import org.opendatamesh.platform.up.metaservice.blindata.api.BlindataSystemAPIImpl;
import org.opendatamesh.platform.up.metaservice.blindata.entities.AdditionalProperty;
import org.opendatamesh.platform.up.metaservice.blindata.entities.BlindataSystem;
import org.opendatamesh.platform.up.metaservice.blindata.entities.Credentials;
import org.opendatamesh.platform.up.metaservice.blindata.entities.SystemSubtype;
import org.opendatamesh.platform.up.metaservice.blindata.exceptions.MetaServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private RestTemplate restTemplate;

    ObjectMapper objectMapper;

    @Autowired
    private Environment environment;

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Override
    public Notification createNotification(Notification notification) {

        if (!notification.getEvent().getType().equals("DATA_PRODUCT_VERSION_CREATED")) {
            notification.setStatus(NotificationStatus.UNPROCESSABLE);
            notification = notificationRepository.save(notification);
        } else {

            notification.setStatus(NotificationStatus.PROCESSING);
            notification = notificationRepository.save(notification);

            handleDataProductVersionCreatedEvent(notification);
            notification = notificationRepository.save(notification);

        }

        return notification;
    }


    @Override
    public Notification readOneNotification(Long notificationId) {
        Notification notification = null;
        Optional<Notification> findResult = notificationRepository.findById(notificationId);
        if (findResult.isPresent()) {
            notification = findResult.get();
        }
        return notification;
    }

    @Override
    public List<Notification> readAllNotifications() {
        return notificationRepository.findAll();
    }

    // TODO
    @Override
    public List<Notification> searchNotificationsByEventAndStatus(String eventType, String notificationStatus) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    private Notification handleDataProductVersionCreatedEvent(Notification notification)  {
        
        DataProductVersionResource dataProductVersionRes = null;
        try {
            dataProductVersionRes = objectMapper.readValue(notification.getEvent().getAfterState(),
            DataProductVersionResource.class);
        } catch (JsonProcessingException e) {
            notification.setStatus(NotificationStatus.PROCESS_ERROR);
            notification.setProcessingOutput(e.getMessage());
            return notification;
        }
        
        BlindataSystemAPIImpl systemAPI = new BlindataSystemAPIImpl(restTemplate);
        logger.debug("Requested load for " + dataProductVersionRes);

    
        // Definition of: blindata endpoint, credentials and tenant.
        Credentials credentials = new Credentials(
            environment.getProperty("blindata.url"),
            environment.getProperty("blindata.user"),
            environment.getProperty("blindata.password"),
            environment.getProperty("blindata.tenantUUID"));
        
        InfoResource dataProductInfoRes = dataProductVersionRes.getInfo();
        BlindataSystem s1 = new BlindataSystem(dataProductInfoRes.getDisplayName(), dataProductInfoRes.getDescription(),
                SystemSubtype.SERVICE);
        s1.setAdditionalProperties(infoToAdditionalPropertyList(dataProductInfoRes));
        // creation System API invocation
        try {
            s1 = systemAPI.postSystem(s1, credentials);
            if (s1 != null) {
                logger.info("Registered System to Blindata: " + s1);
                notification.setProcessingOutput(s1.toString());
                notification.setStatus(NotificationStatus.PROCESSED);
            } else {
                throw new MetaServiceException("Can't register data product to Blindata");
            }
        } catch (Exception e) {
            logger.warn("Communication error with metaservice for dataproduct with id '" + dataProductVersionRes.getInfo().getDataProductId()
                    + "' : " + e.getMessage());
            notification.setStatus(NotificationStatus.PROCESS_ERROR);
            notification.setProcessingOutput(e.getMessage());
        }

        return notification;
    }

    /* 
    public Notification handleDataProductVersionDeletedEvent(Long notificationId, String fake) {
        BlindataSystemAPIImpl systemAPI = new BlindataSystemAPIImpl(restTemplate);

        // Definition of: blindata endpoint, credentials and tenant.
        Credentials credentials = new Credentials(
                environment.getProperty("blindata.url"),
                environment.getProperty("blindata.user"),
                environment.getProperty("blindata.password"),
                environment.getProperty("blindata.tenantUUID"));

        List<Notification> notifications = notificationRepository.findByDataproductIdOrderByIdDesc(notificationId);

        if (notifications == null && notifications.isEmpty()) {
            logger.warn("There isn't a load referred to data product with id: " + notificationId);
        } else {
            boolean deleted = false;
            for (Notification load : notifications) {
                if (load.getStatus() != null && !load.getStatus().equals(NotificationStatus.FAILED)) {
                    BlindataSystem system = new BlindataSystem();
                    system.setUuid(load.getMetaServiceId());
                    try {
                        systemAPI.deleteSystem(system, credentials);
                        logger.info("Data product with id: " + notificationId + " successfully deleted");
                        deleted = true;
                        break;
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
            }
            if (!deleted)
                logger.warn("Data product with id: " + notificationId + " can't be deleted");
        }
    }
    */

    public List<AdditionalProperty> infoToAdditionalPropertyList(InfoResource dataProductInfoRes) {
        List<AdditionalProperty> additionalProperties = new ArrayList<>();
        additionalProperties.add(new AdditionalProperty("id", dataProductInfoRes.getDataProductId()));
        additionalProperties.add(new AdditionalProperty("fullyQualifiedName", dataProductInfoRes.getFullyQualifiedName()));
        additionalProperties.add(new AdditionalProperty("name", dataProductInfoRes.getName()));
        additionalProperties.add(new AdditionalProperty("version", dataProductInfoRes.getVersionNumber()));
        additionalProperties.add(new AdditionalProperty("domain", dataProductInfoRes.getDomain()));
        additionalProperties.add(new AdditionalProperty("entityType", dataProductInfoRes.getEntityType()));
        additionalProperties.add(new AdditionalProperty("ownerId", dataProductInfoRes.getOwner().getId()));
        additionalProperties.add(new AdditionalProperty("ownerName", dataProductInfoRes.getOwner().getName()));
        return additionalProperties;
    }

}
