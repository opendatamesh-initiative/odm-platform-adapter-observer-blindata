package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.probes_upload;

import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.exceptions.BlindataClientException;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes.BDQualityProbesConnectionRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes.BDQualityProbesDefinitionRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes.BDQualityProbesProjectRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes.BDQualityProbesQueryRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes.BDQualityProbesTagRes;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.UseCase;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;

class ProbesUpload implements UseCase {

    private static final String USE_CASE_PREFIX = "[ProbesUpload]";
    private static final String PROBE_TYPE = "CONTRACT_RULE";

    private final ProbesUploadBlindataOutboundPort blindataOutboundPort;
    private final ProbesUploadOdmOutboundPort odmOutboundPort;

    ProbesUpload(ProbesUploadBlindataOutboundPort blindataOutboundPort, ProbesUploadOdmOutboundPort odmOutboundPort) {
        this.blindataOutboundPort = blindataOutboundPort;
        this.odmOutboundPort = odmOutboundPort;
    }

    @Override
    public void execute() throws UseCaseExecutionException {
        withErrorHandling(() -> {
            DataProductVersion dataProductVersion = odmOutboundPort.getDataProductVersion();
            List<ProbeCandidate> candidates = odmOutboundPort.extractProbeCandidates();

            if (CollectionUtils.isEmpty(candidates)) {
                getUseCaseLogger().info(String.format(
                        "%s Data Product: %s no library/sql probe candidates defined.",
                        USE_CASE_PREFIX,
                        dataProductVersion.getInfo().getFullyQualifiedName()
                ));
                return;
            }

            getUseCaseLogger().info(String.format(
                    "%s Data Product: %s extracted %s probe candidate(s).",
                    USE_CASE_PREFIX,
                    dataProductVersion.getInfo().getFullyQualifiedName(),
                    candidates.size()
            ));

            if (!validateConnections(candidates)) {
                getUseCaseLogger().warn(String.format(
                        "[#201] %s Probe upload skipped: one or more candidates have invalid or missing connections.",
                        USE_CASE_PREFIX
                ));
                return;
            }

            BDQualityProbesProjectRes project = ensureProject(dataProductVersion);
            upsertProbes(project, candidates);
            replaceVersionTag(project, dataProductVersion);
        });
    }

    private boolean validateConnections(List<ProbeCandidate> candidates) {
        boolean allValid = true;
        Map<String, String> resolvedConnectionTypes = new HashMap<>();

        for (ProbeCandidate candidate : candidates) {
            if (!StringUtils.hasText(candidate.getConnectionName())) {
                getUseCaseLogger().warn(String.format(
                        "[#200] %s Missing Blindata connection name on port '%s' for probe '%s' (check '%s').",
                        USE_CASE_PREFIX,
                        candidate.getPortFullyQualifiedName(),
                        candidate.getProbeName(),
                        candidate.getCheckName()
                ));
                allValid = false;
                continue;
            }

            String cachedType = resolvedConnectionTypes.get(candidate.getConnectionName());
            if (cachedType != null) {
                candidate.setConnectionType(cachedType);
                continue;
            }

            Optional<BDQualityProbesConnectionRes> connection =
                    blindataOutboundPort.findProbeConnectionByName(candidate.getConnectionName());
            if (connection.isEmpty()) {
                getUseCaseLogger().warn(String.format(
                        "[#204] %s Unknown Blindata probe connection '%s' for port '%s' and probe '%s'.",
                        USE_CASE_PREFIX,
                        candidate.getConnectionName(),
                        candidate.getPortFullyQualifiedName(),
                        candidate.getProbeName()
                ));
                allValid = false;
                continue;
            }

            String connectionType = connection.get().getType();
            if (!StringUtils.hasText(connectionType)) {
                getUseCaseLogger().warn(String.format(
                        "[#205] %s Blindata probe connection '%s' has no type, required to run probe '%s' on port '%s'.",
                        USE_CASE_PREFIX,
                        candidate.getConnectionName(),
                        candidate.getProbeName(),
                        candidate.getPortFullyQualifiedName()
                ));
                allValid = false;
                continue;
            }

            connectionType = connectionType.toUpperCase();
            candidate.setConnectionType(connectionType);
            resolvedConnectionTypes.put(candidate.getConnectionName(), connectionType);
        }
        return allValid;
    }

