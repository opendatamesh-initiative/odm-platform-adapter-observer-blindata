package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDPhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.PortStandardDefinition;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking.SemanticLinkManager;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PortDataStoreApiAnalyzerTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private SemanticLinkManager mockSemanticLinkManager;

    @InjectMocks
    private PortDatastoreApiAnalyzer portStandardDefinitionAnalyzer;

    private PortStandardDefinition portStandardDefinition;

    @BeforeEach
    void setup() {
        portStandardDefinition = new PortStandardDefinition();
        portStandardDefinition.setSpecification("datastoreapi");
        portStandardDefinition.setSpecificationVersion("1.0.0");
    }

    @Test
    void testDatastoreApiV0AnalyzerSingleEntity() throws IOException {
        portStandardDefinition.setDefinition(loadJsonResource("testDataStoreApiV0Analyzer_singleEntitySchema_rawPortStandardDefinition.json"));
        assertThat(portStandardDefinitionAnalyzer.supportsPortStandardDefinition(portStandardDefinition)).isTrue();

        List<BDPhysicalEntityRes> extractedEntities =
                portStandardDefinitionAnalyzer.getBDAssetsFromPortStandardDefinition(portStandardDefinition);
        List<BDPhysicalEntityRes> expectedEntities =
                loadExpectedEntities("testDataStoreApiV0Analyzer_singleEntitySchema_expectedEntities.json");
        assertThat(extractedEntities).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expectedEntities);
    }

    @Test
    void testDatastoreApiV0AnalyzerMultipleEntities() throws IOException {
        portStandardDefinition.setDefinition(loadJsonResource("testDataStoreApiV0Analyzer_multipleEntitiesSchema_rawPortStandardDefinition.json"));
        assertThat(portStandardDefinitionAnalyzer.supportsPortStandardDefinition(portStandardDefinition)).isTrue();

        List<BDPhysicalEntityRes> extractedEntities =
                portStandardDefinitionAnalyzer.getBDAssetsFromPortStandardDefinition(portStandardDefinition);
        List<BDPhysicalEntityRes> expectedEntities =
                loadExpectedEntities("testDataStoreApiV0Analyzer_multipleEntitiesSchema_expectedEntities.json");
        assertThat(extractedEntities).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expectedEntities);
    }

    private String loadJsonResource(String resourcePath) throws IOException {
        return objectMapper.readValue(
                Resources.toByteArray(getClass().getResource(resourcePath)),
                JsonNode.class
        ).toString();
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

