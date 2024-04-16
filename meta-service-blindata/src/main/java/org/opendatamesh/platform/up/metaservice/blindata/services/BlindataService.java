package org.opendatamesh.platform.up.metaservice.blindata.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.info.InfoDPDS;
import org.opendatamesh.platform.core.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyEvaluationResultClient;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultSearchOptions;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDPolicyEvaluationResultClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDStewardshipClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDUserClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.*;
import org.opendatamesh.platform.up.metaservice.blindata.resources.exceptions.BlindataClientException;
import org.opendatamesh.platform.up.metaservice.blindata.resources.exceptions.BlindataClientResourceMappingException;
import org.opendatamesh.platform.up.metaservice.server.services.MetaService;
import org.opendatamesh.platform.up.metaservice.server.services.MetaServiceException;
import org.opendatamesh.platform.up.notification.api.resources.NotificationResource;
import org.opendatamesh.platform.up.notification.api.resources.NotificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BlindataService implements MetaService {
    @Autowired
    private BDPolicyEvaluationResultClient bdPolicyEvaluationResultClient;
    @Autowired
    private BDUserClient bdUserClient;
    @Autowired
    private BDDataProductClient bdDataProductClient;
    @Autowired
    private BDStewardshipClient bdStewardshipClient;
    @Autowired
    private PolicyEvaluationResultClient odmPolicyEvaluationResultsClient;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private ODMRegistryProxy odmPlatformService;

    private static final Logger logger = LoggerFactory.getLogger(BlindataService.class);
    @Value("${blindata.roleUuid}")
    private String roleUuid;

    @Override
    public NotificationResource handleDataProductDelete(NotificationResource notificationRes) throws MetaServiceException {
        try {
            InfoDPDS infoProductToDelete = objectMapper.readValue(
                    notificationRes.getEvent().getBeforeState(),
                    DataProductVersionDPDS.class
            ).getInfo();

            Optional<BDDataProductRes> dataProduct = bdDataProductClient.getDataProduct(infoProductToDelete.getFullyQualifiedName());

            if (dataProduct.isPresent()) {
                deleteDataProductFromBlindata(notificationRes, dataProduct.get());
            } else {
                notificationRes.setProcessingOutput("Data Product Not Found");
            }
            notificationRes.setStatus(NotificationStatus.PROCESSED);
            return notificationRes;
        } catch (BlindataClientException | BlindataClientResourceMappingException e) {
            logger.warn(e.getMessage(), e);
            notificationRes.setStatus(NotificationStatus.PROCESS_ERROR);
            notificationRes.setProcessingOutput(e.getMessage());
            return notificationRes;
        } catch (Exception e) {
            throw new MetaServiceException(e.getMessage(), e);
        }
    }

    @Override
    public NotificationResource handleDataProductCreated(NotificationResource notificationRes) throws MetaServiceException {
        try {
            DataProductVersionDPDS odmDataProduct = objectMapper.readValue(notificationRes.getEvent().getAfterState(), DataProductVersionDPDS.class);

            logger.debug("Requested load for: {} ", odmDataProduct);
            InfoDPDS odmDataProductInfo = odmDataProduct.getInfo();

            Optional<BDDataProductRes> oldBdDataProduct = bdDataProductClient.getDataProduct(odmDataProductInfo.getFullyQualifiedName());

            if (oldBdDataProduct.isEmpty()) {
                BDDataProductRes bdDataProductCreated = createDataProductOnBlindata(odmDataProductInfo, odmDataProduct);

                if (bdDataProductCreated != null) {
                    logger.info("Created Data Product on Blindata: {}", bdDataProductCreated.getDisplayName());

                    createDataProductAssetsOnBlindata(odmDataProduct, bdDataProductCreated);

                    if (odmDataProductInfo.getOwner() != null) {
                        assignResponsibility(bdDataProductCreated, odmDataProductInfo.getOwner().getId());
                    }

                    uploadPolicyEvaluationResultsOnBlindata(odmDataProductInfo, bdDataProductCreated);

                    notificationRes.setProcessingOutput(objectMapper.writeValueAsString(bdDataProductCreated));
                    notificationRes.setStatus(NotificationStatus.PROCESSED);
                }
            } else {
                handleDataProductUpdate(notificationRes);
            }
            return notificationRes;
        } catch (BlindataClientException | BlindataClientResourceMappingException e) {
            logger.warn(e.getMessage(), e);
            notificationRes.setStatus(NotificationStatus.PROCESS_ERROR);
            notificationRes.setProcessingOutput(e.getMessage());
            return notificationRes;
        } catch (Exception e) {
            throw new MetaServiceException(e.getMessage(), e);
        }
    }

    @Override
    public NotificationResource handleDataProductUpdate(NotificationResource notificationRes) throws MetaServiceException {
        try {
            DataProductVersionDPDS odmDataProduct = objectMapper.readValue(notificationRes.getEvent().getAfterState(), DataProductVersionDPDS.class);
            logger.debug("Requested load for: {} ", odmDataProduct);
            InfoDPDS odmDataProductInfo = odmDataProduct.getInfo();

            Optional<BDDataProductRes> oldBdDataProduct = bdDataProductClient.getDataProduct(odmDataProductInfo.getFullyQualifiedName());

            if (oldBdDataProduct.isPresent()) {
                BDDataProductRes dataProductResourceForUpdate = buildBDDataProductRes(odmDataProductInfo, getDataProductsPorts(odmDataProduct));
                dataProductResourceForUpdate.setUuid(oldBdDataProduct.get().getUuid());

                BDDataProductRes updatedDataProduct = bdDataProductClient.updateDataProduct(dataProductResourceForUpdate);
                if (updatedDataProduct != null) {
                    logger.info("Updated Data Product to Blindata: {}", updatedDataProduct.getDisplayName());

                    BDProductPortAssetsRes odmDataProductAssets = getAssetsFromPorts(odmDataProduct);
                    bdDataProductClient.createDataProductAssets(odmDataProductAssets);

                    if (odmDataProductInfo.getOwner() != null && StringUtils.hasText(roleUuid)) {
                        assignResponsibility(updatedDataProduct, odmDataProductInfo.getOwner().getId());
                    }

                    uploadPolicyEvaluationResultsOnBlindata(odmDataProductInfo, updatedDataProduct);

                    notificationRes.setProcessingOutput(updatedDataProduct.toString());
                    notificationRes.setStatus(NotificationStatus.PROCESSED);
                }
            }
            return notificationRes;
        } catch (BlindataClientResourceMappingException | BlindataClientException e) {
            logger.warn(e.getMessage(), e);
            notificationRes.setStatus(NotificationStatus.PROCESS_ERROR);
            notificationRes.setProcessingOutput(e.getMessage());
            return notificationRes;
        } catch (Exception e) {
            throw new MetaServiceException(e.getMessage(), e);
        }
    }

    private List<BDDataProductPortRes> getDataProductsPorts(DataProductVersionDPDS dataProductVersionRes) {
        List<BDDataProductPortRes> ports = new ArrayList<>();
        dataProductVersionRes.getInterfaceComponents().getInputPorts().forEach(portResource -> ports.add(validatePort(portResource, "inputPorts")));
        dataProductVersionRes.getInterfaceComponents().getOutputPorts().forEach(portResource -> ports.add(validatePort(portResource, "outputPorts")));
        dataProductVersionRes.getInterfaceComponents().getObservabilityPorts().forEach(portResource -> ports.add(validatePort(portResource, "observabilityPorts")));
        dataProductVersionRes.getInterfaceComponents().getDiscoveryPorts().forEach(portResource -> ports.add(validatePort(portResource, "discoveryPorts")));
        dataProductVersionRes.getInterfaceComponents().getControlPorts().forEach(portResource -> ports.add(validatePort(portResource, "controlPorts")));
        return ports;
    }

    private BDDataProductPortRes validatePort(PortDPDS portResource, String entityType) {
        BDDataProductPortRes port = new BDDataProductPortRes();
        port.setDisplayName(portResource.getDisplayName());
        port.setDescription(portResource.getDescription());
        port.setName(portResource.getName());
        port.setServicesType(portResource.getPromises().getServicesType());
        port.setIdentifier(portResource.getFullyQualifiedName());
        port.setDisplayName(portResource.getDisplayName());
        port.setEntityType(entityType);
        port.setVersion(portResource.getVersion());
        port.setAdditionalProperties(extractCustomProperties(portResource));
        return port;

    }

    private List<AdditionalPropertiesRes> extractCustomProperties(PortDPDS portDPDS) {
        List<AdditionalPropertiesRes> additionalPropertiesRes = new ArrayList<>();
        if (portDPDS.getPromises().getSlo() != null && StringUtils.hasText(portDPDS.getPromises().getSlo().getDescription())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("sloDescription", portDPDS.getPromises().getSlo().getDescription()));
        }
        if (portDPDS.getPromises().getDeprecationPolicy() != null && StringUtils.hasText(portDPDS.getPromises().getDeprecationPolicy().getDescription())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("deprecationPolicy", portDPDS.getPromises().getSlo().getDescription()));
        }
        return additionalPropertiesRes;
    }

    private void assignResponsibility(BDDataProductRes res, String username) {
        if (StringUtils.hasText(roleUuid)) {
            BDStewardshipRoleRes role = bdStewardshipClient.getRole(roleUuid);
            Optional<BDShortUserRes> blindataUser = bdUserClient.getBlindataUser(username);
            if (role != null && blindataUser.isPresent()) {
                logger.info("Try to assign responsibility to: {} ", blindataUser.get());
                Optional<BDStewardshipResponsibilityRes> responsibility = bdStewardshipClient.getResponsibility(blindataUser.get().getUuid(), res.getUuid());
                if (responsibility.isPresent()) {
                    BDStewardshipResponsibilityRes responsibilityRes = bdStewardshipClient.createResponsibility(createResponsibility(role, blindataUser.get(), res));
                    logger.info("Responsibility created: {}", responsibilityRes);
                }
            }
        }
    }

    private BDStewardshipResponsibilityRes createResponsibility(BDStewardshipRoleRes role, BDShortUserRes blindataUser, BDDataProductRes res) {
        BDStewardshipResponsibilityRes responsibilityRes = new BDStewardshipResponsibilityRes();
        responsibilityRes.setStewardshipRole(role);
        responsibilityRes.setUser(blindataUser);
        responsibilityRes.setResourceIdentifier(res.getUuid());
        responsibilityRes.setResourceName(res.getName());
        responsibilityRes.setStartDate(new Date());
        return responsibilityRes;
    }

    private BDDataProductRes buildBDDataProductRes(InfoDPDS dataProductInfoRes, List<BDDataProductPortRes> dataProductPorts) {
        BDDataProductRes dataProductRes = new BDDataProductRes();
        dataProductRes.setUuid(dataProductInfoRes.getDataProductId());
        dataProductRes.setName(dataProductInfoRes.getName());
        dataProductRes.setDomain(dataProductInfoRes.getDomain());
        dataProductRes.setIdentifier(dataProductInfoRes.getFullyQualifiedName());
        dataProductRes.setVersion(dataProductInfoRes.getVersionNumber());
        dataProductRes.setDisplayName(dataProductInfoRes.getDisplayName());
        dataProductRes.setDescription(dataProductInfoRes.getDescription());
        dataProductRes.setPorts(dataProductPorts);
        return dataProductRes;
    }

    private BDProductPortAssetsRes getAssetsFromPorts(DataProductVersionDPDS dataProductVersionRes) throws MetaServiceException {
        BDProductPortAssetsRes dataProductPortAssetsRes = new BDProductPortAssetsRes();
        List<BDDataProductPortAssetDetailRes> dataProductPortAssetDetailRes = new ArrayList<>();
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

    private void deleteDataProductFromBlindata(NotificationResource notificationRes, BDDataProductRes dataProduct) {
        logger.debug("Requested delete for: {} ", dataProduct);
        bdDataProductClient.deleteDataProduct(dataProduct.getUuid());
        logger.info("Delete Data Product: {} ", dataProduct);
        notificationRes.setProcessingOutput("Deleted");
    }

    private void createDataProductAssetsOnBlindata(DataProductVersionDPDS odmDataProduct, BDDataProductRes bdDataProductCreated) throws MetaServiceException { //TODO change this to the client
        BDProductPortAssetsRes odmDataProductAssets = getAssetsFromPorts(odmDataProduct);
        if (!CollectionUtils.isEmpty(odmDataProductAssets.getPorts())) {
            bdDataProductClient.createDataProductAssets(odmDataProductAssets);
            logger.info("Assets created for Data Product: {}", bdDataProductCreated.getName());
        }
    }

    private BDDataProductRes createDataProductOnBlindata(InfoDPDS odmDataProductInfo, DataProductVersionDPDS odmDataProduct) {
        BDDataProductRes bdDataProductToCreate = buildBDDataProductRes(odmDataProductInfo, getDataProductsPorts(odmDataProduct));
        return bdDataProductClient.createDataProduct(bdDataProductToCreate);
    }

    private void uploadPolicyEvaluationResultsOnBlindata(InfoDPDS odmDataProductInfo, BDDataProductRes bdDataProductCreated) {
        Page<PolicyEvaluationResultResource> odmPolicyEvaluationResults = findOdmPolicyEvaluationResults(odmDataProductInfo);
        BDPolicyEvaluationRecords bdPolicyEvaluationRecords = mapOdmPolicyEvaluationResultsToBlindataPolicyEvaluationRecords(odmPolicyEvaluationResults, bdDataProductCreated);
        logger.info("Starting uploading policy evaluation results to Blindata");
        BDUploadResultsMessage uploadResultsMessage = bdPolicyEvaluationResultClient.createPolicyEvaluationRecords(bdPolicyEvaluationRecords);
        logger.info("Ended uploading policy evaluation results to Blindata: {}", uploadResultsMessage);
    }

    private BDPolicyEvaluationRecords mapOdmPolicyEvaluationResultsToBlindataPolicyEvaluationRecords(Page<PolicyEvaluationResultResource> odmPolicyEvaluationResults, BDDataProductRes bdDataProductCreated) {
        BDPolicyEvaluationRecords bdPolicyEvaluationRecords = new BDPolicyEvaluationRecords();
        for (PolicyEvaluationResultResource odmPolicyEvaluationResult : odmPolicyEvaluationResults) {
            BDPolicyEvaluationRecord bdPolicyEvaluationRecord = mapOdmEvaluationResultToBlindataEvaluationRecord(odmPolicyEvaluationResult, bdDataProductCreated);
            bdPolicyEvaluationRecords.getRecords().add(bdPolicyEvaluationRecord);
        }
        return bdPolicyEvaluationRecords;
    }

    private BDPolicyEvaluationRecord mapOdmEvaluationResultToBlindataEvaluationRecord(PolicyEvaluationResultResource odmPolicyEvaluationResult, BDDataProductRes bdDataProductCreated) {
        BDPolicyEvaluationRecord bdPolicyEvaluationRecord = new BDPolicyEvaluationRecord();
        bdPolicyEvaluationRecord.setPolicyName(odmPolicyEvaluationResult.getPolicy().getName());
        bdPolicyEvaluationRecord.setImplementationName(odmPolicyEvaluationResult.getPolicy().getName());
        bdPolicyEvaluationRecord.setResolverKey("uuid");
        bdPolicyEvaluationRecord.setResolverValue(bdDataProductCreated.getUuid());
        bdPolicyEvaluationRecord.setResourceType(BDResourceType.DATA_PRODUCT);
        bdPolicyEvaluationRecord.setEvaluationResult(odmPolicyEvaluationResult.getResult() ? BDPolicyEvaluationRecord.BDPolicyEvaluationResult.VERIFIED : BDPolicyEvaluationRecord.BDPolicyEvaluationResult.FAILED);
        bdPolicyEvaluationRecord.setEvaluationDate(odmPolicyEvaluationResult.getCreatedAt());
        //TODO bdPolicyEvaluationRecord.setDescription();
        return bdPolicyEvaluationRecord;
    }

    private Page<PolicyEvaluationResultResource> findOdmPolicyEvaluationResults(InfoDPDS odmDataProductInfo) {
        PolicyEvaluationResultSearchOptions policyEvaluationResultFilters = new PolicyEvaluationResultSearchOptions();
        policyEvaluationResultFilters.setDataProductId(odmDataProductInfo.getDataProductId());
        return odmPolicyEvaluationResultsClient.getPolicyEvaluationResults(PageRequest.ofSize(Integer.MAX_VALUE), policyEvaluationResultFilters);
    }
}