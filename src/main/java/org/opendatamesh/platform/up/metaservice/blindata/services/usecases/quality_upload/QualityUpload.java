package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.quality_upload;

import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.dpds.model.interfaces.InterfaceComponents;
import org.opendatamesh.dpds.model.interfaces.Port;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.exceptions.BlindataClientException;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDShortUserRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt.BDIssueCampaignRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt.BDIssueRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityStrategyRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualitySuiteRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityUploadResultsRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;

class QualityUpload implements UseCase {
    private final String USE_CASE_PREFIX = "[QualityUpload]";

    private final QualityUploadBlindataOutboundPort blindataOutboundPort;
    private final QualityUploadOdmOutboundPort odmOutboundPort;

    QualityUpload(QualityUploadBlindataOutboundPort blindataOutboundPort, QualityUploadOdmOutboundPort odmOutboundPort) {
        this.blindataOutboundPort = blindataOutboundPort;
        this.odmOutboundPort = odmOutboundPort;
    }


    @Override
    public void execute() throws UseCaseExecutionException {
        withErrorHandling(() -> {
            DataProductVersion dataProductVersion = odmOutboundPort.getDataProductVersion();
            validateDataProduct(dataProductVersion);

            validateDataProductDescriptorPorts(dataProductVersion.getInterfaceComponents());
            //KQI & QUALITY SUITE
            List<QualityCheck> qualityChecks = extractQualityFromPorts(dataProductVersion.getInterfaceComponents());
            if (CollectionUtils.isEmpty(qualityChecks)) {
                getUseCaseLogger().info(String.format("%s Data Product: %s no quality check defined.", USE_CASE_PREFIX, dataProductVersion.getInfo().getFullyQualifiedName()));
                return;
            }
            getUseCaseLogger().info(String.format("%s Data Product: %s extracted  %s quality checks.", USE_CASE_PREFIX, dataProductVersion.getInfo().getFullyQualifiedName(), qualityChecks.size()));

            qualityChecks = removeMalformedReferences(qualityChecks);
            BDQualitySuiteRes qualitySuite = buildQualitySuite(dataProductVersion);

            //ISSUE POLICIES & CAMPAIGN
            BDIssueCampaignRes issueCampaign = handleIssueCampaign(dataProductVersion);
            updateIssuePoliciesOnQualityChecks(qualityChecks, issueCampaign, dataProductVersion);

            addQualitySuiteCodeToQualityChecksCode(qualitySuite, qualityChecks);
            
            // Associate quality suite to quality checks before validation
            qualityChecks.forEach(qc -> qc.setQualitySuite(qualitySuite));
            // Validate quality checks after quality suite is associated
            validateQualityChecks(qualityChecks);

            BDQualityUploadResultsRes uploadResult = blindataOutboundPort.uploadQuality(qualitySuite, qualityChecks);

            getUseCaseLogger().info(String.format("%s Quality Checks created: %s updated: %s discarded: %s", USE_CASE_PREFIX, uploadResult.getRowCreated(), uploadResult.getRowUpdated(), uploadResult.getRowDiscarded()));
            if (StringUtils.hasText(uploadResult.getMessage())) {
                getUseCaseLogger().warn(String.format("%s Quality Checks upload error: %s", USE_CASE_PREFIX, uploadResult.getMessage()));
            }
        });
    }

    private void addQualitySuiteCodeToQualityChecksCode(BDQualitySuiteRes qualitySuite, List<QualityCheck> qualityChecks) {
        qualityChecks.forEach(qualityCheck -> qualityCheck.setCode(
                String.format("%s - %s", qualitySuite.getCode(), qualityCheck.getCode())
        ));
    }

