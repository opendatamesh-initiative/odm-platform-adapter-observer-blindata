package org.opendatamesh.platform.up.metaservice.blindata.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.dpds.model.info.InfoDPDS;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.enums.EventNotificationStatus;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyEvaluationResultClient;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultSearchOptions;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDPolicyEvaluationResultClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDStewardshipClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDUserClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.*;
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
public class NotificationEventHandlerService {

    @Autowired
    private BDPolicyEvaluationResultClient bdPolicyEvaluationResultClient;
    @Autowired
    private BDUserClient bdUserClient;
    @Autowired
    private BDDataProductClient bdDataProductClient;
    @Autowired
    private BDStewardshipClient bdStewardshipClient;
    @Autowired
    private PolicyEvaluationResultClient policyEvaluationResultClient;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private DataProductPortAssetAnalyzer odmPlatformService;

    private static final Logger logger = LoggerFactory.getLogger(NotificationEventHandlerService.class);
    @Value("${blindata.roleUuid}")
    private String roleUuid;

    public EventNotificationResource handleDataProductDelete(EventNotificationResource notificationRes) {
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
            notificationRes.setStatus(EventNotificationStatus.PROCESSED);
            return notificationRes;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            notificationRes.setStatus(EventNotificationStatus.PROCESS_ERROR);
            notificationRes.setProcessingOutput(e.getMessage());
            return notificationRes;
        }
    }

    public EventNotificationResource handleDataProductCreated(EventNotificationResource notificationRes) {
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
                    notificationRes.setStatus(EventNotificationStatus.PROCESSED);
                }
            } else {
                handleDataProductUpdate(notificationRes);
            }
            return notificationRes;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            notificationRes.setStatus(EventNotificationStatus.PROCESS_ERROR);
            notificationRes.setProcessingOutput(e.getMessage());
            return notificationRes;
        }
    }

    public EventNotificationResource handleDataProductUpdate(EventNotificationResource notificationRes) {
        try {
            DataProductVersionDPDS odmDataProduct = objectMapper.readValue(notificationRes.getEvent().getAfterState(), DataProductVersionDPDS.class);
            logger.debug("Requested load for: {} ", odmDataProduct);
            InfoDPDS odmDataProductInfo = odmDataProduct.getInfo();

            Optional<BDDataProductRes> oldBdDataProduct = bdDataProductClient.getDataProduct(odmDataProductInfo.getFullyQualifiedName());

            if (oldBdDataProduct.isPresent()) {
                BDDataProductRes dataProductResourceForUpdate = buildBDDataProductRes(odmDataProductInfo, generateBDPortsFromODMProductVersion(odmDataProduct));
                dataProductResourceForUpdate.setUuid(oldBdDataProduct.get().getUuid());

                BDDataProductRes updatedDataProduct = bdDataProductClient.updateDataProduct(dataProductResourceForUpdate);
                if (updatedDataProduct != null) {
                    logger.info("Updated Data Product to Blindata: {}", updatedDataProduct.getDisplayName());

                    createDataProductAssetsOnBlindata(odmDataProduct, updatedDataProduct);

                    if (odmDataProductInfo.getOwner() != null && StringUtils.hasText(roleUuid)) {
                        assignResponsibility(updatedDataProduct, odmDataProductInfo.getOwner().getId());
                    }

                    uploadPolicyEvaluationResultsOnBlindata(odmDataProductInfo, updatedDataProduct);

                    notificationRes.setProcessingOutput(updatedDataProduct.toString());
                    notificationRes.setStatus(EventNotificationStatus.PROCESSED);
                }
            }
            return notificationRes;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            notificationRes.setStatus(EventNotificationStatus.PROCESS_ERROR);
            notificationRes.setProcessingOutput(e.getMessage());
            return notificationRes;
        }
    }

    private List<BDDataProductPortRes> generateBDPortsFromODMProductVersion(DataProductVersionDPDS dataProductVersionRes) {
        List<BDDataProductPortRes> ports = new ArrayList<>();
        dataProductVersionRes.getInterfaceComponents().getInputPorts().forEach(portResource -> ports.add(ODMPortToBDPort(portResource, "inputPorts")));
        dataProductVersionRes.getInterfaceComponents().getOutputPorts().forEach(portResource -> ports.add(ODMPortToBDPort(portResource, "outputPorts")));
        dataProductVersionRes.getInterfaceComponents().getObservabilityPorts().forEach(portResource -> ports.add(ODMPortToBDPort(portResource, "observabilityPorts")));
        dataProductVersionRes.getInterfaceComponents().getDiscoveryPorts().forEach(portResource -> ports.add(ODMPortToBDPort(portResource, "discoveryPorts")));
        dataProductVersionRes.getInterfaceComponents().getControlPorts().forEach(portResource -> ports.add(ODMPortToBDPort(portResource, "controlPorts")));
        return ports;
    }

    private BDDataProductPortRes ODMPortToBDPort(PortDPDS portResource, String entityType) {
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

    private BDProductPortAssetsRes getAssetsFromPorts(DataProductVersionDPDS dataProductVersionRes) {
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

    private void deleteDataProductFromBlindata(EventNotificationResource notificationRes, BDDataProductRes dataProduct) {
        logger.debug("Requested delete for: {} ", dataProduct);
        bdDataProductClient.deleteDataProduct(dataProduct.getUuid());
        logger.info("Delete Data Product: {} ", dataProduct);
        notificationRes.setProcessingOutput("Deleted");
    }

    private void createDataProductAssetsOnBlindata(DataProductVersionDPDS odmDataProduct, BDDataProductRes bdDataProduct) {
        BDProductPortAssetsRes odmDataProductAssets = getAssetsFromPorts(odmDataProduct);
        if (!CollectionUtils.isEmpty(odmDataProductAssets.getPorts())) {
            bdDataProductClient.createDataProductAssets(odmDataProductAssets);
            logger.info("Assets created for Data Product: {}", bdDataProduct.getName());
        }
    }

    private BDDataProductRes createDataProductOnBlindata(InfoDPDS odmDataProductInfo, DataProductVersionDPDS odmDataProduct) {
        BDDataProductRes bdDataProductToCreate = buildBDDataProductRes(odmDataProductInfo, generateBDPortsFromODMProductVersion(odmDataProduct));
        return bdDataProductClient.createDataProduct(bdDataProductToCreate);
    }

    private void uploadPolicyEvaluationResultsOnBlindata(InfoDPDS odmDataProductInfo, BDDataProductRes bdDataProductCreated) {
        Page<PolicyEvaluationResultResource> odmPolicyEvaluationResults = findOdmPolicyEvaluationResults(odmDataProductInfo);
        if (odmPolicyEvaluationResults.isEmpty()) return;
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
        bdPolicyEvaluationRecord.setResourceType("DATA_PRODUCT");
        bdPolicyEvaluationRecord.setEvaluationResult(odmPolicyEvaluationResult.getResult() ? BDPolicyEvaluationRecord.BDPolicyEvaluationResult.VERIFIED : BDPolicyEvaluationRecord.BDPolicyEvaluationResult.FAILED);
        bdPolicyEvaluationRecord.setEvaluationDate(odmPolicyEvaluationResult.getCreatedAt());
        //TODO bdPolicyEvaluationRecord.setDescription();
        return bdPolicyEvaluationRecord;
    }

    private Page<PolicyEvaluationResultResource> findOdmPolicyEvaluationResults(InfoDPDS odmDataProductInfo) {
        PolicyEvaluationResultSearchOptions policyEvaluationResultFilters = new PolicyEvaluationResultSearchOptions();
        policyEvaluationResultFilters.setDataProductId(odmDataProductInfo.getDataProductId());
        return policyEvaluationResultClient.getPolicyEvaluationResults(PageRequest.ofSize(Integer.MAX_VALUE), policyEvaluationResultFilters);
    }
}