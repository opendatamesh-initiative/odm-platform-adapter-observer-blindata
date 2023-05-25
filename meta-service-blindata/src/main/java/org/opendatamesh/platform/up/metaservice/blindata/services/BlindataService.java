package org.opendatamesh.platform.up.metaservice.blindata.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DataProductVersionResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.InfoResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.PortResource;
import org.opendatamesh.platform.up.metaservice.blindata.client.BlindataClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.BlindataCredentials;
import org.opendatamesh.platform.up.metaservice.blindata.resources.*;
import org.opendatamesh.platform.up.metaservice.resources.v1.NotificationResource;
import org.opendatamesh.platform.up.metaservice.resources.v1.NotificationStatus;
import org.opendatamesh.platform.up.metaservice.services.MetaService;
import org.opendatamesh.platform.up.metaservice.services.MetaServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${blindata.url}")
    private String blindataUrl;

    @Value("${blindata.user}")
    private String blindataUsername;

    @Value("${blindata.password}")
    private String blindataPass;

    @Value("${blindata.tenantUUID}")
    private String blindataTenantUuid;

    @Value("${blindata.roleUuid}")
    private String roleUuid;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(BlindataService.class);

    @Override
    public NotificationResource deleteProductVersionCreatedEvent(NotificationResource notificationRes) {
        String fullyQualifiedName = null;
        try {
            fullyQualifiedName = objectMapper.readValue(notificationRes.getEvent().getAfterState(),
                    DataProductVersionResource.class).getInfo().getFullyQualifiedName();
        } catch (JsonProcessingException e) {
            notificationRes.setStatus(NotificationStatus.PROCESS_ERROR);
            notificationRes.setProcessingOutput(e.getMessage());
            return notificationRes;
        }
        try {
            BlindataClient blindataClient = new BlindataClient(restTemplate);
            logger.debug("Requested delete for: {} ", fullyQualifiedName);
            BlindataCredentials credentials = new BlindataCredentials(
                    blindataUrl,
                    blindataUsername,
                    blindataPass,
                    blindataTenantUuid,
                    roleUuid);
            blindataClient.deleteDataProduct(fullyQualifiedName, credentials);
            logger.info("Delete Data Product: {} ", fullyQualifiedName);
            notificationRes.setProcessingOutput("Deleted");
            notificationRes.setStatus(NotificationStatus.PROCESSED);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return notificationRes;
    }

    @Override
    public NotificationResource handleDataProductVersionCreatedEvent(NotificationResource notificationRes) {

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
        logger.debug("Requested load for: {} ", dataProductVersionRes);
        // Definition of: blindata endpoint, credentials and tenant.
        BlindataCredentials credentials = new BlindataCredentials(
                blindataUrl,
                blindataUsername,
                blindataPass,
                blindataTenantUuid,
                roleUuid);
        InfoResource dataProductInfoRes = dataProductVersionRes.getInfo();
        try {
            BlindataDataProductRes dataProduct = blindataClient.getDataProduct(dataProductInfoRes.getFullyQualifiedName(), credentials);
            if (dataProduct != null) {
                //update data product
                final BlindataDataProductRes dataProductResToUpdate = createDataProductResource(dataProductInfoRes, dataProductVersionRes);
                dataProductResToUpdate.setUuid(dataProduct.getUuid());
                BlindataDataProductRes updatedDataProduct = blindataClient.updateDataProduct(dataProductResToUpdate, credentials);
                if (updatedDataProduct != null) {
                    logger.info("Update Data Product to Blindata: {} ", dataProduct);
                    notificationRes.setProcessingOutput(updatedDataProduct.toString());
                    notificationRes.setStatus(NotificationStatus.PROCESSED);
                } else {
                    throw new MetaServiceException("Can't register data product to Blindata");
                }
            } else {
                //Create Data product
                dataProduct = blindataClient.createDataProduct(createDataProductResource(dataProductInfoRes, dataProductVersionRes), credentials);
                if (dataProduct != null) {
                    logger.info("Created Data Product to Blindata: {}", dataProduct);
                    notificationRes.setProcessingOutput(dataProduct.toString());
                    notificationRes.setStatus(NotificationStatus.PROCESSED);
                } else {
                    throw new MetaServiceException("Can't register data product to Blindata");
                }

            }
            if (dataProductInfoRes.getOwner() != null) {
                assignResponsibility(dataProduct, dataProductInfoRes.getOwner().getId(), blindataClient, credentials);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to create data product: " + e.getMessage());
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

    private List<BlindataDataProductPortRes> getDataProductsPorts(DataProductVersionResource dataProductVersionRes) {
        List<BlindataDataProductPortRes> ports = new ArrayList<>();
        dataProductVersionRes.getInterfaceComponents().getInputPorts().forEach(portResource -> ports.add(validatePort(portResource, "INPUT_PORT")));
        dataProductVersionRes.getInterfaceComponents().getOutputPorts().forEach(portResource -> ports.add(validatePort(portResource, "OUTPUT_PORT")));
        return ports;
    }

    private BlindataDataProductPortRes validatePort(PortResource portResource, String entityType) {
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
            if (StringUtils.hasText(roleUuid)) {
                final StewardshipRoleRes role = client.getRole(roleUuid, credentials);
                final ShortUserRes blindataUser = client.getBlindataUser(username, credentials);
                if (role != null && blindataUser != null) {
                    logger.info("Try to assign responsibility to: {} ", blindataUser);
                    final StewardshipResponsibilityRes responsibilityOnBlindata = createResponsibilityOnBlindata(role, blindataUser, client, res, credentials);
                    logger.info("Responsibility created: {}", responsibilityOnBlindata);
                }
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

    private BlindataDataProductRes createDataProductResource(InfoResource dataProductInfoRes, DataProductVersionResource dataProductVersionRes) {
        BlindataDataProductRes dataProductRes = new BlindataDataProductRes();
        dataProductRes.setUuid(dataProductInfoRes.getDataProductId());
        dataProductRes.setName(dataProductInfoRes.getName());
        dataProductRes.setDomain(dataProductInfoRes.getDomain());
        dataProductRes.setIdentifier(dataProductInfoRes.getFullyQualifiedName());
        dataProductRes.setVersion(dataProductInfoRes.getVersionNumber());
        dataProductRes.setDisplayName(dataProductInfoRes.getDisplayName());
        dataProductRes.setVersion(dataProductInfoRes.getVersionNumber());
        dataProductRes.setDomain(dataProductInfoRes.getDomain());
        dataProductRes.setDescription(dataProductInfoRes.getDescription());
        dataProductRes.setPorts(getDataProductsPorts(dataProductVersionRes));
        return dataProductRes;
    }

    private StewardshipResponsibilityRes createResponsibilityOnBlindata(StewardshipRoleRes role, ShortUserRes blindataUser, BlindataClient client, BlindataDataProductRes res, BlindataCredentials credentials) throws Exception {
        final StewardshipResponsibilityRes responsibility = client.getResponsibility(blindataUser.getUuid(), res.getUuid(), roleUuid, credentials);
        //check if not are exisisting responsibility
        if (responsibility != null) {
            return client.createResponsibility(createResponsibility(role, blindataUser, res), credentials);
        }
        return null;
    }
}
