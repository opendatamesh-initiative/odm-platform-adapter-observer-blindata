package org.opendatamesh.platform.up.metaservice.blindata.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.registry.api.v1.resources.DataProductResource;
import org.opendatamesh.platform.core.dpds.model.InfoDPDS;
import org.opendatamesh.platform.core.dpds.model.PortDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.client.BlindataClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.BlindataCredentials;
import org.opendatamesh.platform.up.metaservice.blindata.resources.*;
import org.opendatamesh.platform.up.metaservice.server.services.MetaService;
import org.opendatamesh.platform.up.metaservice.server.services.MetaServiceException;
import org.opendatamesh.platform.up.notification.api.resources.NotificationResource;
import org.opendatamesh.platform.up.notification.api.resources.NotificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BlindataService implements MetaService {

    @Autowired
    private Environment environment;

    @Autowired
    private BlindataCredentials credentials;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(BlindataService.class);

    @Override
    public NotificationResource handleDataProductDelete(NotificationResource notificationRes) {
        InfoDPDS infoProductToDelete;
        try {
            infoProductToDelete = objectMapper.readValue(
                    notificationRes.getEvent().getBeforeState(),
                    DataProductVersionDPDS.class
            ).getInfo();
        } catch (JsonProcessingException e) {
            notificationRes.setStatus(NotificationStatus.PROCESS_ERROR);
            notificationRes.setProcessingOutput(e.getMessage());
            return notificationRes;
        }
        try {
            deleteDataProductOnBlindata(notificationRes, infoProductToDelete);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return notificationRes;
    }

    @Override
    public NotificationResource handleDataProductCreated(NotificationResource notificationRes) {
        DataProductVersionDPDS dataProductFromNotification;
        BlindataClient blindataClient = new BlindataClient(restTemplate);
        try {
            dataProductFromNotification = objectMapper.readValue(notificationRes.getEvent().getAfterState(),
                    DataProductVersionDPDS.class);
        } catch (JsonProcessingException e) {
            notificationRes.setStatus(NotificationStatus.PROCESS_ERROR);
            notificationRes.setProcessingOutput(e.getMessage());
            return notificationRes;
        }
        logger.debug("Requested load for: {} ", dataProductFromNotification);
        try {
            BlindataDataProductRes dataProductRes = createDataProductOnBlindata(notificationRes, dataProductFromNotification, blindataClient, dataProductFromNotification.getInfo());
            if (dataProductFromNotification.getInfo().getOwner() != null && StringUtils.hasText(credentials.getRoleUuid())) {
                assignResponsibility(dataProductRes, dataProductFromNotification.getInfo().getOwner().getId(), blindataClient, credentials);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to create data product: " + e.getMessage());
        }
        return notificationRes;
    }

    /*@Override
    public NotificationResource handleDataProductUpdate(NotificationResource notificationRes) {
        BlindataClient blindataClient = new BlindataClient(restTemplate);
        DataProductVersionDPDS dataProductFromNotification;
        try {
            dataProductFromNotification = objectMapper.readValue(notificationRes.getEvent().getAfterState(),
                    DataProductVersionDPDS.class);
        } catch (JsonProcessingException e) {
            notificationRes.setStatus(NotificationStatus.PROCESS_ERROR);
            notificationRes.setProcessingOutput(e.getMessage());
            return notificationRes;
        }
        try {
            BlindataDataProductRes existingDataProductOnBlindata = blindataClient.getDataProduct(dataProductFromNotification.getInfo().getFullyQualifiedName(), credentials);
            BlindataDataProductRes dataProductUpdated = updateDataProductOnBlindata(notificationRes, dataProductFromNotification, blindataClient, dataProductFromNotification.getInfo(), existingDataProductOnBlindata);
            if (dataProductFromNotification.getInfo().getOwner() != null && StringUtils.hasText(credentials.getRoleUuid())) {
                assignResponsibility(dataProductUpdated, dataProductFromNotification.getInfo().getOwner().getId(), blindataClient, credentials);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to create data product: " + e.getMessage());
        }
        return notificationRes;
    }*/
    @Override
    public NotificationResource handleDataProductUpdate(NotificationResource notificationRes) {
        BlindataClient blindataClient = new BlindataClient(restTemplate);
        DataProductResource dataProductFromNotification;
        try {
            dataProductFromNotification = objectMapper.readValue(
                    notificationRes.getEvent().getAfterState(),
                    DataProductResource.class
            );
        } catch (JsonProcessingException e) {
            notificationRes.setStatus(NotificationStatus.PROCESS_ERROR);
            notificationRes.setProcessingOutput(e.getMessage());
            return notificationRes;
        }
        try {
            BlindataDataProductRes existingDataProductOnBlindata = blindataClient.getDataProduct(dataProductFromNotification.getFullyQualifiedName(), credentials);
            updateDataProductOnBlindata(
                    notificationRes,
                    dataProductFromNotification,
                    blindataClient,
                    existingDataProductOnBlindata
            );
        } catch (Exception e) {
            throw new RuntimeException("Unable to update data product: " + e.getMessage());
        }
        return notificationRes;
    }

    /*private BlindataDataProductRes updateDataProductOnBlindata(NotificationResource notificationRes, DataProductVersionDPDS dataProductVersionRes, BlindataClient blindataClient, InfoDPDS dataProductInfoRes, BlindataDataProductRes existingDataProductOnBlindata) throws MetaServiceException {
        final BlindataDataProductRes dataProductResToUpdate = createDataProductResource(dataProductInfoRes, dataProductVersionRes);
        dataProductResToUpdate.setUuid(existingDataProductOnBlindata.getUuid());
        BlindataDataProductRes updatedDataProduct = blindataClient.updateDataProduct(dataProductResToUpdate, credentials);
        if (updatedDataProduct != null) {
            logger.info("Update Data Product to Blindata: {} ", existingDataProductOnBlindata);
            notificationRes.setProcessingOutput(updatedDataProduct.toString());
            notificationRes.setStatus(NotificationStatus.PROCESSED);
        } else {
            throw new MetaServiceException("Can't register data product to Blindata");
        }
        return updatedDataProduct;
    }*/
    private BlindataDataProductRes updateDataProductOnBlindata(NotificationResource notificationRes, DataProductResource dataProductRes, BlindataClient blindataClient, BlindataDataProductRes existingDataProductOnBlindata) throws MetaServiceException {
        existingDataProductOnBlindata.setDescription(dataProductRes.getDescription());
        existingDataProductOnBlindata.setDomain(dataProductRes.getDomain());
        final BlindataDataProductRes dataProductResToUpdate = existingDataProductOnBlindata;
        dataProductResToUpdate.setUuid(existingDataProductOnBlindata.getUuid());
        BlindataDataProductRes updatedDataProduct = blindataClient.updateDataProduct(dataProductResToUpdate, credentials);
        if (updatedDataProduct != null) {
            logger.info("Update Data Product to Blindata: {} ", existingDataProductOnBlindata);
            notificationRes.setProcessingOutput(updatedDataProduct.toString());
            notificationRes.setStatus(NotificationStatus.PROCESSED);
        } else {
            throw new MetaServiceException("Can't register updated data product to Blindata");
        }
        return updatedDataProduct;
    }


    private List<BlindataDataProductPortRes> getDataProductsPorts(DataProductVersionDPDS dataProductVersionRes) {
        List<BlindataDataProductPortRes> ports = new ArrayList<>();
        dataProductVersionRes.getInterfaceComponents().getInputPorts().forEach(portResource -> ports.add(validatePort(portResource, "inputPorts")));
        dataProductVersionRes.getInterfaceComponents().getOutputPorts().forEach(portResource -> ports.add(validatePort(portResource, "outputPorts")));
        dataProductVersionRes.getInterfaceComponents().getObservabilityPorts().forEach(portResource -> ports.add(validatePort(portResource, "observabilityPorts")));
        dataProductVersionRes.getInterfaceComponents().getDiscoveryPorts().forEach(portResource -> ports.add(validatePort(portResource, "discoveryPorts")));
        dataProductVersionRes.getInterfaceComponents().getControlPorts().forEach(portResource -> ports.add(validatePort(portResource, "controlPorts")));
        return ports;
    }

    private BlindataDataProductPortRes validatePort(PortDPDS portResource, String entityType) {
        BlindataDataProductPortRes port = new BlindataDataProductPortRes();
        port.setDisplayName(portResource.getDisplayName());
        port.setDescription(portResource.getDescription());
        port.setName(portResource.getName());
        port.setIdentifier(portResource.getFullyQualifiedName());
        port.setUuid(portResource.getId());
        port.setDisplayName(portResource.getDisplayName());
        port.setEntityType(portResource.getEntityType() != null ? portResource.getEntityType().name() : entityType);
        port.setVersion(portResource.getVersion());
        return port;

    }


    private void assignResponsibility(BlindataDataProductRes res, String username, BlindataClient client, BlindataCredentials credentials) throws MetaServiceException {
        try {
            final StewardshipRoleRes role = client.getRole(credentials.getRoleUuid(), credentials);
            final ShortUserRes blindataUser = client.getBlindataUser(username, credentials);
            if (role != null && blindataUser != null) {
                logger.info("Try to assign responsibility to: {} ", blindataUser);
                final StewardshipResponsibilityRes responsibilityOnBlindata = createResponsibilityOnBlindata(role, blindataUser, client, res, credentials);
                logger.info("Responsibility created: {}", responsibilityOnBlindata);
            }
        } catch (Exception e) {
            throw new MetaServiceException("Impossible to assign responsibility" + e.getMessage());
        }
    }

    private StewardshipResponsibilityRes createResponsibility(StewardshipRoleRes role, ShortUserRes blindataUser, BlindataDataProductRes res) {
        StewardshipResponsibilityRes responsibilityRes = new StewardshipResponsibilityRes();
        responsibilityRes.setStewardshipRole(role);
        responsibilityRes.setUser(blindataUser);
        responsibilityRes.setResourceIdentifier(res.getUuid());
        responsibilityRes.setResourceName(res.getName());
        responsibilityRes.setStartDate(new Date());
        return responsibilityRes;
    }

    private BlindataDataProductRes createDataProductResource(InfoDPDS dataProductInfoRes, DataProductVersionDPDS dataProductVersionRes) {
        BlindataDataProductRes dataProductRes = new BlindataDataProductRes();
        dataProductRes.setUuid(dataProductInfoRes.getDataProductId());
        dataProductRes.setName(dataProductInfoRes.getName());
        dataProductRes.setDomain(dataProductInfoRes.getDomain());
        dataProductRes.setIdentifier(dataProductInfoRes.getFullyQualifiedName());
        dataProductRes.setVersion(dataProductInfoRes.getVersionNumber());
        dataProductRes.setDisplayName(dataProductInfoRes.getDisplayName());
        dataProductRes.setDescription(dataProductInfoRes.getDescription());
        dataProductRes.setPorts(getDataProductsPorts(dataProductVersionRes));
        return dataProductRes;
    }

    private StewardshipResponsibilityRes createResponsibilityOnBlindata(StewardshipRoleRes role, ShortUserRes blindataUser, BlindataClient client, BlindataDataProductRes res, BlindataCredentials credentials) throws MetaServiceException {
        final StewardshipResponsibilityRes responsibility = client.getResponsibility(blindataUser.getUuid(), res.getUuid(), credentials.getRoleUuid(), credentials);
        if (responsibility != null) {
            return client.createResponsibility(createResponsibility(role, blindataUser, res), credentials);
        }
        return null;
    }


    private BlindataDataProductRes createDataProductOnBlindata(NotificationResource notificationRes, DataProductVersionDPDS dataProductVersionRes, BlindataClient blindataClient, InfoDPDS dataProductInfoRes) throws MetaServiceException {
        BlindataDataProductRes dataProduct = blindataClient.createDataProduct(createDataProductResource(dataProductInfoRes, dataProductVersionRes), credentials);
        if (dataProduct != null) {
            logger.info("Created Data Product to Blindata: {}", dataProduct);
            notificationRes.setProcessingOutput(dataProduct.toString());
            notificationRes.setStatus(NotificationStatus.PROCESSED);
        } else {
            throw new MetaServiceException("Can't register data product to Blindata");
        }
        return dataProduct;
    }

    private void deleteDataProductOnBlindata(NotificationResource notificationRes, InfoDPDS infoProductToDelete) throws MetaServiceException {
        BlindataClient blindataClient = new BlindataClient(restTemplate);
        BlindataDataProductRes dataProduct = blindataClient.getDataProduct(infoProductToDelete.getFullyQualifiedName(), credentials);
        logger.debug("Requested delete for: {} ", dataProduct);
        blindataClient.deleteDataProduct(dataProduct.getUuid(), credentials);
        logger.info("Delete Data Product: {} ", dataProduct);
        notificationRes.setProcessingOutput("Deleted");
        notificationRes.setStatus(NotificationStatus.PROCESSED);
    }
}