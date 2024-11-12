package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.opendatamesh.dpds.model.interfaces.InterfaceComponentsDPDS;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.dpds.model.interfaces.PromisesDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.*;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
class DataProductPortsAndAssetsUpload implements UseCase {

    private static final String USE_CASE_PREFIX = "[DataProductVersionUpload]";

    private final DataProductPortsAndAssetsUploadBlindataOutboundPort blindataOutboundPort;
    private final DataProductPortsAndAssetsUploadOdmOutboundPort odmOutboundPort;

    public DataProductPortsAndAssetsUpload(DataProductPortsAndAssetsUploadBlindataOutboundPort blindataOutboundPort, DataProductPortsAndAssetsUploadOdmOutboundPort odmOutboundPort) {
        this.blindataOutboundPort = blindataOutboundPort;
        this.odmOutboundPort = odmOutboundPort;
    }

    @Override
    public void execute() throws UseCaseExecutionException {
        try {
            InterfaceComponentsDPDS interfaceComponentsDPDS = odmOutboundPort.getDataProductVersion().getInterfaceComponents();
            if (interfaceComponentsDPDS == null) {
                log.warn("{} Missing interface components on data product: {}.", USE_CASE_PREFIX, odmOutboundPort.getDataProductVersion().getInfo().getFullyQualifiedName());
                return;
            }
            Optional<BDDataProductRes> existentDataProduct = blindataOutboundPort.findDataProduct(odmOutboundPort.getDataProductVersion().getInfo().getFullyQualifiedName());
            if (existentDataProduct.isEmpty()) {
                log.warn("{} Data product: {} has not been created yet on Blindata.", USE_CASE_PREFIX, odmOutboundPort.getDataProductVersion().getInfo().getFullyQualifiedName());
                return;
            }
            List<BDDataProductPortRes> bdDataProductPorts = extractBdDataProductPorts(interfaceComponentsDPDS);
            log.info("{} Data product: {}, Blindata ports found: {}.", USE_CASE_PREFIX, existentDataProduct.get().getIdentifier(), bdDataProductPorts.size());
            existentDataProduct.get().setPorts(bdDataProductPorts);
            blindataOutboundPort.updateDataProductPorts(existentDataProduct.get());
            log.info("{} Data product: {}, updated ports on Blindata.", USE_CASE_PREFIX, existentDataProduct.get().getIdentifier());

            List<BDDataProductPortAssetDetailRes> dataProductPortAssets = extractBdAssetsFromDpPorts(interfaceComponentsDPDS);
            log.info("{} Data product: {}, found {} data assets.", USE_CASE_PREFIX, existentDataProduct.get().getIdentifier(), dataProductPortAssets.size());
            uploadAssetsOnBlindata(dataProductPortAssets);

        } catch (Exception e) {
            throw new UseCaseExecutionException(e.getMessage(), e);
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
            log.info("{} Data product: {}, uploaded {} assets on Blindata.", USE_CASE_PREFIX, odmOutboundPort.getDataProductVersion().getInfo().getFullyQualifiedName(), dataProductPortAssets.size());
        }
    }

    private BDDataProductPortRes odmToBlindataDataProductPort(PortDPDS odmDataProductPort, String entityType) {
        BDDataProductPortRes port = new BDDataProductPortRes();
        port.setDisplayName(odmDataProductPort.getDisplayName());
        port.setDescription(odmDataProductPort.getDescription());
        port.setName(odmDataProductPort.getName());
        port.setIdentifier(odmDataProductPort.getFullyQualifiedName());
        port.setDisplayName(odmDataProductPort.getDisplayName());
        port.setEntityType(entityType);
        port.setVersion(odmDataProductPort.getVersion());
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
            JsonNode dependsOnNode = portRawContent.get("x-dependsOn");
            String dependsOn = Optional.ofNullable(dependsOnNode).map(JsonNode::asText).orElse(null);
            port.setDependsOnIdentifier(dependsOn);
        } catch (JsonProcessingException e) {
            log.warn("{}: {}", USE_CASE_PREFIX, e.getMessage(), e);
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
}
