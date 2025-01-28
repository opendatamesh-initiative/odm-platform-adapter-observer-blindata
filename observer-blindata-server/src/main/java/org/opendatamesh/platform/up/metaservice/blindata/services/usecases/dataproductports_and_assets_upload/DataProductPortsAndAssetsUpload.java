package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.dpds.model.interfaces.InterfaceComponentsDPDS;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.dpds.model.interfaces.PromisesDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.*;
import org.opendatamesh.platform.up.metaservice.blindata.resources.exceptions.BlindataClientException;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;

class DataProductPortsAndAssetsUpload implements UseCase {

    private static final String USE_CASE_PREFIX = "[DataProductVersionUpload]";

    private final DataProductPortsAndAssetsUploadBlindataOutboundPort blindataOutboundPort;
    private final DataProductPortsAndAssetsUploadOdmOutboundPort odmOutboundPort;

    DataProductPortsAndAssetsUpload(DataProductPortsAndAssetsUploadBlindataOutboundPort blindataOutboundPort, DataProductPortsAndAssetsUploadOdmOutboundPort odmOutboundPort) {
        this.blindataOutboundPort = blindataOutboundPort;
        this.odmOutboundPort = odmOutboundPort;
    }

    @Override
    public void execute() throws UseCaseExecutionException {
        withErrorHandling(() -> {
            InterfaceComponentsDPDS interfaceComponentsDPDS = odmOutboundPort.getDataProductVersion().getInterfaceComponents();
            validateDataProductDescriptorPorts(interfaceComponentsDPDS);

            Optional<BDDataProductRes> existentDataProduct = blindataOutboundPort.findDataProduct(odmOutboundPort.getDataProductVersion().getInfo().getFullyQualifiedName());
            if (existentDataProduct.isEmpty()) {
                getUseCaseLogger().warn(String.format("%s Data product: %s has not been created yet on Blindata.", USE_CASE_PREFIX, odmOutboundPort.getDataProductVersion().getInfo().getFullyQualifiedName()));
                return;
            }
            List<BDDataProductPortRes> bdDataProductPorts = extractBdDataProductPorts(interfaceComponentsDPDS);
            getUseCaseLogger().info(String.format("%s Data product: %s, Blindata ports found: %s.", USE_CASE_PREFIX, existentDataProduct.get().getIdentifier(), bdDataProductPorts.size()));
            existentDataProduct.get().setPorts(bdDataProductPorts);
            blindataOutboundPort.updateDataProductPorts(existentDataProduct.get());
            getUseCaseLogger().info(String.format("%s Data product: %s, updated ports on Blindata.", USE_CASE_PREFIX, existentDataProduct.get().getIdentifier()));

            List<BDDataProductPortAssetDetailRes> dataProductPortAssets = extractBdAssetsFromDpPorts(interfaceComponentsDPDS);
            getUseCaseLogger().info(String.format("%s Data product: %s, found %s data assets.", USE_CASE_PREFIX, existentDataProduct.get().getIdentifier(), dataProductPortAssets.size()));
            validateDataProductPortsAssets(dataProductPortAssets);
            uploadAssetsOnBlindata(dataProductPortAssets);
        });
    }

    private void validateDataProductDescriptorPorts(InterfaceComponentsDPDS interfaceComponentsDPDS) {
        if (interfaceComponentsDPDS == null ||
                (CollectionUtils.isEmpty(interfaceComponentsDPDS.getControlPorts()) &&
                        CollectionUtils.isEmpty(interfaceComponentsDPDS.getOutputPorts()) &&
                        CollectionUtils.isEmpty(interfaceComponentsDPDS.getObservabilityPorts()) &&
                        CollectionUtils.isEmpty(interfaceComponentsDPDS.getInputPorts()) &&
                        CollectionUtils.isEmpty(interfaceComponentsDPDS.getDiscoveryPorts()))
        ) {
            getUseCaseLogger().warn(String.format("%s Missing interface components on data product: %s", USE_CASE_PREFIX, odmOutboundPort.getDataProductVersion().getInfo().getFullyQualifiedName()));
        }

    }

