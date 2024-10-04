package org.opendatamesh.platform.up.metaservice.blindata.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.platform.pp.registry.api.resources.ExternalComponentResource;
import org.opendatamesh.platform.up.metaservice.blindata.client.odm.OdmRegistryClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductPortAssetDetailRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDPhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDProductPortAssetSystemRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDSystemRes;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.PortStandardDefinition;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.PortStandardDefinitionAnalyzer;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.utils.InternalRefResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DataProductPortAssetAnalyzer {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private OdmRegistryClient registryClient;

    @Value("${blindata.systemNameRegex}")
    private String systemNameRegex;
    @Value("${blindata.systemTechnologyRegex}")
    private String systemTechnologyRegex;

    @Autowired
    private List<PortStandardDefinitionAnalyzer> portStandardDefinitionAnalyzers;


    private static final Logger logger = LoggerFactory.getLogger(DataProductPortAssetAnalyzer.class);


    public List<BDDataProductPortAssetDetailRes> extractPhysicalResourcesFromPorts(List<PortDPDS> portDPDS) {
        List<BDDataProductPortAssetDetailRes> dataProductPortAssetDetailRes = new ArrayList<>();
        try {
            for (PortDPDS port : portDPDS) {
                logger.info("Analyzing data assets for port {}", port.getFullyQualifiedName());
                if (port.getPromises() == null || port.getPromises().getApi() == null) {
                    logger.info("Data product port: {} has empty Api", port.getFullyQualifiedName());
                    continue;
                }

                Optional<ExternalComponentResource> externalComponentResources = registryClient.getApi(port.getPromises().getApi().getName(), port.getPromises().getApi().getVersion()).stream().findFirst();
                if (externalComponentResources.isEmpty()) {
                    logger.info("No definition found for the data product port: {}", port.getFullyQualifiedName());
                    continue;
                }

                PortStandardDefinition portStandardDefinition = externalComponentToPortStandardDefinition(externalComponentResources.get());

                if (portIsNotValid(port, portStandardDefinition)) continue;

                logger.info("Data product port: {} has specification: {} of version: {}", port.getFullyQualifiedName(), portStandardDefinition.getSpecification(), portStandardDefinition.getSpecificationVersion());
                BDSystemRes platformSystem = getSystem(port);

                Optional<PortStandardDefinitionAnalyzer> portStandardDefinitionAnalyzer = getPortStandardDefinitionAnalyzer(portStandardDefinition);
                if (portStandardDefinitionAnalyzer.isEmpty()) {
                    logger.warn("Data product port: {} with specification: {} and version: {} is not supported.", port.getFullyQualifiedName(), portStandardDefinition.getSpecification(), portStandardDefinition.getSpecificationVersion());
                    continue;
                }

                List<BDPhysicalEntityRes> extractedEntities = portStandardDefinitionAnalyzer.get().getBDAssetsFromPortStandardDefinition(portStandardDefinition);
                logger.info("Data product port: {}, found {} physical entities", port.getFullyQualifiedName(), extractedEntities.size());
                //forcing physical entities to have the platform system
                extractedEntities = extractedEntities.stream().map(pe -> {
                    if (CollectionUtils.isEmpty(pe.getPhysicalFields())) {
                        logger.info("Data product port: {}, physical entity: {}, no physical fields found", port.getFullyQualifiedName(), pe.getName());
                    } else {
                        logger.info("Data product port: {}, physical entity: {}, found {} physical fields", port.getFullyQualifiedName(), pe.getName(), pe.getPhysicalFields().size());
                    }
                    pe.setSystem(platformSystem);
                    return pe;
                }).collect(Collectors.toList());

                //IDK why, just supporting backward compatibility
                BDDataProductPortAssetDetailRes bdDataProductPortAssetDetail = buildDataProductPortAssetDetail(port, platformSystem, extractedEntities);
                dataProductPortAssetDetailRes.add(bdDataProductPortAssetDetail);
            }
            return dataProductPortAssetDetailRes;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return dataProductPortAssetDetailRes;
        }
    }

    private boolean portIsNotValid(PortDPDS port, PortStandardDefinition portStandardDefinition) {
        if (!StringUtils.hasText(portStandardDefinition.getSpecification())) {
            logger.warn("Missing specification on port: {}", port.getFullyQualifiedName());
            return true;
        }
        if (!StringUtils.hasText(portStandardDefinition.getSpecificationVersion())) {
            logger.warn("Missing specification version on port: {}", port.getFullyQualifiedName());
            return true;
        }
        return false;
    }

    private BDDataProductPortAssetDetailRes buildDataProductPortAssetDetail(PortDPDS port, BDSystemRes platformSystem, List<BDPhysicalEntityRes> extractedEntities) {
        BDProductPortAssetSystemRes bdDataProductPortAssetSystem = new BDProductPortAssetSystemRes();
        bdDataProductPortAssetSystem.setSystem(platformSystem);
        bdDataProductPortAssetSystem.setPhysicalEntities(extractedEntities);

        BDDataProductPortAssetDetailRes bdDataProductPortAssetDetail = new BDDataProductPortAssetDetailRes();
        bdDataProductPortAssetDetail.setPortIdentifier(port.getFullyQualifiedName());
        bdDataProductPortAssetDetail.setAssets(Lists.newArrayList(bdDataProductPortAssetSystem));
        return bdDataProductPortAssetDetail;
    }

    private PortStandardDefinition externalComponentToPortStandardDefinition(ExternalComponentResource externalComponentResources) {
        PortStandardDefinition portStandardDefinition = new PortStandardDefinition();
        portStandardDefinition.setSpecification(externalComponentResources.getSpecification());
        portStandardDefinition.setSpecificationVersion(externalComponentResources.getSpecificationVersion());
        try {
            JsonNode rootNode = objectMapper.readTree(externalComponentResources.getDefinition());
            InternalRefResolver.resolveRefs(rootNode, rootNode);
            portStandardDefinition.setDefinition(objectMapper.writeValueAsString(rootNode));
        } catch (JsonProcessingException e) {
            logger.warn("Failed to parse the JSON of port standard definition schema: {}", externalComponentResources.getId());
        }
        return portStandardDefinition;
    }

    private Optional<PortStandardDefinitionAnalyzer> getPortStandardDefinitionAnalyzer(PortStandardDefinition portStandardDefinition) {
        return this.portStandardDefinitionAnalyzers.stream()
                .filter(portStandardDefinitionAnalyzer -> portStandardDefinitionAnalyzer.supportsPortStandardDefinition(portStandardDefinition))
                .findFirst();
    }

    private BDSystemRes getSystem(PortDPDS port) {
        BDSystemRes systemRes = new BDSystemRes();
        String platform = port.getPromises().getPlatform();
        systemRes.setName(extractSystemName(platform));
        systemRes.setTechnology(extractSystemTechnology(platform));
        return systemRes;
    }

    private String extractSystemTechnology(String platform) {
        if (StringUtils.hasText(systemTechnologyRegex)) {
            Pattern pattern = Pattern.compile(systemTechnologyRegex);
            Matcher matcher = pattern.matcher(platform);
            if (matcher.find()) {
                return matcher.group(0);
            }
        }
        return null;
    }

    private String extractSystemName(String platform) {
        if (StringUtils.hasText(systemNameRegex)) {
            Pattern pattern = Pattern.compile(systemNameRegex);
            Matcher matcher = pattern.matcher(platform);
            if (matcher.find()) {
                return matcher.group(0);
            }
        }
        return platform;
    }

}
