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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;

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

    public List<QualityCheck> extractQualityChecksFromPorts(List<Port> ports) {
        List<QualityCheck> qualityChecks = new ArrayList<>();
        try {
            for (Port port : ports) {
                getUseCaseLogger().info("Analyzing quality definition for port " + port.getFullyQualifiedName());
                //Get StandardDefinition from port
                Optional<StandardDefinition> standardDefinition = resolveStandardDefinitionFromPort(port);
                if (standardDefinition.isEmpty()) continue;

                //Retrieve a qualityExtractor that can handle the definition specification
                Optional<PortStandardDefinitionQualityExtractor> portQualityExtractor = this.qualityChecksExtractors.stream()
                        .filter(extractor -> extractor.supports(standardDefinition.get()))
                        .findFirst();
                if (portQualityExtractor.isEmpty()) {
                    getUseCaseLogger().warn(
                            String.format("Quality extraction for data product port: %s with specification: %s and version: %s is not supported.",
                                    port.getFullyQualifiedName(), standardDefinition.get().getSpecification(), standardDefinition.get().getSpecificationVersion()
                            ));
                    continue;
                }

                //Extract quality checks from the definition using the extractor
                List<QualityCheck> extractedQualityChecks = portQualityExtractor.get().extractQualityChecks(standardDefinition.get());

                addSystemToLinkedPhysicalEntitiesAndFields(port, extractedQualityChecks);
                qualityChecks.addAll(extractedQualityChecks);
            }

            return mergeQualityChecksWithSameCode(qualityChecks);
        } catch (Exception e) {
            getUseCaseLogger().warn(e.getMessage(), e);
        }
        return qualityChecks;
    }

    public List<BDDataProductPortAssetDetailRes> extractPhysicalResourcesFromPorts(List<Port> ports) {
        List<BDDataProductPortAssetDetailRes> dataProductPortAssetDetailRes = new ArrayList<>();
        try {
            for (Port port : ports) {
                getUseCaseLogger().info("Analyzing data assets for port " + port.getFullyQualifiedName());
                Optional<StandardDefinition> standardDefinition = resolveStandardDefinitionFromPort(port);
                if (standardDefinition.isEmpty()) continue;
                getUseCaseLogger().info(String.format("Data product port: %s has specification: %s of version: %s", port.getFullyQualifiedName(), standardDefinition.get().getSpecification(), standardDefinition.get().getSpecificationVersion()));

                BDSystemRes platformSystem = getSystem(port);

                Optional<PortStandardDefinitionEntitiesExtractor> portStandardDefinitionAnalyzer = getPortStandardDefinitionAnalyzer(standardDefinition.get());
                if (portStandardDefinitionAnalyzer.isEmpty()) {
                    getUseCaseLogger().warn(String.format(
                            "Data product port: %s with specification: %s and version: %s is not supported.",
                            port.getFullyQualifiedName(), standardDefinition.get().getSpecification(), standardDefinition.get().getSpecificationVersion())
                    );
                    continue;
                }

                List<BDPhysicalEntityRes> extractedEntities = portStandardDefinitionAnalyzer.get().extractEntities(standardDefinition.get());
                getUseCaseLogger().info(String.format(
                        "Data product port: %s, found %s physical entities",
                        port.getFullyQualifiedName(), extractedEntities.size())
                );
                //forcing physical entities to have the platform system
                extractedEntities = extractedEntities.stream().map(pe -> {
                    if (CollectionUtils.isEmpty(pe.getPhysicalFields())) {
                        getUseCaseLogger().info(String.format(
                                "Data product port: %s, physical entity: %s, no physical fields found", port.getFullyQualifiedName(), pe.getName()
                        ));
                    } else {
                        getUseCaseLogger().info(String.format("Data product port: %s, physical entity: %s, found %s physical fields", port.getFullyQualifiedName(), pe.getName(), pe.getPhysicalFields().size()));
                    }
                    pe.setSystem(platformSystem);
                    return pe;
                }).collect(Collectors.toList());

                BDDataProductPortAssetDetailRes bdDataProductPortAssetDetail = buildDataProductPortAssetDetail(port, platformSystem, extractedEntities);
                dataProductPortAssetDetailRes.add(bdDataProductPortAssetDetail);
            }
        } catch (Exception e) {
            getUseCaseLogger().warn(e.getMessage(), e);
        }
        return dataProductPortAssetDetailRes;
    }

    private Optional<StandardDefinition> resolveStandardDefinitionFromPort(Port port) {
        if (port.getPromises() == null || port.getPromises().getApi() == null) {
            getUseCaseLogger().info(String.format("Data product port: %s has empty Api", port.getFullyQualifiedName()));
            return Optional.empty();
        }
        Optional<JsonNode> rawApi = retrieveRawApi(port);
        if (rawApi.isEmpty()) {
            getUseCaseLogger().info("No definition found for the data product port: " + port.getFullyQualifiedName());
            return Optional.empty();
        }

        try {
            StandardDefinition standardDefinition = objectMapper.treeToValue(rawApi.get(), StandardDefinition.class);
            if (portIsNotValid(port, standardDefinition)) {
                return Optional.empty();
            }
            return Optional.of(standardDefinition);
        } catch (Exception e) {
            getUseCaseLogger().warn(String.format("Failed to parse standard definition for port %s: %s", port.getFullyQualifiedName(), e.getMessage()));
            return Optional.empty();
        }
    }

    private Optional<JsonNode> retrieveRawApi(Port port) {
        if (portContainsCompleteApi(port)) {
            return Optional.of(objectMapper.valueToTree(port.getPromises().getApi()));
        } else {
            return registryClient.getApi(port.getPromises().getApi().getId());
        }
    }

    private boolean portContainsCompleteApi(Port port) {
        return port.getPromises().getApi().getDefinition() != null && !port.getPromises().getApi().getDefinition().getAdditionalProperties().containsKey("$ref");
    }

    private boolean portIsNotValid(Port port, StandardDefinition standardDefinition) {
        if (!StringUtils.hasText(standardDefinition.getSpecification())) {
            getUseCaseLogger().warn("Missing specification on port: " + port.getFullyQualifiedName());
            return true;
        }
        if (!StringUtils.hasText(standardDefinition.getSpecificationVersion())) {
            getUseCaseLogger().warn("Missing specification version on port: " + port.getFullyQualifiedName());
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
                List<BDPhysicalEntityRes> distinctPhysicalEntities = physicalEntities.stream().filter(CollectionsBDUtils.distinctByKey(this::getPeNaturalKey))
                        .collect(Collectors.toList());
                List<BDPhysicalFieldRes> distinctPhysicalFields = physicalFields.stream().filter(CollectionsBDUtils.distinctByKey(this::getPfNaturalKey))
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

    private String getPfNaturalKey(BDPhysicalFieldRes pf) {
        return pf.getPhysicalEntity().getSystem().getName() + pf.getPhysicalEntity().getSchema() + pf.getPhysicalEntity().getName()
                + pf.getName();
    }

    private String getPeNaturalKey(BDPhysicalEntityRes pe) {
        return pe.getSystem().getName() + pe.getSchema() + pe.getName();
    }

    private void addSystemToLinkedPhysicalEntitiesAndFields(Port port, List<QualityCheck> qualityChecks) {
        BDSystemRes platformSystem = getSystem(port);
        qualityChecks.forEach(qualityCheck -> {
            qualityCheck.getPhysicalEntities().forEach(pe -> pe.setSystem(platformSystem));
            qualityCheck.getPhysicalFields().forEach(pf -> pf.getPhysicalEntity().setSystem(platformSystem));
        });
    }
}