    private void validateDataProductPortsAssets(List<BDDataProductPortAssetDetailRes> dataProductPortAssetsDetails) {
        for (BDDataProductPortAssetDetailRes dataProductPortAssetsDetail : dataProductPortAssetsDetails) {
            if (!StringUtils.hasText(dataProductPortAssetsDetail.getPortIdentifier())) {
                getUseCaseLogger().warn(USE_CASE_PREFIX + "Missing port identifier on data product port asset");
                break;
            }
            for (BDProductPortAssetSystemRes dataProductPortAssets : dataProductPortAssetsDetail.getAssets()) {
                validateSystem(dataProductPortAssets.getSystem());
                if (dataProductPortAssets.getPhysicalEntities() == null) {
                    getUseCaseLogger().warn(USE_CASE_PREFIX + " Missing physical entities on data product port assets");
                } else {
                    dataProductPortAssets.getPhysicalEntities().forEach(this::validatePhysicalEntity);
                }
            }
        }
    }

    private void validateSystem(BDSystemRes system) {
        if (system == null) {
            getUseCaseLogger().warn(USE_CASE_PREFIX + " Missing system on data product port assets.");
        } else {
            if (!StringUtils.hasText(system.getName())) {
                getUseCaseLogger().warn(USE_CASE_PREFIX + " Missing name on data product port asset system.");
            }
        }
    }

    private void validatePhysicalEntity(BDPhysicalEntityRes physicalEntity) {
        if (!StringUtils.hasText(physicalEntity.getName())) {
            getUseCaseLogger().warn(USE_CASE_PREFIX + " Missing name on data product port asset physical entity.");
        }
        if (physicalEntity.getPhysicalFields() != null) {
            for (BDPhysicalFieldRes physicalField : physicalEntity.getPhysicalFields()) {
                if (!StringUtils.hasText(physicalField.getName())) {
                    getUseCaseLogger().warn(USE_CASE_PREFIX + " Missing name on data product port asset physical field.");
                }
            }
        }
    }

    private List<BDDataProductPortRes> extractBdDataProductPorts(InterfaceComponentsDPDS interfaceComponentsDPDS) {
        List<BDDataProductPortRes> bdDataProductPorts = new ArrayList<>();
        interfaceComponentsDPDS.getInputPorts().forEach(portResource -> bdDataProductPorts.add(odmToBlindataDataProductPort(portResource, "inputPorts")));
        interfaceComponentsDPDS.getOutputPorts().forEach(portResource -> bdDataProductPorts.add(odmToBlindataDataProductPort(portResource, "outputPorts")));
        interfaceComponentsDPDS.getObservabilityPorts().forEach(portResource -> bdDataProductPorts.add(odmToBlindataDataProductPort(portResource, "observabilityPorts")));
        interfaceComponentsDPDS.getDiscoveryPorts().forEach(portResource -> bdDataProductPorts.add(odmToBlindataDataProductPort(portResource, "discoveryPorts")));
        interfaceComponentsDPDS.getControlPorts().forEach(portResource -> bdDataProductPorts.add(odmToBlindataDataProductPort(portResource, "controlPorts")));
        return bdDataProductPorts;
    }

    private void uploadAssetsOnBlindata(List<BDDataProductPortAssetDetailRes> dataProductPortAssets) {
        if (!CollectionUtils.isEmpty(dataProductPortAssets)) {
            BDProductPortAssetsRes bdDataProductAssets = new BDProductPortAssetsRes();
            bdDataProductAssets.setPorts(dataProductPortAssets);
            blindataOutboundPort.createDataProductAssets(bdDataProductAssets);
            getUseCaseLogger().info(String.format("%s Data product: %s, uploaded %s assets on Blindata.", USE_CASE_PREFIX, odmOutboundPort.getDataProductVersion().getInfo().getFullyQualifiedName(), dataProductPortAssets.size()));
        }
    }

    private BDDataProductPortRes odmToBlindataDataProductPort(PortDPDS odmDataProductPort, String entityType) {
        BDDataProductPortRes port = new BDDataProductPortRes();
        if (!StringUtils.hasText(odmDataProductPort.getFullyQualifiedName())) {
            getUseCaseLogger().warn(" Missing identifier on data product port.");
        }
        port.setIdentifier(odmDataProductPort.getFullyQualifiedName());

        if (!StringUtils.hasText(odmDataProductPort.getName())) {
            getUseCaseLogger().warn(USE_CASE_PREFIX + " Missing name on data product port.");
        }
        port.setName(odmDataProductPort.getName());
        port.setDisplayName(odmDataProductPort.getDisplayName());

        if (!StringUtils.hasText(odmDataProductPort.getVersion())) {
            getUseCaseLogger().warn(USE_CASE_PREFIX + " Missing version on data product port.");
        }
        port.setVersion(odmDataProductPort.getVersion());
        port.setDescription(odmDataProductPort.getDescription());

        if (!StringUtils.hasText(entityType)) {
            getUseCaseLogger().warn(USE_CASE_PREFIX + " Missing type on data product port.");
        }
        port.setEntityType(entityType);

        if (odmDataProductPort.getPromises() != null) {
            port.setServicesType(odmDataProductPort.getPromises().getServicesType());
            port.setAdditionalProperties(extractAdditionalProperties(odmDataProductPort.getPromises()));
        }
        addDependsOnIfPresent(odmDataProductPort, port);
        return port;

    }

