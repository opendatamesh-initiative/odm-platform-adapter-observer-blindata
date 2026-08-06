package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.probes_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes.BDQualityProbesConnectionRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes.BDQualityProbesDefinitionRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes.BDQualityProbesProjectRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes.BDQualityProbesTagRes;

import java.util.Optional;

class ProbesUploadBlindataOutboundPortDryRunImpl implements ProbesUploadBlindataOutboundPort {

    private final ProbesUploadBlindataOutboundPort outboundPort;

    ProbesUploadBlindataOutboundPortDryRunImpl(ProbesUploadBlindataOutboundPort outboundPort) {
        this.outboundPort = outboundPort;
    }

    @Override
    public Optional<BDQualityProbesProjectRes> findProbeProjectByName(String projectName) {
        return outboundPort.findProbeProjectByName(projectName);
    }

    @Override
    public BDQualityProbesProjectRes createProbeProject(BDQualityProbesProjectRes project) {
        return project;
    }

    @Override
    public Optional<BDQualityProbesDefinitionRes> findProbeDefinitionByProjectAndName(String projectUuid, String probeName) {
        return outboundPort.findProbeDefinitionByProjectAndName(projectUuid, probeName);
    }

    @Override
    public BDQualityProbesDefinitionRes createProbeDefinition(BDQualityProbesDefinitionRes definition) {
        return definition;
    }

    @Override
    public BDQualityProbesDefinitionRes overwriteProbeDefinition(String rootUuid, BDQualityProbesDefinitionRes definition) {
        definition.setRootUuid(rootUuid);
        return definition;
    }

    @Override
    public Optional<BDQualityProbesConnectionRes> findProbeConnectionByName(String connectionName) {
        return outboundPort.findProbeConnectionByName(connectionName);
    }

    @Override
    public Optional<BDQualityProbesTagRes> findTagByProjectAndName(String projectUuid, String tagName) {
        return outboundPort.findTagByProjectAndName(projectUuid, tagName);
    }

    @Override
    public void deleteTag(String tagUuid) {
        // dry-run: no mutation
    }

    @Override
    public BDQualityProbesTagRes createTag(BDQualityProbesTagRes tag) {
        return tag;
    }
}
