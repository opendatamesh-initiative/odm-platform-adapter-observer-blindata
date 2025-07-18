package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opendatamesh.dpds.model.core.ComponentBase;
import org.opendatamesh.dpds.model.core.StandardDefinition;
import org.opendatamesh.platform.up.metaservice.blindata.configurations.BdDataProductConfig;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt.BDIssuePolicyRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking.SemanticLinkManager;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLogger;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PortDataStoreApiQualityExtractorTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private SemanticLinkManager mockSemanticLinkManager;

    @Mock
    private BdDataProductConfig mockBDDataProductConfig;

    @InjectMocks
    private PortDatastoreApiEntitiesExtractor portStandardDefinitionAnalyzer;

    @Test
    void testDatastoreApiV1QualityExtractor() throws IOException {
        StandardDefinition portStandardDefinition = new StandardDefinition();
        portStandardDefinition.setSpecification("datastoreapi");
        portStandardDefinition.setSpecificationVersion("1.0.0");
        portStandardDefinition.setDefinition(objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("testDataStoreApiV1QualityExtractor_rawPortStandardDefinition.json")),
                ComponentBase.class
        ));
        assertThat(portStandardDefinitionAnalyzer.supports(portStandardDefinition)).isTrue();

        List<QualityCheck> extractedQualityChecks = portStandardDefinitionAnalyzer.extractQualityChecks(portStandardDefinition);

        ExpectedQualityChecks expectedQualityChecks = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("testDataStoreApiV1QualityExtractor_expected_quality_checks.json")),
                ExpectedQualityChecks.class
        );

        assertThat(extractedQualityChecks)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFields("physicalFields.physicalEntity.physicalFields")
                .ignoringFields("physicalEntities.physicalFields")
                .isEqualTo(expectedQualityChecks.getQualityChecks());
    }

    @Test
    void testDatastoreApiV1QualityExtractorWithRef() throws IOException {
        StandardDefinition portStandardDefinition = new StandardDefinition();
        portStandardDefinition.setSpecification("datastoreapi");
        portStandardDefinition.setSpecificationVersion("1.0.0");
        portStandardDefinition.setDefinition(objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("testDataStoreApiV1QualityExtractor_rawPortStandardDefinition_with_ref.json")),
                ComponentBase.class
        ));
        assertThat(portStandardDefinitionAnalyzer.supports(portStandardDefinition)).isTrue();

        List<QualityCheck> extractedQualityChecks = portStandardDefinitionAnalyzer.extractQualityChecks(portStandardDefinition);

        ExpectedQualityChecks expectedQualityChecks = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("testDataStoreApiV1QualityExtractor_with_ref_expected_quality_checks.json")),
                ExpectedQualityChecks.class
        );

        assertThat(extractedQualityChecks)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringExpectedNullFields()
                .ignoringActualNullFields()
                .ignoringFields("physicalFields.physicalEntity.physicalFields")
                .ignoringFields("physicalEntities.physicalFields")
                .isEqualTo(expectedQualityChecks.getQualityChecks());
    }

    @Test
    void testDatastoreApiV1QualityExtractorWithIssuePolicies() throws IOException {
        StandardDefinition portStandardDefinition = new StandardDefinition();
        portStandardDefinition.setSpecification("datastoreapi");
        portStandardDefinition.setSpecificationVersion("1.0.0");
        portStandardDefinition.setDefinition(objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("testDataStoreApiV1QualityExtractor_rawPortStandardDefinition_issue_policies.json")),
                ComponentBase.class
        ));
        assertThat(portStandardDefinitionAnalyzer.supports(portStandardDefinition)).isTrue();

        List<QualityCheck> extractedQualityChecks = portStandardDefinitionAnalyzer.extractQualityChecks(portStandardDefinition);

        ExpectedQualityChecks expectedQualityChecks = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("testDataStoreApiV1QualityExtractor_expected_results_issue_policies.json")),
                ExpectedQualityChecks.class
        );

        List<BDIssuePolicyRes> issuePolicies = extractedQualityChecks.stream().flatMap(qualityCheck -> qualityCheck.getIssuePolicies().stream()).collect(Collectors.toList());
        List<BDIssuePolicyRes> expectedIssuePolicies = expectedQualityChecks.getQualityChecks().stream().flatMap(qualityCheck -> qualityCheck.getIssuePolicies().stream()).collect(Collectors.toList());

        assertThat(issuePolicies)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFields("policyContent")
                .isEqualTo(expectedIssuePolicies);
    }

    @Test
    void testDatastoreApiV1QualityExtractorIssue90() throws IOException {
        // Backup the original logger
        UseCaseLogger originalLogger = UseCaseLoggerContext.getUseCaseLogger();
        // Create and set mock logger
        UseCaseLogger mockLogger = mock(UseCaseLogger.class);
        UseCaseLoggerContext.setUseCaseLogger(mockLogger);
        try {
            StandardDefinition portStandardDefinition = new StandardDefinition();
            portStandardDefinition.setSpecification("datastoreapi");
            portStandardDefinition.setSpecificationVersion("1.0.0");
            portStandardDefinition.setDefinition(objectMapper.readValue(
                    Resources.toByteArray(getClass().getResource("testDataStoreApiV1QualityExtractor_rawPortStandardDefinition_issue90.json")),
                    ComponentBase.class
            ));
            assertThat(portStandardDefinitionAnalyzer.supports(portStandardDefinition)).isTrue();
            // Verify no null pointer exception is thrown
            assertThatCode(() -> portStandardDefinitionAnalyzer.extractQualityChecks(portStandardDefinition))
                    .doesNotThrowAnyException();
            // Capture and verify the log
            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            verify(mockLogger).warn(captor.capture());
            assertThat(captor.getValue()).isEqualTo("Quality object inside datastoreApi is not valid: {\"customProperties\":{\"issuePolicies\":[],\"ref\":\"sapIess-PrezzoSbilanciamento-PrezzoBase-ExpectColumnPairValuesAToBeGreaterThanB\"}}");
        } finally {
            // Restore the original logger after the test
            UseCaseLoggerContext.setUseCaseLogger(originalLogger);
        }
    }


    private static class ExpectedQualityChecks {
        private List<QualityCheck> qualityChecks = new ArrayList<>();

        public ExpectedQualityChecks() {
        }

        public List<QualityCheck> getQualityChecks() {
            return qualityChecks;
        }

        public void setQualityChecks(List<QualityCheck> qualityChecks) {
            this.qualityChecks = qualityChecks;
        }
    }
}

