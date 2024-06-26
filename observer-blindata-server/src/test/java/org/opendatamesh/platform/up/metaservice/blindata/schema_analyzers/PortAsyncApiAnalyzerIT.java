package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDPhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.v2.PortAsyncApi2Analyzer;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.v3.PortAsyncApi3Analyzer;

import java.io.IOException;
import java.util.List;

public class PortAsyncApiAnalyzerIT {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testAsyncApiV2Analyzer() throws IOException {
        String rawDefinition = objectMapper.readValue(
                Resources.toByteArray(
                        getClass().getResource("testAsyncApiV2Analyzer_rawPortStandardDefinition.json")
                ),
                JsonNode.class
        ).toString();

        PortStandardDefinition portStandardDefinition = new PortStandardDefinition();
        portStandardDefinition.setSpecification("asyncapi");
        portStandardDefinition.setSpecificationVersion("2.5.0");
        portStandardDefinition.setDefinition(rawDefinition);

        PortStandardDefinitionAnalyzer portStandardDefinitionAnalyzer = new PortAsyncApi2Analyzer();
        Assertions.assertThat(portStandardDefinitionAnalyzer.supportsPortStandardDefinition(portStandardDefinition)).isTrue();

        List<BDPhysicalEntityRes> extractedEntities = portStandardDefinitionAnalyzer.getBDAssetsFromPortStandardDefinition(portStandardDefinition);
        List<BDPhysicalEntityRes> expectedEntities = objectMapper.readValue(Resources.toByteArray(getClass().getResource("testAsyncApiV2Analyzer_expectedEntities.json")), Entities.class).physicalEntities;

        Assertions.assertThat(extractedEntities).containsExactlyInAnyOrderElementsOf(expectedEntities);
    }

    @Test
    public void testAsyncApiV3Analyzer() throws IOException {

        String rawDefinition = objectMapper.readValue(
                Resources.toByteArray(
                        getClass().getResource("testAsyncApiV3Analyzer_rawPortStandardDefinition.json")
                ),
                JsonNode.class
        ).toString();
        PortStandardDefinition portStandardDefinition = new PortStandardDefinition();
        portStandardDefinition.setSpecification("asyncapi");
        portStandardDefinition.setSpecificationVersion("3.0.0");
        portStandardDefinition.setDefinition(rawDefinition);

        PortStandardDefinitionAnalyzer portStandardDefinitionAnalyzer = new PortAsyncApi3Analyzer();
        Assertions.assertThat(portStandardDefinitionAnalyzer.supportsPortStandardDefinition(portStandardDefinition)).isTrue();

        List<BDPhysicalEntityRes> extractedEntities = portStandardDefinitionAnalyzer.getBDAssetsFromPortStandardDefinition(portStandardDefinition);
        List<BDPhysicalEntityRes> expectedEntities = objectMapper.readValue(Resources.toByteArray(getClass().getResource("testAsyncApiV3Analyzer_expectedEntities.json")), Entities.class).physicalEntities;

        Assertions.assertThat(extractedEntities).containsExactlyInAnyOrderElementsOf(expectedEntities);
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
