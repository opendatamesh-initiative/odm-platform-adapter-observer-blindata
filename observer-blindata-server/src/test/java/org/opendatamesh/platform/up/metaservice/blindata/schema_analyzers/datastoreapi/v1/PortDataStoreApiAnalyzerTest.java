package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDPhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.PortStandardDefinition;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.PortStandardDefinitionAnalyzer;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking.SemanticLinkManager;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking.SemanticLinkingManagerInitialState;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking.SemanticLinkingManagerMockFactory;

import java.io.IOException;
import java.util.List;

public class PortDataStoreApiAnalyzerTest {
    private ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void testDatastoreApiV0AnalyzerSingleEntity() throws IOException {
        SemanticLinkingManagerMockFactory mockFactory = new SemanticLinkingManagerMockFactory();

        String rawDefinition = objectMapper.readValue(
                Resources.toByteArray(
                        getClass().getResource("testDataStoreApiV0Analyzer_singleEntitySchema_rawPortStandardDefinition.json")
                ),
                JsonNode.class
        ).toString();

        PortStandardDefinition portStandardDefinition = new PortStandardDefinition();
        portStandardDefinition.setSpecification("datastoreapi");
        portStandardDefinition.setSpecificationVersion("1.0.0");
        portStandardDefinition.setDefinition(rawDefinition);

        final SemanticLinkManager semanticLinkManager = mockFactory.buildSemanticLinkingManagerMock(new SemanticLinkingManagerInitialState());
        PortStandardDefinitionAnalyzer portStandardDefinitionAnalyzer = new PortDatastoreApiAnalyzer(semanticLinkManager);
        Assertions.assertThat(portStandardDefinitionAnalyzer.supportsPortStandardDefinition(portStandardDefinition)).isTrue();

        List<BDPhysicalEntityRes> extractedEntities = portStandardDefinitionAnalyzer.getBDAssetsFromPortStandardDefinition(portStandardDefinition);
        List<BDPhysicalEntityRes> expectedEntities = objectMapper.readValue(Resources.toByteArray(getClass().getResource("testDataStoreApiV0Analyzer_singleEntitySchema_expectedEntities.json")), Entities.class).physicalEntities;

        Assertions.assertThat(extractedEntities).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expectedEntities);
    }


    @Test
    public void testDatastoreApiV0AnalyzerMultipleEntities() throws IOException {
        SemanticLinkingManagerMockFactory mockFactory = new SemanticLinkingManagerMockFactory();

        String rawDefinition = objectMapper.readValue(
                Resources.toByteArray(
                        getClass().getResource("testDataStoreApiV0Analyzer_multipleEntitiesSchema_rawPortStandardDefinition.json")
                ),
                JsonNode.class
        ).toString();

        PortStandardDefinition portStandardDefinition = new PortStandardDefinition();
        portStandardDefinition.setSpecification("datastoreapi");
        portStandardDefinition.setSpecificationVersion("1.0.0");
        portStandardDefinition.setDefinition(rawDefinition);

        PortStandardDefinitionAnalyzer portStandardDefinitionAnalyzer = new PortDatastoreApiAnalyzer(mockFactory.buildSemanticLinkingManagerMock(null));
        Assertions.assertThat(portStandardDefinitionAnalyzer.supportsPortStandardDefinition(portStandardDefinition)).isTrue();

        List<BDPhysicalEntityRes> extractedEntities = portStandardDefinitionAnalyzer.getBDAssetsFromPortStandardDefinition(portStandardDefinition);
        List<BDPhysicalEntityRes> expectedEntities = objectMapper.readValue(Resources.toByteArray(getClass().getResource("testDataStoreApiV0Analyzer_multipleEntitiesSchema_expectedEntities.json")), Entities.class).physicalEntities;

        Assertions.assertThat(extractedEntities).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expectedEntities);
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
