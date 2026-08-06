package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.probes_upload;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.dpds.model.interfaces.Port;
import org.opendatamesh.platform.up.metaservice.blindata.configurations.BdDataProductConfig;
import org.opendatamesh.platform.up.metaservice.blindata.configurations.BdProbesUploadConfig;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes.BDQualityProbesConnectionRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes.BDQualityProbesDefinitionRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes.BDQualityProbesProjectRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes.BDQualityProbesTagRes;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1.PortDatastoreApiEntitiesExtractor;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking.SemanticLinkManager;
import org.opendatamesh.platform.up.metaservice.blindata.services.DataProductPortAssetAnalyzer;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLogger;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProbesUploadTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ProbesUploadBlindataOutboundPort blindataOutboundPort;
    @Mock
    private SemanticLinkManager mockSemanticLinkManager;
    @Mock
    private BdDataProductConfig mockBDDataProductConfig;
    @Mock
    private UseCaseLogger mockLogger;

    @InjectMocks
    private PortDatastoreApiEntitiesExtractor portStandardDefinitionAnalyzer;

    private UseCaseLogger originalLogger;

    @BeforeEach
    void setUpLogger() {
        originalLogger = UseCaseLoggerContext.getUseCaseLogger();
        UseCaseLoggerContext.setUseCaseLogger(mockLogger);
    }

    @AfterEach
    void restoreLogger() {
        UseCaseLoggerContext.setUseCaseLogger(originalLogger);
    }

    @Test
    void testProbesUploadUsesStableDataProductNameForProject() throws IOException, UseCaseExecutionException {
        DataProductVersion dataProductVersion = loadDataProductVersion();
        assertThat(dataProductVersion.getInfo().getName()).isEqualTo("probeSim");
        assertThat(dataProductVersion.getInfo().getDisplayName()).isEqualTo("Probe Upload Simulation");
        ProbesUploadOdmOutboundPort odmOutboundPort = odmOutboundPortFromFixture(dataProductVersion);

        BDQualityProbesConnectionRes connection = new BDQualityProbesConnectionRes();
        connection.setName("Postgres");
        connection.setType("jdbc");

        BDQualityProbesProjectRes createdProject = new BDQualityProbesProjectRes();
        createdProject.setUuid("project-uuid");
        createdProject.setName("demo - probeSim");

        when(blindataOutboundPort.findProbeConnectionByName("Postgres")).thenReturn(Optional.of(connection));
        when(blindataOutboundPort.findProbeProjectByName("demo - probeSim")).thenReturn(Optional.empty());
        when(blindataOutboundPort.createProbeProject(any())).thenAnswer(invocation -> {
            BDQualityProbesProjectRes project = invocation.getArgument(0);
            project.setUuid("project-uuid");
            return project;
        });
        when(blindataOutboundPort.findProbeDefinitionByProjectAndName(eq("project-uuid"), anyString()))
                .thenReturn(Optional.empty());
        when(blindataOutboundPort.findTagByProjectAndName(eq("project-uuid"), eq("1.0.2")))
                .thenReturn(Optional.empty());
        when(blindataOutboundPort.createProbeDefinition(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(blindataOutboundPort.createTag(any())).thenAnswer(invocation -> invocation.getArgument(0));

        new ProbesUpload(blindataOutboundPort, odmOutboundPort).execute();

        ArgumentCaptor<BDQualityProbesProjectRes> projectCaptor = ArgumentCaptor.forClass(BDQualityProbesProjectRes.class);
        ArgumentCaptor<BDQualityProbesDefinitionRes> definitionCaptor = ArgumentCaptor.forClass(BDQualityProbesDefinitionRes.class);
        ArgumentCaptor<BDQualityProbesTagRes> tagCaptor = ArgumentCaptor.forClass(BDQualityProbesTagRes.class);

        verify(blindataOutboundPort, times(1)).createProbeProject(projectCaptor.capture());
        verify(blindataOutboundPort, times(1)).findProbeProjectByName("demo - probeSim");
        verify(blindataOutboundPort, times(3)).createProbeDefinition(definitionCaptor.capture());
        verify(blindataOutboundPort, never()).overwriteProbeDefinition(anyString(), any());
        verify(blindataOutboundPort, times(1)).createTag(tagCaptor.capture());
        verify(mockLogger, never()).warn(contains("[#200]"));
        verify(mockLogger, never()).warn(contains("[#201]"));
        verify(mockLogger, never()).warn(contains("[#204]"));
        verify(mockLogger, never()).warn(contains("[#205]"));

        ProbesExpectedResult expected = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("probes_upload_expected_results.json")),
                ProbesExpectedResult.class
        );

        assertThat(projectCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expected.getProbeProject());

        assertThat(definitionCaptor.getAllValues())
                .usingRecursiveComparison()
                .isEqualTo(expected.getProbeDefinitions());

        assertThat(tagCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expected.getTag());
    }

    @Test
    void testProbesUploadOverwritesExistingDefinitionsAndReplacesTag() throws IOException, UseCaseExecutionException {
        DataProductVersion dataProductVersion = loadDataProductVersion();
        ProbesUploadOdmOutboundPort odmOutboundPort = odmOutboundPortFromFixture(dataProductVersion);

        BDQualityProbesConnectionRes connection = new BDQualityProbesConnectionRes();
        connection.setName("Postgres");
        connection.setType("jdbc");

        BDQualityProbesProjectRes existingProject = new BDQualityProbesProjectRes();
        existingProject.setUuid("project-uuid");
        existingProject.setName("demo - probeSim");

        BDQualityProbesDefinitionRes existingDefinition = new BDQualityProbesDefinitionRes();
        existingDefinition.setRootUuid("root-uuid");
        existingDefinition.setName("existing");

        BDQualityProbesTagRes existingTag = new BDQualityProbesTagRes();
        existingTag.setUuid("tag-uuid");
        existingTag.setName("1.0.2");

        when(blindataOutboundPort.findProbeConnectionByName("Postgres")).thenReturn(Optional.of(connection));
        when(blindataOutboundPort.findProbeProjectByName("demo - probeSim"))
                .thenReturn(Optional.of(existingProject));
        when(blindataOutboundPort.findProbeDefinitionByProjectAndName(eq("project-uuid"), anyString()))
                .thenReturn(Optional.of(existingDefinition));
        when(blindataOutboundPort.findTagByProjectAndName(eq("project-uuid"), eq("1.0.2")))
                .thenReturn(Optional.of(existingTag));
        when(blindataOutboundPort.overwriteProbeDefinition(anyString(), any()))
                .thenAnswer(invocation -> invocation.getArgument(1));
        when(blindataOutboundPort.createTag(any())).thenAnswer(invocation -> invocation.getArgument(0));

        new ProbesUpload(blindataOutboundPort, odmOutboundPort).execute();

        verify(blindataOutboundPort, never()).createProbeProject(any());
        verify(blindataOutboundPort, never()).createProbeDefinition(any());
        verify(blindataOutboundPort, times(3)).overwriteProbeDefinition(eq("root-uuid"), any());
        verify(blindataOutboundPort, times(1)).deleteTag("tag-uuid");
        verify(blindataOutboundPort, times(1)).createTag(any());
    }

    @Test
    void testMissingConnection_warnsAndSkipsBlindataWrites() throws IOException, UseCaseExecutionException {
        DataProductVersion dataProductVersion = loadDataProductVersion();
        dataProductVersion.getInterfaceComponents().getOutputPorts()
                .forEach(port -> port.getAdditionalProperties().remove("x-blindataConnectionName"));
        ProbesUploadOdmOutboundPort odmOutboundPort = odmOutboundPortFromFixture(dataProductVersion);

        new ProbesUpload(blindataOutboundPort, odmOutboundPort).execute();

        verify(mockLogger, atLeastOnce()).warn(contains("[#200]"));
        verify(mockLogger, atLeastOnce()).warn(contains("[#201]"));
        verify(blindataOutboundPort, never()).createProbeProject(any());
        verify(blindataOutboundPort, never()).createProbeDefinition(any());
        verify(blindataOutboundPort, never()).createTag(any());
    }

    @Test
    void testUnknownConnection_warnsAndSkipsBlindataWrites() throws IOException, UseCaseExecutionException {
        DataProductVersion dataProductVersion = loadDataProductVersion();
        ProbesUploadOdmOutboundPort odmOutboundPort = odmOutboundPortFromFixture(dataProductVersion);

        when(blindataOutboundPort.findProbeConnectionByName("Postgres")).thenReturn(Optional.empty());

        new ProbesUpload(blindataOutboundPort, odmOutboundPort).execute();

        verify(mockLogger, atLeastOnce()).warn(contains("[#204]"));
        verify(mockLogger, atLeastOnce()).warn(contains("[#201]"));
        verify(blindataOutboundPort, never()).createProbeProject(any());
        verify(blindataOutboundPort, never()).createProbeDefinition(any());
        verify(blindataOutboundPort, never()).createTag(any());
    }

    @Test
    void testConnectionWithoutType_warnsAndSkipsBlindataWrites() throws IOException, UseCaseExecutionException {
        DataProductVersion dataProductVersion = loadDataProductVersion();
        ProbesUploadOdmOutboundPort odmOutboundPort = odmOutboundPortFromFixture(dataProductVersion);

        BDQualityProbesConnectionRes connectionWithoutType = new BDQualityProbesConnectionRes();
        connectionWithoutType.setName("Postgres");
        when(blindataOutboundPort.findProbeConnectionByName("Postgres"))
                .thenReturn(Optional.of(connectionWithoutType));

        new ProbesUpload(blindataOutboundPort, odmOutboundPort).execute();

        verify(mockLogger, atLeastOnce()).warn(contains("[#205]"));
        verify(mockLogger, atLeastOnce()).warn(contains("[#201]"));
        verify(blindataOutboundPort, never()).createProbeProject(any());
        verify(blindataOutboundPort, never()).createProbeDefinition(any());
        verify(blindataOutboundPort, never()).createTag(any());
    }

    private DataProductVersion loadDataProductVersion() throws IOException {
        return objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("probes_upload_data_product_version.json")),
                DataProductVersion.class
        );
    }

    private ProbesUploadOdmOutboundPort odmOutboundPortFromFixture(DataProductVersion dataProductVersion) {
        DataProductPortAssetAnalyzer analyzer = mock(DataProductPortAssetAnalyzer.class);
        when(analyzer.extractDeclaredQualityChecksFromPorts(any())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            List<Port> ports = invocation.getArgument(0);
            return ports.stream()
                    .map(port -> port.getPromises().getApi())
                    .flatMap(api -> portStandardDefinitionAnalyzer.extractQualityChecks(api).stream())
                    .collect(Collectors.toList());
        });

        BdProbesUploadConfig config = mock(BdProbesUploadConfig.class);
        lenient().when(config.getConnectionNamePropertyKey()).thenReturn("x-blindataConnectionName");

        return new ProbesUploadOdmOutboundPortImpl(analyzer, dataProductVersion, config);
    }

    static class ProbesExpectedResult {
        private BDQualityProbesProjectRes probeProject;
        private List<BDQualityProbesDefinitionRes> probeDefinitions;
        private BDQualityProbesTagRes tag;

        public ProbesExpectedResult() {
            //DO NOTHING
        }

        public BDQualityProbesProjectRes getProbeProject() {
            return probeProject;
        }

        public void setProbeProject(BDQualityProbesProjectRes probeProject) {
            this.probeProject = probeProject;
        }

        public List<BDQualityProbesDefinitionRes> getProbeDefinitions() {
            return probeDefinitions;
        }

        public void setProbeDefinitions(List<BDQualityProbesDefinitionRes> probeDefinitions) {
            this.probeDefinitions = probeDefinitions;
        }

        public BDQualityProbesTagRes getTag() {
            return tag;
        }

        public void setTag(BDQualityProbesTagRes tag) {
            this.tag = tag;
        }
    }
}
