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
import org.opendatamesh.platform.up.metaservice.blindata.configurations.BdDataProductConfig;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking.SemanticLinkManager;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortDataStoreApiEntitiesExtractorTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private SemanticLinkManager mockSemanticLinkManager;

    @Mock
    private BdDataProductConfig mockBDDataProductConfig;

    @InjectMocks
    private PortDatastoreApiEntitiesExtractor portStandardDefinitionAnalyzer;

    @Test
    void testDatastoreApiV1AnalyzerSingleEntity() throws IOException {
        StandardDefinition portStandardDefinition = new StandardDefinition();
        portStandardDefinition.setSpecification("datastoreapi");
        portStandardDefinition.setSpecificationVersion("1.0.0");
        portStandardDefinition.setDefinition(loadJsonResource("testDataStoreApiV1Analyzer_singleEntitySchema_rawPortStandardDefinition.json"));
        assertThat(portStandardDefinitionAnalyzer.supports(portStandardDefinition)).isTrue();

        List<BDPhysicalEntityRes> extractedEntities =
                portStandardDefinitionAnalyzer.extractEntities(portStandardDefinition);
        List<BDPhysicalEntityRes> expectedEntities =
                loadExpectedEntities("testDataStoreApiV1Analyzer_singleEntitySchema_expectedEntities.json");
        assertThat(extractedEntities)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFields("physicalFields.physicalEntity")
                .isEqualTo(expectedEntities);
    }

    @Test
    void testDatastoreApiV1AnalyzerMultipleEntities() throws IOException {
        StandardDefinition portStandardDefinition = new StandardDefinition();
        portStandardDefinition.setSpecification("datastoreapi");
        portStandardDefinition.setSpecificationVersion("1.0.0");
        portStandardDefinition.setDefinition(loadJsonResource("testDataStoreApiV1Analyzer_multipleEntitiesSchema_rawPortStandardDefinition.json"));
        assertThat(portStandardDefinitionAnalyzer.supports(portStandardDefinition)).isTrue();

        List<BDPhysicalEntityRes> extractedEntities =
                portStandardDefinitionAnalyzer.extractEntities(portStandardDefinition);
        List<BDPhysicalEntityRes> expectedEntities =
                loadExpectedEntities("testDataStoreApiV1Analyzer_multipleEntitiesSchema_expectedEntities.json");
        assertThat(extractedEntities)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFields("physicalFields.physicalEntity")
                .isEqualTo(expectedEntities);
    }

    @Test
    void testDatastoreApiV1AnalyzerWithCustomAdditionalPropertiesRegex() throws Exception {
        when(mockBDDataProductConfig.getAdditionalPropertiesRegex()).thenReturn("\\bcustom-([\\S]+)");

        StandardDefinition portStandardDefinition = new StandardDefinition();
        portStandardDefinition.setSpecification("datastoreapi");
        portStandardDefinition.setSpecificationVersion("1.0.0");
        portStandardDefinition.setDefinition(loadJsonResource("testDataStoreApiV1Analyzer_customProperties_rawPortStandardDefinition.json"));

        List<BDPhysicalEntityRes> actualEntities = portStandardDefinitionAnalyzer.extractEntities(portStandardDefinition);

        List<BDPhysicalEntityRes> expectedEntities = loadExpectedEntities("testDataStoreApiV1Analyzer_customProperties_expectedEntities.json");
        assertThat(actualEntities)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFields("physicalFields.physicalEntity")
                .isEqualTo(expectedEntities);
    }

    private ComponentBase loadJsonResource(String resourcePath) throws IOException {
        return objectMapper.readValue(
                Resources.toByteArray(getClass().getResource(resourcePath)),
                ComponentBase.class
        );
    }

    private List<BDPhysicalEntityRes> loadExpectedEntities(String resourcePath) throws IOException {
        return objectMapper.readValue(
                Resources.toByteArray(getClass().getResource(resourcePath)),
                Entities.class
        ).physicalEntities;
    }

    private static class Entities {
        private List<BDPhysicalEntityRes> physicalEntities;

        public List<BDPhysicalEntityRes> getPhysicalEntities() {
            return physicalEntities;
        }

        public void setPhysicalEntities(List<BDPhysicalEntityRes> physicalEntities) {
            this.physicalEntities = physicalEntities;
        }
    }
}

