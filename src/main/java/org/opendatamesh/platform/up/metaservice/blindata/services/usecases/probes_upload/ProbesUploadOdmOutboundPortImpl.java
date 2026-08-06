package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.probes_upload;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.dpds.model.interfaces.InterfaceComponents;
import org.opendatamesh.dpds.model.interfaces.Port;
import org.opendatamesh.platform.up.metaservice.blindata.configurations.BdProbesUploadConfig;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDAdditionalPropertiesRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;
import org.opendatamesh.platform.up.metaservice.blindata.services.DataProductPortAssetAnalyzer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;

class ProbesUploadOdmOutboundPortImpl implements ProbesUploadOdmOutboundPort {

    private static final String USE_CASE_PREFIX = "[ProbesUpload]";

    private final DataProductPortAssetAnalyzer dataProductPortAssetAnalyzer;
    private final DataProductVersion dataProductVersion;
    private final BdProbesUploadConfig config;

    ProbesUploadOdmOutboundPortImpl(
            DataProductPortAssetAnalyzer dataProductPortAssetAnalyzer,
            DataProductVersion dataProductVersion,
            BdProbesUploadConfig config
    ) {
        this.dataProductPortAssetAnalyzer = dataProductPortAssetAnalyzer;
        this.dataProductVersion = dataProductVersion;
        this.config = config;
    }

    @Override
    public DataProductVersion getDataProductVersion() {
        return dataProductVersion;
    }

    @Override
    public List<ProbeCandidate> extractProbeCandidates() {
        InterfaceComponents interfaceComponents = dataProductVersion.getInterfaceComponents();
        if (interfaceComponents == null) {
            return Collections.emptyList();
        }

        List<Port> allPorts = Stream.of(
                        interfaceComponents.getInputPorts(),
                        interfaceComponents.getOutputPorts(),
                        interfaceComponents.getControlPorts(),
                        interfaceComponents.getDiscoveryPorts(),
                        interfaceComponents.getObservabilityPorts())
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        String suiteCode = buildSuiteCode(dataProductVersion);
        Map<String, ProbeCandidate> candidatesByProbeName = new LinkedHashMap<>();

        for (Port port : allPorts) {
            String connectionName = resolveConnectionName(port);
            //A probe runs on a single table or column: the checks must keep the physical object they were declared on,
            //which the refName merge of extractQualityChecksFromPorts does not guarantee.
            List<QualityCheck> qualityChecks = dataProductPortAssetAnalyzer.extractDeclaredQualityChecksFromPorts(Collections.singletonList(port));
            for (QualityCheck qualityCheck : qualityChecks) {
                if (qualityCheck.isReference()) {
                    continue;
                }
                String ruleType = getContractRuleType(qualityCheck);
                if (isLegacyQualityCheck(qualityCheck, ruleType)) {
                    continue;
                }
                if (!isLibraryOrSqlRuleType(ruleType)) {
                    if (StringUtils.hasText(ruleType)) {
                        getUseCaseLogger().info(String.format(
                                "%s Skipping unsupported quality rule type '%s' for check '%s' on port '%s'.",
                                USE_CASE_PREFIX, ruleType, qualityCheck.getCode(), port.getFullyQualifiedName()
                        ));
                    }
                    continue;
                }
                //Presence of a Blindata connection name opts the port into probe upload; absence is an intentional skip
                //(e.g. quality executed outside Blindata probes) and must not block publish.
                if (!StringUtils.hasText(connectionName)) {
                    getUseCaseLogger().info(String.format(
                            "%s Skipping quality rule '%s' on port '%s': no Blindata connection name declared; probe not uploaded.",
                            USE_CASE_PREFIX, qualityCheck.getCode(), port.getFullyQualifiedName()
                    ));
                    continue;
                }

                String checkCode = String.format("%s - %s", suiteCode, qualityCheck.getCode());
                if (candidatesByProbeName.containsKey(checkCode)) {
                    getUseCaseLogger().info(String.format(
                            "%s Quality rule '%s' is declared more than once on port '%s', keeping the first declaration for probe '%s'.",
                            USE_CASE_PREFIX, qualityCheck.getCode(), port.getFullyQualifiedName(), checkCode
                    ));
                    continue;
                }

                ProbeCandidate candidate = new ProbeCandidate();
                candidate.setProbeName(checkCode);
                candidate.setCheckCode(checkCode);
                candidate.setCheckName(qualityCheck.getName());
                candidate.setConnectionName(connectionName);
                candidate.setPortFullyQualifiedName(port.getFullyQualifiedName());
                candidate.setContractRule(ContractRuleEnvelopeBuilder.buildRule(qualityCheck));
                candidate.setPhysicalBinding(ContractRuleEnvelopeBuilder.buildPhysicalBinding(qualityCheck));
                candidatesByProbeName.put(checkCode, candidate);
            }
        }
        return new ArrayList<>(candidatesByProbeName.values());
    }

    private String buildSuiteCode(DataProductVersion dataProductVersion) {
        return String.format("%s - %s", dataProductVersion.getInfo().getDomain(), dataProductVersion.getInfo().getName());
    }

    private String resolveConnectionName(Port port) {
        if (port.getAdditionalProperties() == null || port.getAdditionalProperties().isEmpty()) {
            return null;
        }
        String configuredKey = config.getConnectionNamePropertyKey();
        String connectionName = readConnectionProperty(port, configuredKey);
        if (StringUtils.hasText(connectionName)) {
            return connectionName;
        }
        if (configuredKey.startsWith("x-")) {
            return readConnectionProperty(port, configuredKey.substring(2));
        }
        return null;
    }

    private String readConnectionProperty(Port port, String key) {
        JsonNode node = port.getAdditionalProperties().get(key);
        if (node != null && node.isTextual()) {
            return node.asText();
        }
        return null;
    }

    private boolean isLibraryOrSqlRuleType(String ruleType) {
        if (!StringUtils.hasText(ruleType)) {
            return false;
        }
        return "library".equalsIgnoreCase(ruleType) || "sql".equalsIgnoreCase(ruleType);
    }

    private boolean isLegacyQualityCheck(QualityCheck qualityCheck, String ruleType) {
        return qualityCheck.getScoreStrategy() != null && !StringUtils.hasText(ruleType);
    }

    private String getContractRuleType(QualityCheck qualityCheck) {
        if (CollectionUtils.isEmpty(qualityCheck.getAdditionalProperties())) {
            return null;
        }
        return qualityCheck.getAdditionalProperties().stream()
                .filter(prop -> "_contract.ruleType".equals(prop.getName()))
                .map(BDAdditionalPropertiesRes::getValue)
                .findFirst()
                .orElse(null);
    }
}
