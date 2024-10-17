package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductversion_upload;

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
class DataProductVersionUpload implements UseCase {

    private final String USE_CASE_PREFIX = "[DataProductVersionUpload]";

    private final DataProductVersionUploadBlindataOutputPort blindataOutputPort;
    private final DataProductVersionUploadOdmOutputPort odmOutputPort;

    public DataProductVersionUpload(DataProductVersionUploadBlindataOutputPort blindataOutputPort, DataProductVersionUploadOdmOutputPort odmOutputPort) {
        this.blindataOutputPort = blindataOutputPort;
        this.odmOutputPort = odmOutputPort;
    }

    @Override
    public void execute() throws UseCaseExecutionException {
        try {
            InterfaceComponentsDPDS interfaceComponentsDPDS = odmOutputPort.getDataProductVersion().getInterfaceComponents();
            if (interfaceComponentsDPDS == null) {
                log.warn("{} Missing interface components on data product: {}.", USE_CASE_PREFIX, odmOutputPort.getDataProductVersion().getInfo().getFullyQualifiedName());
                return;
            }
            Optional<BDDataProductRes> existentDataProduct = blindataOutputPort.findDataProduct(odmOutputPort.getDataProductVersion().getInfo().getFullyQualifiedName());
            if (existentDataProduct.isEmpty()) {
                log.warn("{} Data product: {} has not been created yet on Blindata.", USE_CASE_PREFIX, odmOutputPort.getDataProductVersion().getInfo().getFullyQualifiedName());
                return;
            }
            List<BDDataProductPortRes> bdDataProductPorts = extractBdDataProductPorts(interfaceComponentsDPDS);
            log.info("{} Data product: {}, Blindata ports found: {}.", USE_CASE_PREFIX, existentDataProduct.get().getIdentifier(), bdDataProductPorts.size());
            existentDataProduct.get().setPorts(bdDataProductPorts);
            blindataOutputPort.updateDataProductPorts(existentDataProduct.get());
            log.info("{} Data product: {}, updated ports on Blindata.", USE_CASE_PREFIX, existentDataProduct.get().getIdentifier());

            List<BDDataProductPortAssetDetailRes> dataProductPortAssets = extractBdAssetsFromDpPorts(interfaceComponentsDPDS);
            log.info("{} Data product: {}, found {} data assets.", USE_CASE_PREFIX, existentDataProduct.get().getIdentifier(), dataProductPortAssets.size());
            uploadAssetsOnBlindata(dataProductPortAssets);

            List<BDDataProductStageRes> stages = odmOutputPort.extractDataProductStages();
            log.info("{} Data product: {}, found {} stages.", USE_CASE_PREFIX, existentDataProduct.get().getIdentifier(), stages.size());
            blindataOutputPort.uploadDataProductStages(existentDataProduct.get().getUuid(), stages);

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
            blindataOutputPort.createDataProductAssets(bdDataProductAssets);
            log.info("{} Data product: {}, uploaded assets on Blindata.", USE_CASE_PREFIX, odmOutputPort.getDataProductVersion().getInfo().getFullyQualifiedName(), dataProductPortAssets.size());
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

        return port;

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
                .map(odmOutputPort::extractBDAssetsFromPorts)
                .ifPresent(dataProductPortAssets::addAll);

        Optional.ofNullable(interfaceComponentsDPDS.getOutputPorts())
                .map(odmOutputPort::extractBDAssetsFromPorts)
                .ifPresent(dataProductPortAssets::addAll);

        Optional.ofNullable(interfaceComponentsDPDS.getControlPorts())
                .map(odmOutputPort::extractBDAssetsFromPorts)
                .ifPresent(dataProductPortAssets::addAll);

        Optional.ofNullable(interfaceComponentsDPDS.getDiscoveryPorts())
                .map(odmOutputPort::extractBDAssetsFromPorts)
                .ifPresent(dataProductPortAssets::addAll);

        Optional.ofNullable(interfaceComponentsDPDS.getObservabilityPorts())
                .map(odmOutputPort::extractBDAssetsFromPorts)
                .ifPresent(dataProductPortAssets::addAll);
        return dataProductPortAssets;
    }
}
