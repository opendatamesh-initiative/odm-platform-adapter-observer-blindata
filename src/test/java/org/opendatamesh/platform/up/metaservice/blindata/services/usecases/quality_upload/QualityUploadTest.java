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
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDUploadResultsMessage;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt.BDIssuePolicyRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualitySuiteRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1.PortDatastoreApiEntitiesExtractor;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking.SemanticLinkManager;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
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

        when(blindataOutboundPort.findDataProductOwner(any())).thenReturn(Optional.empty());
        when(blindataOutboundPort.findIssueCampaign(any())).thenReturn(Optional.empty());
        when(blindataOutboundPort.createIssueCampaign(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(blindataOutboundPort.saveQualityDefinitions(any(), any())).thenReturn(new BDUploadResultsMessage());

        new QualityUpload(blindataOutboundPort, odmOutboundPort).execute();

        verify(odmOutboundPort, times(1)).extractQualityChecks(any());
        verify(blindataOutboundPort, times(1)).findDataProductOwner(any());
        verify(blindataOutboundPort, times(1)).findIssueCampaign(any());
        verify(blindataOutboundPort, times(1)).createIssueCampaign(any());

        ArgumentCaptor<BDQualitySuiteRes> qualitySuiteArg = ArgumentCaptor.forClass(BDQualitySuiteRes.class);
        ArgumentCaptor<List<QualityCheck>> qualityChecksArg = ArgumentCaptor.forClass(List.class);

        verify(blindataOutboundPort, times(1))
                .saveQualityDefinitions(qualitySuiteArg.capture(), qualityChecksArg.capture());

        QualityExpectedResult expectedQualityUpload = objectMapper.readValue(Resources.toByteArray(getClass().getResource("quality_upload_expected_results.json")), QualityExpectedResult.class);

        assertThat(qualitySuiteArg.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expectedQualityUpload.getQualitySuite());

        assertThat(qualityChecksArg.getValue()).hasSize(2);
        assertThat(qualityChecksArg.getValue().stream().flatMap(qualityCheck -> qualityCheck.getIssuePolicies().stream()).collect(Collectors.toList()))
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFields("policyContent")
                .isEqualTo(expectedQualityUpload.getIssuePolicies());

    }

    static class QualityExpectedResult {
        private BDQualitySuiteRes qualitySuite;
        private List<BDIssuePolicyRes> issuePolicies;

        public QualityExpectedResult() {
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
    }
}
