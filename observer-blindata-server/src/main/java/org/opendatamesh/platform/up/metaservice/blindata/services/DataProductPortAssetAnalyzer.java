package org.opendatamesh.platform.up.metaservice.blindata.services;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
                if (port.getPromises() == null || port.getPromises().getApi() == null) continue;

                Optional<ExternalComponentResource> externalComponentResources = registryClient.getApi(port.getPromises().getApi().getName(), port.getPromises().getApi().getVersion()).stream().findFirst();
                if (externalComponentResources.isEmpty()) {
                    logger.info("No definition found for the Port {}", port.getFullyQualifiedName());
                    continue;
                }

                PortStandardDefinition portStandardDefinition = externalComponentToPortStandardDefinition(externalComponentResources.get());

                BDSystemRes platformSystem = getSystem(port);
                Optional<PortStandardDefinitionAnalyzer> portStandardDefinitionAnalyzer = getPortStandardDefinitionAnalyzer(portStandardDefinition);
                if (portStandardDefinitionAnalyzer.isEmpty()) {
                    logger.warn("Port:{} specification:{} version:{} is not supported.", port.getFullyQualifiedName(), portStandardDefinition.getSpecification(), portStandardDefinition.getSpecificationVersion());
                    continue;
                }

                List<BDPhysicalEntityRes> extractedEntities = portStandardDefinitionAnalyzer.get().getBDAssetsFromPortStandardDefinition(portStandardDefinition)
                        //forcing physical entities to have the platform system
                        .stream().map(pe -> {
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
        portStandardDefinition.setDefinition(externalComponentResources.getDefinition());
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
                return matcher.group(1);
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