    private void addDependsOnIfPresent(PortDPDS odmDataProductPort, BDDataProductPortRes port) {
        if (!StringUtils.hasText(odmDataProductPort.getRawContent())) {
            return;
        }
        try {
            JsonNode portRawContent = new ObjectMapper().readValue(odmDataProductPort.getRawContent(), JsonNode.class);
            JsonNode xDependsOnNode = portRawContent.get("x-dependsOn");
            JsonNode dependsOnNode = portRawContent.get("dependsOn");
            String xDependsOn = Optional.ofNullable(xDependsOnNode).map(JsonNode::asText).orElse(null);
            String dependsOn = Optional.ofNullable(dependsOnNode).map(JsonNode::asText).orElse(null);
            if (xDependsOn != null && dependsOn != null) {
                getUseCaseLogger().warn(String.format("%s: Both 'x-dependsOn' and 'dependsOn' are present. 'dependsOn' will be used.", USE_CASE_PREFIX));
            }
            // Prioritize 'dependsOn' if it exists; otherwise, use 'x-dependsOn'
            String portDependency = dependsOn != null ? dependsOn : xDependsOn;
            if (StringUtils.hasText(portDependency)) {
                blindataOutboundPort.getSystemDependency(portDependency)
                        .ifPresentOrElse(
                                port::setDependsOnSystem,
                                () -> port.setDependsOnIdentifier(portDependency)
                        );
            }
        } catch (JsonProcessingException e) {
            getUseCaseLogger().warn(String.format("%s: %s", USE_CASE_PREFIX, e.getMessage()), e);
        }
    }

    private List<AdditionalPropertiesRes> extractAdditionalProperties(PromisesDPDS promises) {
        List<AdditionalPropertiesRes> additionalPropertiesRes = new ArrayList<>();
        if (promises.getSlo() != null && StringUtils.hasText(promises.getSlo().getDescription())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("sloDescription", promises.getSlo().getDescription()));
        }
        if (promises.getDeprecationPolicy() != null && StringUtils.hasText(promises.getDeprecationPolicy().getDescription())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("deprecationPolicy", promises.getDeprecationPolicy().getDescription()));
        }
        return additionalPropertiesRes;
    }

    private List<BDDataProductPortAssetDetailRes> extractBdAssetsFromDpPorts(InterfaceComponentsDPDS interfaceComponentsDPDS) {
        List<BDDataProductPortAssetDetailRes> dataProductPortAssets = new ArrayList<>();
        Optional.ofNullable(interfaceComponentsDPDS.getInputPorts())
                .map(odmOutboundPort::extractBDAssetsFromPorts)
                .ifPresent(dataProductPortAssets::addAll);

        Optional.ofNullable(interfaceComponentsDPDS.getOutputPorts())
                .map(odmOutboundPort::extractBDAssetsFromPorts)
                .ifPresent(dataProductPortAssets::addAll);

        Optional.ofNullable(interfaceComponentsDPDS.getControlPorts())
                .map(odmOutboundPort::extractBDAssetsFromPorts)
                .ifPresent(dataProductPortAssets::addAll);

        Optional.ofNullable(interfaceComponentsDPDS.getDiscoveryPorts())
                .map(odmOutboundPort::extractBDAssetsFromPorts)
                .ifPresent(dataProductPortAssets::addAll);

        Optional.ofNullable(interfaceComponentsDPDS.getObservabilityPorts())
                .map(odmOutboundPort::extractBDAssetsFromPorts)
                .ifPresent(dataProductPortAssets::addAll);
        return dataProductPortAssets;
    }

    private void withErrorHandling(Runnable runnable) throws UseCaseExecutionException {
        try {
            runnable.run();
        } catch (BlindataClientException e) {
            if (e.getCode() != HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                throw e;
            } else {
                getUseCaseLogger().warn(e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new UseCaseExecutionException(e.getMessage(), e);
        }
    }
}