    private void updateIssuePoliciesOnQualityChecks(List<QualityCheck> qualityChecks, BDIssueCampaignRes issueCampaign, DataProductVersion dataProductVersion) {
        qualityChecks.stream().flatMap(qualityCheck -> qualityCheck.getIssuePolicies().stream())
                .forEach(issuePolicy -> {
                    issuePolicy.getIssueTemplate().setCampaign(issueCampaign);

                    BDIssueRes issueTemplate = issuePolicy.getIssueTemplate();

                    // Handle assignee (issue owner)
                    if (issueTemplate.getAssignee() == null || !StringUtils.hasText(issueTemplate.getAssignee().getUsername())) {
                        // No issueOwner specified, use data product owner as default
                        setDataProductOwnerAsAssignee(issueTemplate, dataProductVersion);
                    } else if ("None".equalsIgnoreCase(issueTemplate.getAssignee().getUsername())) {
                        // issueOwner is set to "None", leave unassigned
                        issueTemplate.setAssignee(null);
                    } else {
                        // Validate assignee exists in Blindata
                        Optional<BDShortUserRes> owner = blindataOutboundPort.findUser(issueTemplate.getAssignee().getUsername());
                        if (owner.isEmpty()) {
                            getUseCaseLogger().warn(String.format("%s Issue owner '%s' not found in Blindata for issue policy '%s'.",
                                    USE_CASE_PREFIX, issueTemplate.getAssignee().getUsername(), issuePolicy.getName()));
                        } else {
                            // Update with full user information
                            issueTemplate.setAssignee(owner.get());
                        }
                    }

                    // Validate reporter exists in Blindata
                    if (issueTemplate.getReporter() != null && StringUtils.hasText(issueTemplate.getReporter().getUsername())) {
                        Optional<BDShortUserRes> reporter = blindataOutboundPort.findUser(issueTemplate.getReporter().getUsername());
                        if (reporter.isEmpty()) {
                            getUseCaseLogger().warn(String.format("%s Issue reporter '%s' not found in Blindata for issue policy '%s'.",
                                    USE_CASE_PREFIX, issueTemplate.getReporter().getUsername(), issuePolicy.getName()));
                        } else {
                            // Update with full user information
                            issueTemplate.setReporter(reporter.get());
                        }
                    }
                });
    }

    private void setDataProductOwnerAsAssignee(BDIssueRes issueTemplate, DataProductVersion dataProductVersion) {
        boolean hasValidDataProductOwner = dataProductVersion.getInfo() != null &&
                dataProductVersion.getInfo().getOwner() != null &&
                StringUtils.hasText(dataProductVersion.getInfo().getOwner().getId());

        if (hasValidDataProductOwner) {
            Optional<BDShortUserRes> dataProductOwner = blindataOutboundPort.findUser(dataProductVersion.getInfo().getOwner().getId());
            if (dataProductOwner.isPresent()) {
                issueTemplate.setAssignee(dataProductOwner.get());
            } else {
                getUseCaseLogger().warn(String.format("%s Data product owner '%s' not found in Blindata for data product: %s.",
                        USE_CASE_PREFIX, dataProductVersion.getInfo().getOwner().getId(), dataProductVersion.getInfo().getFullyQualifiedName()));
            }
        } else {
            getUseCaseLogger().warn(String.format("%s Missing data product owner on data product: %s, skipping assignee on issue policies.",
                    USE_CASE_PREFIX, dataProductVersion.getInfo().getFullyQualifiedName()));
        }
    }

    private BDIssueCampaignRes handleIssueCampaign(DataProductVersion dataProductVersion) {
        String campaignName = String.format("Quality - %s - %s", dataProductVersion.getInfo().getDomain(), dataProductVersion.getInfo().getName());
        Optional<BDIssueCampaignRes> existentCampaign = blindataOutboundPort.findIssueCampaign(campaignName);
        if (existentCampaign.isPresent()) {
            return existentCampaign.get();
        }
        BDIssueCampaignRes newIssueCampaign = new BDIssueCampaignRes();
        newIssueCampaign.setName(campaignName);
        return blindataOutboundPort.createIssueCampaign(newIssueCampaign);
    }