    private BDQualityProbesProjectRes ensureProject(DataProductVersion dataProductVersion) {
        String projectName = buildProjectName(dataProductVersion);
        Optional<BDQualityProbesProjectRes> existingProject = blindataOutboundPort.findProbeProjectByName(projectName);
        if (existingProject.isPresent()) {
            return existingProject.get();
        }

        BDQualityProbesProjectRes newProject = new BDQualityProbesProjectRes();
        newProject.setName(projectName);
        newProject.setDescription(String.format("Probe project for data product %s", dataProductVersion.getInfo().getFullyQualifiedName()));
        return blindataOutboundPort.createProbeProject(newProject);
    }

    private void upsertProbes(BDQualityProbesProjectRes project, List<ProbeCandidate> candidates) {
        BDQualityProbesProjectRes projectRef = new BDQualityProbesProjectRes();
        projectRef.setUuid(project.getUuid());
        projectRef.setName(project.getName());

        for (ProbeCandidate candidate : candidates) {
            BDQualityProbesDefinitionRes definition = buildDefinition(candidate, projectRef);
            Optional<BDQualityProbesDefinitionRes> existingDefinition =
                    blindataOutboundPort.findProbeDefinitionByProjectAndName(project.getUuid(), candidate.getProbeName());
            if (existingDefinition.isPresent()) {
                blindataOutboundPort.overwriteProbeDefinition(existingDefinition.get().getRootUuid(), definition);
                getUseCaseLogger().info(String.format(
                        "%s Overwrote probe definition '%s' in project '%s'.",
                        USE_CASE_PREFIX,
                        candidate.getProbeName(),
                        project.getName()
                ));
            } else {
                blindataOutboundPort.createProbeDefinition(definition);
                getUseCaseLogger().info(String.format(
                        "%s Created probe definition '%s' in project '%s'.",
                        USE_CASE_PREFIX,
                        candidate.getProbeName(),
                        project.getName()
                ));
            }
        }
    }

    private void replaceVersionTag(BDQualityProbesProjectRes project, DataProductVersion dataProductVersion) {
        String tagName = dataProductVersion.getInfo().getVersion();
        if (!StringUtils.hasText(tagName)) {
            getUseCaseLogger().warn(String.format("[#202] %s Missing data product version; skipping probe tag creation.", USE_CASE_PREFIX));
            return;
        }

        Optional<BDQualityProbesTagRes> existingTag = blindataOutboundPort.findTagByProjectAndName(project.getUuid(), tagName);
        existingTag.ifPresent(tag -> blindataOutboundPort.deleteTag(tag.getUuid()));

        BDQualityProbesProjectRes projectRef = new BDQualityProbesProjectRes();
        projectRef.setUuid(project.getUuid());
        projectRef.setName(project.getName());

        BDQualityProbesTagRes tag = new BDQualityProbesTagRes();
        tag.setName(tagName);
        tag.setDescription(String.format("Snapshot for data product version %s", tagName));
        tag.setProject(projectRef);
        blindataOutboundPort.createTag(tag);
    }

    private BDQualityProbesDefinitionRes buildDefinition(ProbeCandidate candidate, BDQualityProbesProjectRes projectRef) {
        BDQualityProbesQueryRes query = new BDQualityProbesQueryRes();
        query.setConnectionName(candidate.getConnectionName());
        query.setConnectionType(candidate.getConnectionType());
        query.setQueryBody(ContractRuleEnvelopeBuilder.buildQueryBody(candidate.getContractRule(), candidate.getPhysicalBinding()));

        BDQualityProbesDefinitionRes definition = new BDQualityProbesDefinitionRes();
        definition.setName(candidate.getProbeName());
        definition.setType(PROBE_TYPE);
        definition.setCheckCode(candidate.getCheckCode());
        definition.setCheckName(candidate.getCheckName());
        definition.setProject(projectRef);
        definition.setQueries(Collections.singletonList(query));
        return definition;
    }

    private String buildProjectName(DataProductVersion dataProductVersion) {
        return String.format(
                "%s - %s",
                dataProductVersion.getInfo().getDomain(),
                dataProductVersion.getInfo().getName()
        );
    }

    private void withErrorHandling(Runnable runnable) throws UseCaseExecutionException {
        try {
            runnable.run();
        } catch (BlindataClientException e) {
            if (e.getCode() != HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                throw e;
            } else {
                getUseCaseLogger().warn("[#203] " + e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new UseCaseExecutionException(e.getMessage(), e);
        }
    }
}
