package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.quality_upload;

import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.dpds.model.interfaces.InterfaceComponents;
import org.opendatamesh.dpds.model.interfaces.Port;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.exceptions.BlindataClientException;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDUploadResultsMessage;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualitySuiteRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
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

            List<QualityCheck> qualityChecks = extractQualityFromPorts(dataProductVersion.getInterfaceComponents());
            if (CollectionUtils.isEmpty(qualityChecks)) {
                getUseCaseLogger().info(String.format("%s Data Product: %s no quality check defined.", USE_CASE_PREFIX, dataProductVersion.getInfo().getFullyQualifiedName()));
                return;
            }
            getUseCaseLogger().info(String.format("%s Data Product: %s extracted  %s quality checks.", USE_CASE_PREFIX, dataProductVersion.getInfo().getFullyQualifiedName(), qualityChecks.size()));

            qualityChecks = removeMalformedReferences(qualityChecks);

            BDQualitySuiteRes qualitySuite = buildQualitySuite(dataProductVersion);
            BDUploadResultsMessage uploadResult = blindataOutboundPort.saveQualityDefinitions(qualitySuite, qualityChecks);

            getUseCaseLogger().info(String.format("%s Quality Checks created: %s updated: %s discarded: %s", USE_CASE_PREFIX, uploadResult.getRowCreated(), uploadResult.getRowUpdated(), uploadResult.getRowDiscarded()));
            if (StringUtils.hasText(uploadResult.getMessage())) {
                getUseCaseLogger().warn(String.format("%s Quality Checks upload error: %s", USE_CASE_PREFIX, uploadResult.getMessage()));
            }
        });
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
        qualitySuite.setCode(String.format("%s::%s", dataProductVersion.getInfo().getDomain(), dataProductVersion.getInfo().getName()));
        qualitySuite.setName(String.format("%s::%s", dataProductVersion.getInfo().getDomain(), displayName));
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
}
