package org.opendatamesh.platform.up.metaservice.blindata.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.opendatamesh.dpds.model.core.StandardDefinition;
import org.opendatamesh.dpds.model.interfaces.Port;
import org.opendatamesh.platform.up.metaservice.blindata.client.odm.OdmRegistryClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalFieldRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDSystemRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductPortAssetDetailRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDProductPortAssetSystemRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.PortStandardDefinitionEntitiesExtractor;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.PortStandardDefinitionQualityExtractor;
import org.opendatamesh.platform.up.metaservice.blindata.utils.CollectionsBDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
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
    private List<PortStandardDefinitionEntitiesExtractor> entitiesExtractors;

    @Autowired
    private List<PortStandardDefinitionQualityExtractor> qualityChecksExtractors;

    private static final Logger logger = LoggerFactory.getLogger(DataProductPortAssetAnalyzer.class);

    public List<QualityCheck> extractQualityChecksFromPorts(List<Port> ports) {
        List<QualityCheck> qualityChecks = new ArrayList<>();
        try {
            for (Port port : ports) {
                logger.info("Analyzing quality definition for port {}", port.getFullyQualifiedName());
                //Get StandardDefinition from port
                Optional<StandardDefinition> standardDefinition = resolveStandardDefinitionFromPort(port);
                if (standardDefinition.isEmpty()) continue;

                //Retrieve a qualityExtractor that can handle the definition specification
                Optional<PortStandardDefinitionQualityExtractor> portQualityExtractor = this.qualityChecksExtractors.stream()
                        .filter(extractor -> extractor.supports(standardDefinition.get()))
                        .findFirst();
                if (portQualityExtractor.isEmpty()) {
                    logger.warn("Quality extraction for data product port: {} with specification: {} and version: {} is not supported.",
                            port.getFullyQualifiedName(), standardDefinition.get().getSpecification(), standardDefinition.get().getSpecificationVersion());
                    continue;
                }

                //Extract quality checks from the definition using the extractor
                List<QualityCheck> extractedQualityChecks = portQualityExtractor.get().extractQualityChecks(standardDefinition.get());

                addSystemToLinkedPhysicalEntitiesAndFields(port, extractedQualityChecks);
                qualityChecks.addAll(extractedQualityChecks);
            }

            return mergeQualityChecksWithSameCode(qualityChecks);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return qualityChecks;
    }

    public List<BDDataProductPortAssetDetailRes> extractPhysicalResourcesFromPorts(List<Port> ports) {
        List<BDDataProductPortAssetDetailRes> dataProductPortAssetDetailRes = new ArrayList<>();
        try {
            for (Port port : ports) {
                logger.info("Analyzing data assets for port {}", port.getFullyQualifiedName());
                Optional<StandardDefinition> standardDefinition = resolveStandardDefinitionFromPort(port);
                if (standardDefinition.isEmpty()) continue;
                logger.info("Data product port: {} has specification: {} of version: {}", port.getFullyQualifiedName(), standardDefinition.get().getSpecification(), standardDefinition.get().getSpecificationVersion());

                BDSystemRes platformSystem = getSystem(port);

                Optional<PortStandardDefinitionEntitiesExtractor> portStandardDefinitionAnalyzer = getPortStandardDefinitionAnalyzer(standardDefinition.get());
                if (portStandardDefinitionAnalyzer.isEmpty()) {
                    logger.warn("Data product port: {} with specification: {} and version: {} is not supported.",
                            port.getFullyQualifiedName(), standardDefinition.get().getSpecification(), standardDefinition.get().getSpecificationVersion());
                    continue;
                }

                List<BDPhysicalEntityRes> extractedEntities = portStandardDefinitionAnalyzer.get().extractEntities(standardDefinition.get());
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

                BDDataProductPortAssetDetailRes bdDataProductPortAssetDetail = buildDataProductPortAssetDetail(port, platformSystem, extractedEntities);
                dataProductPortAssetDetailRes.add(bdDataProductPortAssetDetail);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return dataProductPortAssetDetailRes;
    }

    private Optional<StandardDefinition> resolveStandardDefinitionFromPort(Port port) {
        if (port.getPromises() == null || port.getPromises().getApi() == null) {
            logger.info("Data product port: {} has empty Api", port.getFullyQualifiedName());
            return Optional.empty();
        }

        Optional<JsonNode> externalComponentResources = registryClient.getApi(port.getPromises().getApi().getId());
        if (externalComponentResources.isEmpty()) {
            logger.info("No definition found for the data product port: {}", port.getFullyQualifiedName());
            return Optional.empty();
        }

        try {
            StandardDefinition standardDefinition = objectMapper.treeToValue(externalComponentResources.get(), StandardDefinition.class);
            if (portIsNotValid(port, standardDefinition)) {
                return Optional.empty();
            }
            return Optional.of(standardDefinition);
        } catch (Exception e) {
            logger.warn("Failed to parse standard definition for port {}: {}", port.getFullyQualifiedName(), e.getMessage());
            return Optional.empty();
        }
    }


    private boolean portIsNotValid(Port port, StandardDefinition standardDefinition) {
        if (!StringUtils.hasText(standardDefinition.getSpecification())) {
            logger.warn("Missing specification on port: {}", port.getFullyQualifiedName());
            return true;
        }
        if (!StringUtils.hasText(standardDefinition.getSpecificationVersion())) {
            logger.warn("Missing specification version on port: {}", port.getFullyQualifiedName());
            return true;
        }
        return false;
    }

    private BDDataProductPortAssetDetailRes buildDataProductPortAssetDetail(Port port, BDSystemRes platformSystem, List<BDPhysicalEntityRes> extractedEntities) {
        BDProductPortAssetSystemRes bdDataProductPortAssetSystem = new BDProductPortAssetSystemRes();
        bdDataProductPortAssetSystem.setSystem(platformSystem);
        bdDataProductPortAssetSystem.setPhysicalEntities(extractedEntities);

        BDDataProductPortAssetDetailRes bdDataProductPortAssetDetail = new BDDataProductPortAssetDetailRes();
        bdDataProductPortAssetDetail.setPortIdentifier(port.getFullyQualifiedName());
        bdDataProductPortAssetDetail.setAssets(Lists.newArrayList(bdDataProductPortAssetSystem));
        return bdDataProductPortAssetDetail;
    }

    private Optional<PortStandardDefinitionEntitiesExtractor> getPortStandardDefinitionAnalyzer(StandardDefinition portStandardDefinition) {
        return this.entitiesExtractors.stream()
                .filter(portStandardDefinitionAnalyzer -> portStandardDefinitionAnalyzer.supports(portStandardDefinition))
                .findFirst();
    }

    private BDSystemRes getSystem(Port port) {
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

    private List<QualityCheck> mergeQualityChecksWithSameCode(List<QualityCheck> extractedQualityChecks) {
        Map<String, QualityCheck> mergedQualityChecks = new HashMap<>();
        extractedQualityChecks.forEach(qualityCheck -> {
            if (mergedQualityChecks.containsKey(qualityCheck.getCode())) {
                QualityCheck encounteredQualityCheck = mergedQualityChecks.get(qualityCheck.getCode());

                //Merging physical entities and physical fields
                List<BDPhysicalEntityRes> physicalEntities = encounteredQualityCheck.getPhysicalEntities();
                physicalEntities.addAll(qualityCheck.getPhysicalEntities());
                List<BDPhysicalFieldRes> physicalFields = encounteredQualityCheck.getPhysicalFields();
                physicalFields.addAll(qualityCheck.getPhysicalFields());

                //Removing duplicates from physical fields and physical entities
                List<BDPhysicalEntityRes> distinctPhysicalEntities = physicalEntities.stream().filter(CollectionsBDUtils.distinctByKey((pe) -> pe.getSchema() + pe.getName()))
                        .collect(Collectors.toList());
                List<BDPhysicalFieldRes> distinctPhysicalFields = physicalFields.stream().filter(CollectionsBDUtils.distinctByKey((pf) -> pf.getPhysicalEntity().getSchema() + pf.getPhysicalEntity().getName() + pf.getName()))
                        .collect(Collectors.toList());

                encounteredQualityCheck.setPhysicalEntities(distinctPhysicalEntities);
                encounteredQualityCheck.setPhysicalFields(distinctPhysicalFields);
                if (!qualityCheck.isReference()) {
                    qualityCheck.setPhysicalFields(distinctPhysicalFields);
                    qualityCheck.setPhysicalEntities(distinctPhysicalEntities);
                    mergedQualityChecks.put(qualityCheck.getCode(), qualityCheck);
                }
            } else {
                mergedQualityChecks.put(qualityCheck.getCode(), qualityCheck);
            }
        });
        return new ArrayList<>(mergedQualityChecks.values());
    }

    private void addSystemToLinkedPhysicalEntitiesAndFields(Port port, List<QualityCheck> qualityChecks) {
        BDSystemRes platformSystem = getSystem(port);
        qualityChecks.forEach(qualityCheck -> {
            qualityCheck.getPhysicalEntities().forEach(pe -> pe.setSystem(platformSystem));
            qualityCheck.getPhysicalFields().forEach(pf -> pf.getPhysicalEntity().setSystem(platformSystem));
        });
    }
}
