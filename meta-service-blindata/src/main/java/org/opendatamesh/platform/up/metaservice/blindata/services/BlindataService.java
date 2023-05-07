package org.opendatamesh.platform.up.metaservice.blindata.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.opendatamesh.platform.pp.api.resources.v1.dataproduct.DataProductVersionResource;
import org.opendatamesh.platform.pp.api.resources.v1.dataproduct.InfoResource;
import org.opendatamesh.platform.up.metaservice.blindata.client.BlindataClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.BlindataCredentials;
import org.opendatamesh.platform.up.metaservice.blindata.resources.AdditionalPropertyResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.SystemResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.SystemSubtype;
import org.opendatamesh.platform.up.metaservice.resources.v1.NotificationResource;
import org.opendatamesh.platform.up.metaservice.resources.v1.NotificationStatus;
import org.opendatamesh.platform.up.metaservice.services.MetaService;
import org.opendatamesh.platform.up.metaservice.services.MetaServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class BlindataService implements MetaService  {

    @Autowired
    private Environment environment;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(BlindataService.class);

    @Override
    public NotificationResource handleDataProductVersionCreatedEvent(NotificationResource notificationRes)  {
        
        DataProductVersionResource dataProductVersionRes = null;
        try {
            dataProductVersionRes = objectMapper.readValue(notificationRes.getEvent().getAfterState(),
            DataProductVersionResource.class);
        } catch (JsonProcessingException e) {
            notificationRes.setStatus(NotificationStatus.PROCESS_ERROR);
            notificationRes.setProcessingOutput(e.getMessage());
            return notificationRes;
        }
        
        BlindataClient blindataClient = new BlindataClient(restTemplate);
        logger.debug("Requested load for " + dataProductVersionRes);

    
        // Definition of: blindata endpoint, credentials and tenant.
        BlindataCredentials credentials = new BlindataCredentials(
            environment.getProperty("blindata.url"),
            environment.getProperty("blindata.user"),
            environment.getProperty("blindata.password"),
            environment.getProperty("blindata.tenantUUID"));
        
        InfoResource dataProductInfoRes = dataProductVersionRes.getInfo();
        SystemResource systemRes = new SystemResource(dataProductInfoRes.getDisplayName(), dataProductInfoRes.getDescription(),
                SystemSubtype.SERVICE);
        systemRes.setAdditionalProperties(infoToAdditionalPropertyList(dataProductInfoRes));
        // creation System API invocation
        try {
            systemRes = blindataClient.createSystem(systemRes, credentials);
            if (systemRes != null) {
                logger.info("Registered System to Blindata: " + systemRes);
                notificationRes.setProcessingOutput(systemRes.toString());
                notificationRes.setStatus(NotificationStatus.PROCESSED);
            } else {
                throw new MetaServiceException("Can't register data product to Blindata");
            }
        } catch (Exception e) {
            logger.warn("Communication error with metaservice for dataproduct with id '" + dataProductVersionRes.getInfo().getDataProductId()
                    + "' : " + e.getMessage());
            notificationRes.setStatus(NotificationStatus.PROCESS_ERROR);
            notificationRes.setProcessingOutput(e.getMessage());
        }

        return notificationRes;
    }
    

    public List<AdditionalPropertyResource> infoToAdditionalPropertyList(InfoResource dataProductInfoRes) {
        List<AdditionalPropertyResource> additionalPropertiesRes = new ArrayList<>();
        additionalPropertiesRes.add(new AdditionalPropertyResource("id", dataProductInfoRes.getDataProductId()));
        additionalPropertiesRes.add(new AdditionalPropertyResource("fullyQualifiedName", dataProductInfoRes.getFullyQualifiedName()));
        additionalPropertiesRes.add(new AdditionalPropertyResource("name", dataProductInfoRes.getName()));
        additionalPropertiesRes.add(new AdditionalPropertyResource("version", dataProductInfoRes.getVersionNumber()));
        additionalPropertiesRes.add(new AdditionalPropertyResource("domain", dataProductInfoRes.getDomain()));
        additionalPropertiesRes.add(new AdditionalPropertyResource("entityType", dataProductInfoRes.getEntityType()));
        additionalPropertiesRes.add(new AdditionalPropertyResource("ownerId", dataProductInfoRes.getOwner().getId()));
        additionalPropertiesRes.add(new AdditionalPropertyResource("ownerName", dataProductInfoRes.getOwner().getName()));
        return additionalPropertiesRes;
    }

}
