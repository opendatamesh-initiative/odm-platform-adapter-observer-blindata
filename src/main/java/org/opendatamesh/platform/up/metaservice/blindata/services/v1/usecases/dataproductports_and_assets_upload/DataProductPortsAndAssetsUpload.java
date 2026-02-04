package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.dataproductports_and_assets_upload;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.dpds.model.info.Info;
import org.opendatamesh.dpds.model.interfaces.InterfaceComponents;
import org.opendatamesh.dpds.model.interfaces.Port;
import org.opendatamesh.dpds.model.interfaces.Promises;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.exceptions.BlindataClientException;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDAdditionalPropertiesRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalFieldRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDSystemRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.*;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.exceptions.UseCaseExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;

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
            InterfaceComponents interfaceComponents = odmOutboundPort.getDataProductVersion().getInterfaceComponents();
            validateDataProductDescriptorPorts(interfaceComponents);

            Optional<BDDataProductRes> existentDataProduct = blindataOutboundPort.findDataProduct(odmOutboundPort.getDataProductVersion().getInfo().getFullyQualifiedName());
            if (existentDataProduct.isEmpty()) {
                getUseCaseLogger().warn(String.format("%s Data product: %s has not been created yet on Blindata.", USE_CASE_PREFIX, odmOutboundPort.getDataProductVersion().getInfo().getFullyQualifiedName()));
                return;
            }
            List<BDDataProductPortRes> bdDataProductPorts = extractBdDataProductPorts(interfaceComponents);
            getUseCaseLogger().info(String.format("%s Data product: %s, Blindata ports found: %s.", USE_CASE_PREFIX, existentDataProduct.get().getIdentifier(), bdDataProductPorts.size()));
            existentDataProduct.get().setPorts(bdDataProductPorts);
            blindataOutboundPort.updateDataProductPorts(existentDataProduct.get());
            getUseCaseLogger().info(String.format("%s Data product: %s, updated ports on Blindata.", USE_CASE_PREFIX, existentDataProduct.get().getIdentifier()));

            String versionNumber = Optional.ofNullable(odmOutboundPort.getDataProductVersion().getInfo()).map(Info::getVersion).orElse(null);
            if (StringUtils.hasText(versionNumber)) {
                getUseCaseLogger().info(USE_CASE_PREFIX + " Updating data product version number on Blindata. Version: " + versionNumber);
                existentDataProduct.get().setVersion(versionNumber);
                blindataOutboundPort.updateDataProduct(existentDataProduct.get());
            } else {
                getUseCaseLogger().warn(USE_CASE_PREFIX + " Impossible to retrieve Data Product version number from Odm Data Product Version.");
            }


            getUseCaseLogger().info(USE_CASE_PREFIX + " Extracting data product ports assets");
            List<BDDataProductPortAssetDetailRes> dataProductPortAssets = extractBdAssetsFromDpPorts(interfaceComponents);
            getUseCaseLogger().info(String.format("%s Data product: %s, found %s ports to update assets.", USE_CASE_PREFIX, existentDataProduct.get().getIdentifier(), dataProductPortAssets.size()));

            getUseCaseLogger().info(USE_CASE_PREFIX + " Validating data product ports assets");
            validateDataProductPortsAssets(dataProductPortAssets);
            getUseCaseLogger().info(USE_CASE_PREFIX + " Validating data product ports assets completed");

            getUseCaseLogger().info(USE_CASE_PREFIX + " Uploading of data product ports assets");
            uploadAssetsOnBlindata(dataProductPortAssets);
            getUseCaseLogger().info(USE_CASE_PREFIX + " Uploading of data product ports assets completed");
        });
    }

    private void validateDataProductDescriptorPorts(InterfaceComponents interfaceComponents) {
        if (interfaceComponents == null ||
                (CollectionUtils.isEmpty(interfaceComponents.getControlPorts()) &&
                        CollectionUtils.isEmpty(interfaceComponents.getOutputPorts()) &&
                        CollectionUtils.isEmpty(interfaceComponents.getObservabilityPorts()) &&
                        CollectionUtils.isEmpty(interfaceComponents.getInputPorts()) &&
                        CollectionUtils.isEmpty(interfaceComponents.getDiscoveryPorts()))
        ) {
            getUseCaseLogger().warn(String.format("%s Missing interface components on data product: %s", USE_CASE_PREFIX, odmOutboundPort.getDataProductVersion().getInfo().getFullyQualifiedName()));
        }

    }

    private void validateDataProductPortsAssets(List<BDDataProductPortAssetDetailRes> dataProductPortAssetsDetails) {
        for (BDDataProductPortAssetDetailRes dataProductPortAssetsDetail : dataProductPortAssetsDetails) {
            if (!StringUtils.hasText(dataProductPortAssetsDetail.getPortIdentifier())) {
                getUseCaseLogger().warn(USE_CASE_PREFIX + " Missing port identifier on data product port asset");
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

    private List<BDDataProductPortRes> extractBdDataProductPorts(InterfaceComponents interfaceComponents) {
        List<BDDataProductPortRes> bdDataProductPorts = new ArrayList<>();
        interfaceComponents.getInputPorts().forEach(portResource -> bdDataProductPorts.add(odmToBlindataDataProductPort(portResource, "inputPorts")));
        interfaceComponents.getOutputPorts().forEach(portResource -> bdDataProductPorts.add(odmToBlindataDataProductPort(portResource, "outputPorts")));
        interfaceComponents.getObservabilityPorts().forEach(portResource -> bdDataProductPorts.add(odmToBlindataDataProductPort(portResource, "observabilityPorts")));
        interfaceComponents.getDiscoveryPorts().forEach(portResource -> bdDataProductPorts.add(odmToBlindataDataProductPort(portResource, "discoveryPorts")));
        interfaceComponents.getControlPorts().forEach(portResource -> bdDataProductPorts.add(odmToBlindataDataProductPort(portResource, "controlPorts")));
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

    private BDDataProductPortRes odmToBlindataDataProductPort(Port odmDataProductPort, String entityType) {
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
        handleDataProductAdditionalProperties(odmDataProductPort, port);
        return port;

    }

    private void handleDataProductAdditionalProperties(Port odmPort, BDDataProductPortRes bdPort) {
        String addPropRegex = blindataOutboundPort.getDataProductAdditionalPropertiesRegex();
        if (CollectionUtils.isEmpty(bdPort.getAdditionalProperties())) {
            bdPort.setAdditionalProperties(new ArrayList<>());
        }
        try {
            Pattern compiledPattern = Pattern.compile(addPropRegex);
            if (!CollectionUtils.isEmpty(odmPort.getAdditionalProperties()) && StringUtils.hasText(addPropRegex)) {
                odmPort.getAdditionalProperties().forEach((key, value) -> {
                    Matcher matcher = compiledPattern.matcher(key);
                    if (matcher.find()) {
                        String propName = matcher.group(1);
                        addAdditionalPropertyValue(bdPort.getAdditionalProperties(), propName, value);
                    }
                });
            }
        } catch (PatternSyntaxException e) {
            getUseCaseLogger().warn("Invalid regex for additional properties: " + addPropRegex, e);
        }
    }

    /**
     * Utility method to extract additional properties from JsonNode values, handling both single values and arrays
     *
     * @param additionalProperties the list to add the extracted properties to
     * @param propName             the name of the property
     * @param value                the JsonNode value to extract from
     */
    private void addAdditionalPropertyValue(List<BDAdditionalPropertiesRes> additionalProperties, String propName, JsonNode value) {
        if (value.isArray()) {
            // Create a separate additional property for each array element
            value.forEach(element -> {
                String elementValue = element.isTextual() ? element.asText() : element.toString();
                additionalProperties.add(new BDAdditionalPropertiesRes(propName, elementValue));
            });
        } else {
            // Handle single values as before
            additionalProperties.add(new BDAdditionalPropertiesRes(
                    propName,
                    value.isTextual() ? value.asText() : value.toString()
            ));
        }
    }

    private void addDependsOnIfPresent(Port odmDataProductPort, BDDataProductPortRes port) {
        JsonNode xDependsOnNode = odmDataProductPort.getAdditionalProperties().get("x-dependsOn");
        JsonNode dependsOnNode = odmDataProductPort.getAdditionalProperties().get("dependsOn");
        String xDependsOn = Optional.ofNullable(xDependsOnNode).map(JsonNode::asText).orElse(null);
        String dependsOn = Optional.ofNullable(dependsOnNode).map(JsonNode::asText).orElse(null);
        if (xDependsOn != null && dependsOn != null) {
            getUseCaseLogger().warn(String.format("%s: Both 'x-dependsOn' and 'dependsOn' are present. 'dependsOn' will be used.", USE_CASE_PREFIX));
        }
        // Prioritize 'dependsOn' if it exists; otherwise, use 'x-dependsOn'
        String portDependency = dependsOn != null ? dependsOn : xDependsOn;
        if (StringUtils.hasText(portDependency)) {
            Optional<String> systemName = blindataOutboundPort.findSystemName(portDependency);
            if (!systemName.isEmpty()) {
                Optional<BDSystemRes> system = blindataOutboundPort.findExistingSystem(systemName.get());
                if (system.isPresent()) {
                    port.setDependsOnSystem(system.get());
                } else {
                    getUseCaseLogger().warn(String.format("%s: System: %s not found in Blindata.", USE_CASE_PREFIX, systemName.get()));
                    BDSystemRes systemRes = new BDSystemRes();
                    systemRes.setName(systemName.get());
                    port.setDependsOnSystem(systemRes);
                }
            } else {
                port.setDependsOnIdentifier(portDependency);
            }
        }
    }

    private List<BDAdditionalPropertiesRes> extractAdditionalProperties(Promises promises) {
        List<BDAdditionalPropertiesRes> additionalPropertiesRes = new ArrayList<>();
        if (promises.getSlo() != null && StringUtils.hasText(promises.getSlo().getDescription())) {
            additionalPropertiesRes.add(new BDAdditionalPropertiesRes("sloDescription", promises.getSlo().getDescription()));
        }
        if (promises.getDeprecationPolicy() != null && StringUtils.hasText(promises.getDeprecationPolicy().getDescription())) {
            additionalPropertiesRes.add(new BDAdditionalPropertiesRes("deprecationPolicy", promises.getDeprecationPolicy().getDescription()));
        }
        return additionalPropertiesRes;
    }

    private List<BDDataProductPortAssetDetailRes> extractBdAssetsFromDpPorts(InterfaceComponents interfaceComponents) {
        List<BDDataProductPortAssetDetailRes> dataProductPortAssets = new ArrayList<>();
        Optional.ofNullable(interfaceComponents.getInputPorts())
                .map(odmOutboundPort::extractBDAssetsFromPorts)
                .ifPresent(dataProductPortAssets::addAll);

        Optional.ofNullable(interfaceComponents.getOutputPorts())
                .map(odmOutboundPort::extractBDAssetsFromPorts)
                .ifPresent(dataProductPortAssets::addAll);

        Optional.ofNullable(interfaceComponents.getControlPorts())
                .map(odmOutboundPort::extractBDAssetsFromPorts)
                .ifPresent(dataProductPortAssets::addAll);

        Optional.ofNullable(interfaceComponents.getDiscoveryPorts())
                .map(odmOutboundPort::extractBDAssetsFromPorts)
                .ifPresent(dataProductPortAssets::addAll);

        Optional.ofNullable(interfaceComponents.getObservabilityPorts())
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