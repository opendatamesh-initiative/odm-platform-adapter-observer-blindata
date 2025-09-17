package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.quality_upload;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.dpds.model.interfaces.Port;
import org.opendatamesh.dpds.model.interfaces.Promises;
import org.opendatamesh.platform.up.metaservice.blindata.configurations.BdDataProductConfig;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDShortUserRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt.BDIssuePolicyRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt.BDIssueRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualitySuiteRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityUploadResultsRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1.PortDatastoreApiEntitiesExtractor;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking.SemanticLinkManager;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QualityUploadTest {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private QualityUploadBlindataOutboundPort blindataOutboundPort;
    @Mock
    private QualityUploadOdmOutboundPort odmOutboundPort;

    @Mock
    private SemanticLinkManager mockSemanticLinkManager;

    @Mock
    private BdDataProductConfig mockBDDataProductConfig;

    @InjectMocks
    private PortDatastoreApiEntitiesExtractor portStandardDefinitionAnalyzer;

    @Test
    public void testDataProductVersionUpload() throws IOException, UseCaseExecutionException {
        DataProductVersion dataProductVersion = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("quality_upload_data_product_version.json")),
                DataProductVersion.class
        );

        List<QualityCheck> qualityChecks = dataProductVersion.getInterfaceComponents()
                .getOutputPorts()
                .stream()
                .map(Port::getPromises)
                .map(Promises::getApi)
                .flatMap(def -> portStandardDefinitionAnalyzer.extractQualityChecks(def)
                        .stream()).collect(Collectors.toList());

        when(odmOutboundPort.getDataProductVersion()).thenReturn(dataProductVersion);
        when(odmOutboundPort.extractQualityChecks(any())).thenReturn(qualityChecks);

        when(blindataOutboundPort.findUser(any())).thenReturn(Optional.empty());
        when(blindataOutboundPort.findIssueCampaign(any())).thenReturn(Optional.empty());
        when(blindataOutboundPort.createIssueCampaign(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(blindataOutboundPort.uploadQuality(any(), any())).thenReturn(new BDQualityUploadResultsRes());

        new QualityUpload(blindataOutboundPort, odmOutboundPort).execute();

        verify(odmOutboundPort, times(1)).extractQualityChecks(any());
        verify(blindataOutboundPort, times(1)).findUser(any());
        verify(blindataOutboundPort, times(1)).findIssueCampaign(any());
        verify(blindataOutboundPort, times(1)).createIssueCampaign(any());

        ArgumentCaptor<BDQualitySuiteRes> qualitySuiteArg = ArgumentCaptor.forClass(BDQualitySuiteRes.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<QualityCheck>> qualityChecksArg = ArgumentCaptor.forClass(List.class);

        verify(blindataOutboundPort, times(1))
                .uploadQuality(qualitySuiteArg.capture(), qualityChecksArg.capture());

        QualityExpectedResult expectedQualityUpload = objectMapper.readValue(Resources.toByteArray(getClass().getResource("quality_upload_expected_results.json")), QualityExpectedResult.class);

        assertThat(qualitySuiteArg.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expectedQualityUpload.getQualitySuite());

        assertThat(qualityChecksArg.getValue()).hasSize(2);
        Set<String> qualityChecksCodes = qualityChecksArg.getValue().stream().map(QualityCheck::getCode).collect(Collectors.toSet());
        assertThat(qualityChecksCodes).containsExactlyInAnyOrderElementsOf(expectedQualityUpload.getQualityChecksCodes());

        List<BDIssuePolicyRes> issuePolicies = qualityChecksArg.getValue().stream().flatMap(qualityCheck -> qualityCheck.getIssuePolicies().stream()).collect(Collectors.toList());
        assertThat(issuePolicies)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFields("policyContent")
                .isEqualTo(expectedQualityUpload.getIssuePolicies());
    }

    @Test
    public void testQualityUploadWithValidIssueOwnerAndReporter() throws IOException, UseCaseExecutionException {
        DataProductVersion dataProductVersion = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("quality_upload_data_product_version.json")),
                DataProductVersion.class
        );

        List<QualityCheck> qualityChecks = dataProductVersion.getInterfaceComponents()
                .getOutputPorts()
                .stream()
                .map(Port::getPromises)
                .map(Promises::getApi)
                .flatMap(def -> portStandardDefinitionAnalyzer.extractQualityChecks(def)
                        .stream()).collect(Collectors.toList());

        // Clear existing issue policies and add new ones with valid users
        for (QualityCheck qualityCheck : qualityChecks) {
            qualityCheck.getIssuePolicies().clear(); // Clear existing issue policies
            BDIssuePolicyRes issuePolicy = new BDIssuePolicyRes();
            issuePolicy.setName("Test Policy");
            
            // Create issue template with valid users
            BDIssueRes issueTemplate = new BDIssueRes();
            issueTemplate.setName("Test Issue");
            
            BDShortUserRes owner = new BDShortUserRes();
            owner.setUsername("valid.owner@example.com");
            issueTemplate.setAssignee(owner);
            
            BDShortUserRes reporter = new BDShortUserRes();
            reporter.setUsername("valid.reporter@example.com");
            issueTemplate.setReporter(reporter);
            
            issuePolicy.setIssueTemplate(issueTemplate);
            qualityCheck.getIssuePolicies().add(issuePolicy);
        }

        when(odmOutboundPort.getDataProductVersion()).thenReturn(dataProductVersion);
        when(odmOutboundPort.extractQualityChecks(any())).thenReturn(qualityChecks);

        // Mock valid users
        BDShortUserRes validOwner = new BDShortUserRes();
        validOwner.setUsername("valid.owner@example.com");
        validOwner.setUuid("owner-uuid");
        
        BDShortUserRes validReporter = new BDShortUserRes();
        validReporter.setUsername("valid.reporter@example.com");
        validReporter.setUuid("reporter-uuid");
        
        when(blindataOutboundPort.findUser("valid.owner@example.com")).thenReturn(Optional.of(validOwner));
        when(blindataOutboundPort.findUser("valid.reporter@example.com")).thenReturn(Optional.of(validReporter));
        when(blindataOutboundPort.findUser("owner@default.blindata.io")).thenReturn(Optional.empty()); // Data product owner
        when(blindataOutboundPort.findIssueCampaign(any())).thenReturn(Optional.empty());
        when(blindataOutboundPort.createIssueCampaign(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(blindataOutboundPort.uploadQuality(any(), any())).thenReturn(new BDQualityUploadResultsRes());

        new QualityUpload(blindataOutboundPort, odmOutboundPort).execute();

        verify(blindataOutboundPort, times(1)).uploadQuality(any(), any());
    }

    @Test
    public void testQualityUploadWithInvalidIssueOwner() throws IOException, UseCaseExecutionException {
        DataProductVersion dataProductVersion = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("quality_upload_data_product_version.json")),
                DataProductVersion.class
        );

        List<QualityCheck> qualityChecks = dataProductVersion.getInterfaceComponents()
                .getOutputPorts()
                .stream()
                .map(Port::getPromises)
                .map(Promises::getApi)
                .flatMap(def -> portStandardDefinitionAnalyzer.extractQualityChecks(def)
                        .stream()).collect(Collectors.toList());

        // Clear existing issue policies and add new one with invalid owner
        for (QualityCheck qualityCheck : qualityChecks) {
            qualityCheck.getIssuePolicies().clear(); // Clear existing issue policies
            BDIssuePolicyRes issuePolicy = new BDIssuePolicyRes();
            issuePolicy.setName("Test Policy");
            
            BDIssueRes issueTemplate = new BDIssueRes();
            issueTemplate.setName("Test Issue");
            
            BDShortUserRes invalidOwner = new BDShortUserRes();
            invalidOwner.setUsername("invalid.owner@example.com");
            issueTemplate.setAssignee(invalidOwner);
            
            issuePolicy.setIssueTemplate(issueTemplate);
            qualityCheck.getIssuePolicies().add(issuePolicy);
        }

        when(odmOutboundPort.getDataProductVersion()).thenReturn(dataProductVersion);
        when(odmOutboundPort.extractQualityChecks(any())).thenReturn(qualityChecks);

        // Mock invalid user
        when(blindataOutboundPort.findUser("invalid.owner@example.com")).thenReturn(Optional.empty());
        when(blindataOutboundPort.findUser("owner@default.blindata.io")).thenReturn(Optional.empty()); // Data product owner
        when(blindataOutboundPort.findIssueCampaign(any())).thenReturn(Optional.empty());
        when(blindataOutboundPort.createIssueCampaign(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        new QualityUpload(blindataOutboundPort, odmOutboundPort).execute();

        // Verify upload is skipped due to invalid user
        verify(blindataOutboundPort, never()).uploadQuality(any(), any());
    }

    @Test
    public void testQualityUploadWithInvalidIssueReporter() throws IOException, UseCaseExecutionException {
        DataProductVersion dataProductVersion = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("quality_upload_data_product_version.json")),
                DataProductVersion.class
        );

        List<QualityCheck> qualityChecks = dataProductVersion.getInterfaceComponents()
                .getOutputPorts()
                .stream()
                .map(Port::getPromises)
                .map(Promises::getApi)
                .flatMap(def -> portStandardDefinitionAnalyzer.extractQualityChecks(def)
                        .stream()).collect(Collectors.toList());

        // Clear existing issue policies and add new one with invalid reporter
        for (QualityCheck qualityCheck : qualityChecks) {
            qualityCheck.getIssuePolicies().clear(); // Clear existing issue policies
            BDIssuePolicyRes issuePolicy = new BDIssuePolicyRes();
            issuePolicy.setName("Test Policy");
            
            BDIssueRes issueTemplate = new BDIssueRes();
            issueTemplate.setName("Test Issue");
            
            BDShortUserRes invalidReporter = new BDShortUserRes();
            invalidReporter.setUsername("invalid.reporter@example.com");
            issueTemplate.setReporter(invalidReporter);
            
            issuePolicy.setIssueTemplate(issueTemplate);
            qualityCheck.getIssuePolicies().add(issuePolicy);
        }

        when(odmOutboundPort.getDataProductVersion()).thenReturn(dataProductVersion);
        when(odmOutboundPort.extractQualityChecks(any())).thenReturn(qualityChecks);

        // Mock invalid user
        when(blindataOutboundPort.findUser("invalid.reporter@example.com")).thenReturn(Optional.empty());
        when(blindataOutboundPort.findUser("owner@default.blindata.io")).thenReturn(Optional.empty()); // Data product owner
        when(blindataOutboundPort.findIssueCampaign(any())).thenReturn(Optional.empty());
        when(blindataOutboundPort.createIssueCampaign(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        new QualityUpload(blindataOutboundPort, odmOutboundPort).execute();

        // Verify upload is skipped due to invalid user
        verify(blindataOutboundPort, never()).uploadQuality(any(), any());
    }


    @Test
    public void testQualityUploadWithMissingIssueOwnerDefaultsToDataProductOwner() throws IOException, UseCaseExecutionException {
        DataProductVersion dataProductVersion = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("quality_upload_data_product_version.json")),
                DataProductVersion.class
        );

        List<QualityCheck> qualityChecks = dataProductVersion.getInterfaceComponents()
                .getOutputPorts()
                .stream()
                .map(Port::getPromises)
                .map(Promises::getApi)
                .flatMap(def -> portStandardDefinitionAnalyzer.extractQualityChecks(def)
                        .stream()).collect(Collectors.toList());

        // Clear existing issue policies and add new one without owner and without reporter (should default to data product owner for assignee, null for reporter)
        for (QualityCheck qualityCheck : qualityChecks) {
            qualityCheck.getIssuePolicies().clear(); // Clear existing issue policies
            BDIssuePolicyRes issuePolicy = new BDIssuePolicyRes();
            issuePolicy.setName("Test Policy");
            
            BDIssueRes issueTemplate = new BDIssueRes();
            issueTemplate.setName("Test Issue");
            // No assignee set - should default to data product owner
            // No reporter set - should default to null
            
            issuePolicy.setIssueTemplate(issueTemplate);
            qualityCheck.getIssuePolicies().add(issuePolicy);
        }

        when(odmOutboundPort.getDataProductVersion()).thenReturn(dataProductVersion);
        when(odmOutboundPort.extractQualityChecks(any())).thenReturn(qualityChecks);

        // Mock data product owner
        BDShortUserRes dataProductOwner = new BDShortUserRes();
        dataProductOwner.setUsername("owner@default.blindata.io");
        dataProductOwner.setUuid("dpo-uuid");
        
        when(blindataOutboundPort.findUser("owner@default.blindata.io")).thenReturn(Optional.of(dataProductOwner));
        when(blindataOutboundPort.findIssueCampaign(any())).thenReturn(Optional.empty());
        when(blindataOutboundPort.createIssueCampaign(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(blindataOutboundPort.uploadQuality(any(), any())).thenReturn(new BDQualityUploadResultsRes());

        new QualityUpload(blindataOutboundPort, odmOutboundPort).execute();

        verify(blindataOutboundPort, times(1)).uploadQuality(any(), any());
        
        // Verify that the assignee was set to the data product owner
        // and that the reporter defaults to null
        ArgumentCaptor<BDQualitySuiteRes> qualitySuiteArg = ArgumentCaptor.forClass(BDQualitySuiteRes.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<QualityCheck>> qualityChecksArg = ArgumentCaptor.forClass(List.class);
        verify(blindataOutboundPort, times(1))
                .uploadQuality(qualitySuiteArg.capture(), qualityChecksArg.capture());
        
        List<BDIssuePolicyRes> issuePolicies = qualityChecksArg.getValue().stream()
                .flatMap(qualityCheck -> qualityCheck.getIssuePolicies().stream())
                .collect(Collectors.toList());
        
        assertThat(issuePolicies).hasSize(2); // 2 quality checks, each with 1 issue policy
        assertThat(issuePolicies.get(0).getIssueTemplate().getAssignee()).isNotNull();
        assertThat(issuePolicies.get(0).getIssueTemplate().getAssignee().getUsername()).isEqualTo("owner@default.blindata.io");
        assertThat(issuePolicies.get(0).getIssueTemplate().getReporter()).isNull(); // Reporter defaults to null when missing
        assertThat(issuePolicies.get(1).getIssueTemplate().getAssignee()).isNotNull();
        assertThat(issuePolicies.get(1).getIssueTemplate().getAssignee().getUsername()).isEqualTo("owner@default.blindata.io");
        assertThat(issuePolicies.get(1).getIssueTemplate().getReporter()).isNull(); // Reporter defaults to null when missing
    }

    @Test
    public void testQualityUploadWithIssueOwnerSetToNone() throws IOException, UseCaseExecutionException {
        DataProductVersion dataProductVersion = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("quality_upload_data_product_version.json")),
                DataProductVersion.class
        );

        List<QualityCheck> qualityChecks = dataProductVersion.getInterfaceComponents()
                .getOutputPorts()
                .stream()
                .map(Port::getPromises)
                .map(Promises::getApi)
                .flatMap(def -> portStandardDefinitionAnalyzer.extractQualityChecks(def)
                        .stream()).collect(Collectors.toList());

        // Clear existing issue policies and add new one with owner set to "None"
        for (QualityCheck qualityCheck : qualityChecks) {
            qualityCheck.getIssuePolicies().clear(); // Clear existing issue policies
            BDIssuePolicyRes issuePolicy = new BDIssuePolicyRes();
            issuePolicy.setName("Test Policy");
            
            BDIssueRes issueTemplate = new BDIssueRes();
            issueTemplate.setName("Test Issue");
            
            BDShortUserRes owner = new BDShortUserRes();
            owner.setUsername("None");
            issueTemplate.setAssignee(owner);
            
            issuePolicy.setIssueTemplate(issueTemplate);
            qualityCheck.getIssuePolicies().add(issuePolicy);
        }

        when(odmOutboundPort.getDataProductVersion()).thenReturn(dataProductVersion);
        when(odmOutboundPort.extractQualityChecks(any())).thenReturn(qualityChecks);

        // No need to mock findUser("None") because "None" handling logic sets assignee to null before validation
        when(blindataOutboundPort.findIssueCampaign(any())).thenReturn(Optional.empty());
        when(blindataOutboundPort.createIssueCampaign(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(blindataOutboundPort.uploadQuality(any(), any())).thenReturn(new BDQualityUploadResultsRes());

        new QualityUpload(blindataOutboundPort, odmOutboundPort).execute();

        verify(blindataOutboundPort, times(1)).uploadQuality(any(), any());
        
        // Verify that the assignee was set to null (not assigned)
        ArgumentCaptor<BDQualitySuiteRes> qualitySuiteArg = ArgumentCaptor.forClass(BDQualitySuiteRes.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<QualityCheck>> qualityChecksArg = ArgumentCaptor.forClass(List.class);
        verify(blindataOutboundPort, times(1))
                .uploadQuality(qualitySuiteArg.capture(), qualityChecksArg.capture());
        
        List<BDIssuePolicyRes> issuePolicies = qualityChecksArg.getValue().stream()
                .flatMap(qualityCheck -> qualityCheck.getIssuePolicies().stream())
                .collect(Collectors.toList());
        
        assertThat(issuePolicies).hasSize(2); // 2 quality checks, each with 1 issue policy
        assertThat(issuePolicies.get(0).getIssueTemplate().getAssignee()).isNull();
        assertThat(issuePolicies.get(1).getIssueTemplate().getAssignee()).isNull();
    }

    static class QualityExpectedResult {
        private BDQualitySuiteRes qualitySuite;
        private List<BDIssuePolicyRes> issuePolicies;
        private Set<String> qualityChecksCodes;

        public QualityExpectedResult() {
            //DO NOTHING
        }

        public BDQualitySuiteRes getQualitySuite() {
            return qualitySuite;
        }

        public void setQualitySuite(BDQualitySuiteRes qualitySuite) {
            this.qualitySuite = qualitySuite;
        }

        public List<BDIssuePolicyRes> getIssuePolicies() {
            return issuePolicies;
        }

        public void setIssuePolicies(List<BDIssuePolicyRes> issuePolicies) {
            this.issuePolicies = issuePolicies;
        }

        public Set<String> getQualityChecksCodes() {
            return qualityChecksCodes;
        }

        public void setQualityChecksCodes(Set<String> qualityChecksCodes) {
            this.qualityChecksCodes = qualityChecksCodes;
        }
    }
}
