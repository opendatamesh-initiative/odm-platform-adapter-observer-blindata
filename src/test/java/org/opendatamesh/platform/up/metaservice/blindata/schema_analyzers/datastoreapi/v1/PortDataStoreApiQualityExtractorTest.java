package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opendatamesh.dpds.model.core.ComponentBase;
import org.opendatamesh.dpds.model.core.StandardDefinition;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking.SemanticLinkManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PortDataStoreApiQualityExtractorTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private SemanticLinkManager mockSemanticLinkManager;

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

