package org.opendatamesh.platform.up.metaservice.blindata.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.info.InfoDPDS;
import org.opendatamesh.platform.core.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.opendatamesh.platform.up.metaservice.blindata.client.BlindataClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.BlindataCredentials;
import org.opendatamesh.platform.up.metaservice.blindata.client.PlatformClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.BlindataDataProductPortRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.BlindataDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.ShortUserRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.StewardshipResponsibilityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.StewardshipRoleRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.DataProductPortAssetDetailRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.DataProductPortAssetsRes;
import org.opendatamesh.platform.up.metaservice.server.services.MetaService;
import org.opendatamesh.platform.up.metaservice.server.services.MetaServiceException;
import org.opendatamesh.platform.up.notification.api.resources.NotificationResource;
import org.opendatamesh.platform.up.notification.api.resources.NotificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
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
    private PlatformClient platformClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ODMPlatformService odmPlatformService;

    private static final Logger logger = LoggerFactory.getLogger(BlindataService.class);

    private DataProductPortAssetsRes createAssociatedDataAssetsInBlindata(DataProductPortAssetsRes assets, BlindataClient blindataClient) throws MetaServiceException, JsonProcessingException {
        final DataProductPortAssetsRes dataProductAssets = blindataClient.createDataProductAssets(assets, credentials);
        if (dataProductAssets != null) {
            logger.info("Created related assets to data products");
        } else {
            throw new MetaServiceException("Can't create assets in Blindata");
        }
        return dataProductAssets;
    }

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
            throw new RuntimeException(e.getMessage());
        }
        return notificationRes;
    }

    @Override
    public NotificationResource handleDataProductCreated(NotificationResource notificationRes) {
        DataProductVersionDPDS dataProductFromNotification;
        DataProductPortAssetsRes assets;
        BlindataClient blindataClient = new BlindataClient(restTemplate);
        try {
            dataProductFromNotification = objectMapper.readValue(notificationRes.getEvent().getAfterState(),
                    DataProductVersionDPDS.class);
            assets = getAssetsFromPorts(dataProductFromNotification);
        } catch (JsonProcessingException e) {
            notificationRes.setStatus(NotificationStatus.PROCESS_ERROR);
            notificationRes.setProcessingOutput(e.getMessage());
            return notificationRes;
        } catch (MetaServiceException e) {
            throw new RuntimeException(e);
        }
        logger.debug("Requested load for: {} ", dataProductFromNotification);
        try {
            BlindataDataProductRes dataProductRes = createDataProductOnBlindata(notificationRes, dataProductFromNotification, blindataClient, dataProductFromNotification.getInfo());
            if (dataProductFromNotification.getInfo().getOwner() != null && credentials.getRoleUuid().isPresent()) {
                assignResponsibility(dataProductRes, dataProductFromNotification.getInfo().getOwner().getId(), blindataClient, credentials);
            }
            createAssociatedDataAssetsInBlindata(assets, blindataClient);
            logger.info("Assets Created");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return notificationRes;
    }


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
            BlindataDataProductRes dataProductUpdated = updateDataProductOnBlindata(
                    notificationRes,
                    dataProductFromNotification,
                    blindataClient,
                    existingDataProductOnBlindata
            );
            // CHECK with Blindata team
            if (existingDataProductOnBlindata.getUuid() != null && credentials.getRoleUuid().isPresent()) {
                assignResponsibility(dataProductUpdated, existingDataProductOnBlindata.getUuid(), blindataClient, credentials);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return notificationRes;
    }


    private BlindataDataProductRes updateDataProductOnBlindata(NotificationResource notificationRes, DataProductResource dataProductRes, BlindataClient blindataClient, BlindataDataProductRes existingDataProductOnBlindata) throws MetaServiceException, JsonProcessingException {
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
        port.setDisplayName(portResource.getDisplayName());
        port.setEntityType(entityType);
        port.setVersion(portResource.getVersion());
        return port;

    }


    private void assignResponsibility(BlindataDataProductRes res, String username, BlindataClient client, BlindataCredentials credentials) throws MetaServiceException {
        if (credentials.getRoleUuid().isPresent()) {
            try {
                final StewardshipRoleRes role = client.getRole(credentials.getRoleUuid().get(), credentials);
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
        final StewardshipResponsibilityRes responsibility = client.getResponsibility(blindataUser.getUuid(), res.getUuid(), credentials.getRoleUuid().get(), credentials);
        if (responsibility != null) {
            return client.createResponsibility(createResponsibility(role, blindataUser, res), credentials);
        }
        return null;
    }


    private BlindataDataProductRes createDataProductOnBlindata(NotificationResource notificationRes, DataProductVersionDPDS dataProductVersionRes, BlindataClient blindataClient, InfoDPDS dataProductInfoRes) throws MetaServiceException, JsonProcessingException {
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

    private DataProductPortAssetsRes getAssetsFromPorts(DataProductVersionDPDS dataProductVersionRes) throws MetaServiceException {
        DataProductPortAssetsRes dataProductPortAssetsRes = new DataProductPortAssetsRes();
        List<DataProductPortAssetDetailRes> dataProductPortAssetDetailRes = new ArrayList<>();
        final List<PortDPDS> inputPorts = new ArrayList<>(dataProductVersionRes.getInterfaceComponents().getInputPorts());
        final ArrayList<PortDPDS> outputPorts = new ArrayList<>(dataProductVersionRes.getInterfaceComponents().getOutputPorts());
        final ArrayList<PortDPDS> controlPorts = new ArrayList<>(dataProductVersionRes.getInterfaceComponents().getControlPorts());
        final ArrayList<PortDPDS> discoveryPorts = new ArrayList<>(dataProductVersionRes.getInterfaceComponents().getDiscoveryPorts());
        final ArrayList<PortDPDS> observabilityPorts = new ArrayList<>(dataProductVersionRes.getInterfaceComponents().getObservabilityPorts());
        if (!CollectionUtils.isEmpty(inputPorts)) {
            dataProductPortAssetDetailRes.addAll(odmPlatformService.extractPhysicalResourcesFromPorts(inputPorts));
        }
        if (!CollectionUtils.isEmpty(outputPorts)) {
            dataProductPortAssetDetailRes.addAll((odmPlatformService.extractPhysicalResourcesFromPorts(outputPorts)));
        }
        if (!CollectionUtils.isEmpty(controlPorts)) {
            dataProductPortAssetDetailRes.addAll((odmPlatformService.extractPhysicalResourcesFromPorts(controlPorts)));
        }
        if (!CollectionUtils.isEmpty(discoveryPorts)) {
            dataProductPortAssetDetailRes.addAll((odmPlatformService.extractPhysicalResourcesFromPorts(discoveryPorts)));
        }
        if (!CollectionUtils.isEmpty(observabilityPorts)) {
            dataProductPortAssetDetailRes.addAll((odmPlatformService.extractPhysicalResourcesFromPorts(observabilityPorts)));
        }
        dataProductPortAssetsRes.setPorts(dataProductPortAssetDetailRes);
        return dataProductPortAssetsRes;
    }

    private void deleteDataProductOnBlindata(NotificationResource notificationRes, InfoDPDS infoProductToDelete) throws MetaServiceException, JsonProcessingException {
        BlindataClient blindataClient = new BlindataClient(restTemplate);
        BlindataDataProductRes dataProduct = blindataClient.getDataProduct(infoProductToDelete.getFullyQualifiedName(), credentials);
        logger.debug("Requested delete for: {} ", dataProduct);
        blindataClient.deleteDataProduct(dataProduct.getUuid(), credentials);
        logger.info("Delete Data Product: {} ", dataProduct);
        notificationRes.setProcessingOutput("Deleted");
        notificationRes.setStatus(NotificationStatus.PROCESSED);
    }
}