    private List<QualityCheck> removeMalformedReferences(List<QualityCheck> qualityChecks) {
        return qualityChecks.stream()
                .filter(qualityCheck -> {
                    if (qualityCheck.isReference()) {
                        getUseCaseLogger().warn(String.format("%s Quality Check: %s is a reference and does not have a main declaration.", USE_CASE_PREFIX, qualityCheck.getCode()));
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    private void validateDataProduct(DataProductVersion dataProductVersion) {
        if (dataProductVersion == null ||
                dataProductVersion.getInfo() == null ||
                !StringUtils.hasText(dataProductVersion.getInfo().getFullyQualifiedName()) ||
                !StringUtils.hasText(dataProductVersion.getInfo().getDomain()) ||
                !StringUtils.hasText(dataProductVersion.getInfo().getName())
        ) {
            getUseCaseLogger().warn(String.format("%s Missing info fields on data product", USE_CASE_PREFIX));
        }
    }

    private BDQualitySuiteRes buildQualitySuite(DataProductVersion dataProductVersion) {
        BDQualitySuiteRes qualitySuite = new BDQualitySuiteRes();
        String displayName = StringUtils.hasText(dataProductVersion.getInfo().getDisplayName()) ? dataProductVersion.getInfo().getDisplayName() : dataProductVersion.getInfo().getName();
        qualitySuite.setCode(String.format("%s - %s", dataProductVersion.getInfo().getDomain(), dataProductVersion.getInfo().getName()));
        qualitySuite.setName(String.format("%s - %s", dataProductVersion.getInfo().getDomain(), displayName));
        return qualitySuite;
    }

    private List<QualityCheck> extractQualityFromPorts(InterfaceComponents interfaceComponents) {
        List<Port> allPorts = Stream.of(interfaceComponents.getInputPorts(),
                        interfaceComponents.getOutputPorts(),
                        interfaceComponents.getControlPorts(),
                        interfaceComponents.getDiscoveryPorts(),
                        interfaceComponents.getObservabilityPorts())
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return odmOutboundPort.extractQualityChecks(allPorts);
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

    private void validateQualityChecks(List<QualityCheck> qualityChecks) {
        if (CollectionUtils.isEmpty(qualityChecks)) {
            return;
        }
        for (QualityCheck qualityCheck : qualityChecks) {
            validateQualityCheckRequiredParameters(qualityCheck);
            validateQualityCheckStrategyParameters(qualityCheck);
        }
    }

    private void validateQualityCheckRequiredParameters(QualityCheck qualityCheck) {
        String qualityCheckCode = qualityCheck.getCode() != null ? qualityCheck.getCode() : "unknown";
        if (!StringUtils.hasText(qualityCheck.getCode())) {
            getUseCaseLogger().warn(String.format("%s Quality Check validation failed: A valid code must be provided for the check. Quality Check: %s", USE_CASE_PREFIX, qualityCheckCode));
        }
        if (!StringUtils.hasText(qualityCheck.getName())) {
            getUseCaseLogger().warn(String.format("%s Quality Check validation failed: A valid name must be provided for the check. Quality Check: %s", USE_CASE_PREFIX, qualityCheckCode));
        }
        if (qualityCheck.getSuccessThreshold() == null || qualityCheck.getWarningThreshold() == null) {
            getUseCaseLogger().warn(String.format("%s Quality Check validation failed: Thresholds must be defined. Quality Check: %s", USE_CASE_PREFIX, qualityCheckCode));
        } else {
            if (qualityCheck.getSuccessThreshold().compareTo(BigDecimal.ZERO) <= 0 || qualityCheck.getSuccessThreshold().compareTo(BigDecimal.valueOf(100)) > 0) {
                getUseCaseLogger().warn(String.format("%s Quality Check validation failed: Success threshold must be between 0 and 100 included. Quality Check: %s", USE_CASE_PREFIX, qualityCheckCode));
            }

            if (qualityCheck.getWarningThreshold().compareTo(BigDecimal.ZERO) <= 0 || qualityCheck.getWarningThreshold().compareTo(BigDecimal.valueOf(100)) > 0) {
                getUseCaseLogger().warn(String.format("%s Quality Check validation failed: Warning threshold must be between 0 and 100 included. Quality Check: %s", USE_CASE_PREFIX, qualityCheckCode));
            }

            if (qualityCheck.getWarningThreshold().compareTo(qualityCheck.getSuccessThreshold()) > 0) {
                getUseCaseLogger().warn(String.format("%s Quality Check validation failed: Warning threshold must be lower than or equal to the success threshold. Quality Check: %s", USE_CASE_PREFIX, qualityCheckCode));
            }
        }
        if (qualityCheck.getQualitySuite() == null) {
            getUseCaseLogger().warn(String.format("%s Quality Check validation failed: A valid quality suite must be provided for the check. Quality Check: %s", USE_CASE_PREFIX, qualityCheckCode));
        }
    }

    private void validateQualityCheckStrategyParameters(QualityCheck qualityCheck) {
        String qualityCheckCode = qualityCheck.getCode() != null ? qualityCheck.getCode() : "unknown";
        BDQualityStrategyRes strategy = qualityCheck.getScoreStrategy();
        if (strategy == null) {
            return;
        }
        switch (strategy) {
            case PERCENTAGE:
            case PERCENTAGE_DEVIATION:
                break;
            case MINIMUM:
                validateMinimumStrategy(qualityCheck, qualityCheckCode);
                break;
            case MAXIMUM:
                validateMaximumStrategy(qualityCheck, qualityCheckCode);
                break;
            case DISTANCE:
                validateDistanceStrategy(qualityCheck, qualityCheckCode);
                break;
            default:
                getUseCaseLogger().warn(String.format("%s Quality Check validation failed: Unknown score strategy '%s'. Quality Check: %s", USE_CASE_PREFIX, strategy, qualityCheckCode));
        }
    }

    private void validateMinimumStrategy(QualityCheck qualityCheck, String qualityCheckCode) {
        if (qualityCheck.getScoreExpectedValue() == null) {
            getUseCaseLogger().warn(String.format("%s Quality Check validation failed: To properly calculate the score an expected value is mandatory for MINIMUM strategy. Quality Check: %s", USE_CASE_PREFIX, qualityCheckCode));
        }

        if (qualityCheck.getScoreLeftValue() == null) {
            getUseCaseLogger().warn(String.format("%s Quality Check validation failed: To properly calculate the score a lowest acceptable value is mandatory for MINIMUM strategy. Quality Check: %s", USE_CASE_PREFIX, qualityCheckCode));
        } else if (qualityCheck.getScoreExpectedValue() != null && qualityCheck.getScoreLeftValue().compareTo(qualityCheck.getScoreExpectedValue()) > 0) {
            getUseCaseLogger().warn(String.format("%s Quality Check validation failed: The lowest acceptable value must be lower than or equal to the expected value for MINIMUM strategy. Quality Check: %s", USE_CASE_PREFIX, qualityCheckCode));
        }
    }

    private void validateMaximumStrategy(QualityCheck qualityCheck, String qualityCheckCode) {
        if (qualityCheck.getScoreExpectedValue() == null) {
            getUseCaseLogger().warn(String.format("%s Quality Check validation failed: To properly calculate the score an expected value is mandatory for MAXIMUM strategy. Quality Check: %s", USE_CASE_PREFIX, qualityCheckCode));
        }
        if (qualityCheck.getScoreRightValue() == null) {
            getUseCaseLogger().warn(String.format("%s Quality Check validation failed: To properly calculate the score a highest acceptable value is mandatory for MAXIMUM strategy. Quality Check: %s", USE_CASE_PREFIX, qualityCheckCode));
        } else if (qualityCheck.getScoreExpectedValue() != null && qualityCheck.getScoreRightValue().compareTo(qualityCheck.getScoreExpectedValue()) < 0) {
            getUseCaseLogger().warn(String.format("%s Quality Check validation failed: The highest acceptable value must be greater than or equal to the expected value for MAXIMUM strategy. Quality Check: %s", USE_CASE_PREFIX, qualityCheckCode));
        }
    }

    private void validateDistanceStrategy(QualityCheck qualityCheck, String qualityCheckCode) {
        if (qualityCheck.getScoreExpectedValue() == null) {
            getUseCaseLogger().warn(String.format("%s Quality Check validation failed: To properly calculate the score an expected value is mandatory for DISTANCE strategy. Quality Check: %s", USE_CASE_PREFIX, qualityCheckCode));
        }
        if (qualityCheck.getScoreLeftValue() == null) {
            getUseCaseLogger().warn(String.format("%s Quality Check validation failed: To properly calculate the score a lowest acceptable value is mandatory for DISTANCE strategy. Quality Check: %s", USE_CASE_PREFIX, qualityCheckCode));
        } else if (qualityCheck.getScoreExpectedValue() != null && qualityCheck.getScoreLeftValue().compareTo(qualityCheck.getScoreExpectedValue()) > 0) {
            getUseCaseLogger().warn(String.format("%s Quality Check validation failed: The lowest acceptable value must be lower than or equal to the expected value for DISTANCE strategy. Quality Check: %s", USE_CASE_PREFIX, qualityCheckCode));
        }

        if (qualityCheck.getScoreRightValue() == null) {
            getUseCaseLogger().warn(String.format("%s Quality Check validation failed: To properly calculate the score a highest acceptable value is mandatory for DISTANCE strategy. Quality Check: %s", USE_CASE_PREFIX, qualityCheckCode));
        } else if (qualityCheck.getScoreExpectedValue() != null && qualityCheck.getScoreRightValue().compareTo(qualityCheck.getScoreExpectedValue()) < 0) {
            getUseCaseLogger().warn(String.format("%s Quality Check validation failed: The highest acceptable value must be greater than or equal to the expected value for DISTANCE strategy. Quality Check: %s", USE_CASE_PREFIX, qualityCheckCode));
        }
    }
}
