package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opendatamesh.dpds.model.core.ComponentBase;
import org.opendatamesh.dpds.model.core.StandardDefinition;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.PortStandardDefinitionEntitiesExtractor;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.v2.PortAsyncApi2EntitiesExtractor;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.v3.PortAsyncApi3EntitiesExtractor;

import java.io.IOException;
import java.util.List;

public class PortAsyncApiAnalyzerTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testAsyncApiV2AvroAnalyzer() throws IOException {
        ComponentBase rawDefinition = objectMapper.readValue(
                Resources.toByteArray(
                        getClass().getResource("testAsyncApiV2_Avro_Analyzer_rawPortStandardDefinition.json")
                ),
                ComponentBase.class
        );

        StandardDefinition portStandardDefinition = new StandardDefinition();
        portStandardDefinition.setSpecification("asyncapi");
        portStandardDefinition.setSpecificationVersion("2.5.0");
        portStandardDefinition.setDefinition(rawDefinition);

        PortStandardDefinitionEntitiesExtractor portStandardDefinitionEntitiesExtractor = new PortAsyncApi2EntitiesExtractor();
        Assertions.assertThat(portStandardDefinitionEntitiesExtractor.supports(portStandardDefinition)).isTrue();

        List<BDPhysicalEntityRes> extractedEntities = portStandardDefinitionEntitiesExtractor.extractEntities(portStandardDefinition);
        List<BDPhysicalEntityRes> expectedEntities = objectMapper.readValue(Resources.toByteArray(getClass().getResource("testAsyncApiV2_Avro_Analyzer_expectedEntities.json")), Entities.class).physicalEntities;

        Assertions.assertThat(extractedEntities).containsExactlyInAnyOrderElementsOf(expectedEntities);
    }

    @Test
    public void testAsyncApiV2JsonAnalyzer() throws IOException {
        ComponentBase rawDefinition = objectMapper.readValue(
                Resources.toByteArray(
                        getClass().getResource("testAsyncApiV2_Json_Analyzer_rawPortStandardDefinition.json")
                ),
                ComponentBase.class
        );

        StandardDefinition portStandardDefinition = new StandardDefinition();
        portStandardDefinition.setSpecification("asyncapi");
        portStandardDefinition.setSpecificationVersion("2.5.0");
        portStandardDefinition.setDefinition(rawDefinition);

        PortStandardDefinitionEntitiesExtractor portStandardDefinitionEntitiesExtractor = new PortAsyncApi2EntitiesExtractor();
        Assertions.assertThat(portStandardDefinitionEntitiesExtractor.supports(portStandardDefinition)).isTrue();

        List<BDPhysicalEntityRes> extractedEntities = portStandardDefinitionEntitiesExtractor.extractEntities(portStandardDefinition);
        List<BDPhysicalEntityRes> expectedEntities = objectMapper.readValue(Resources.toByteArray(getClass().getResource("testAsyncApiV2_Json_Analyzer_expectedEntities.json")), Entities.class).physicalEntities;

        Assertions.assertThat(extractedEntities).containsExactlyInAnyOrderElementsOf(expectedEntities);
    }

    @Test
    public void testAsyncApiV3AvroAnalyzer() throws IOException {

        ComponentBase rawDefinition = objectMapper.readValue(
                Resources.toByteArray(
                        getClass().getResource("testAsyncApiV3_Avro_Analyzer_rawPortStandardDefinition.json")
                ),
                ComponentBase.class
        );
        StandardDefinition portStandardDefinition = new StandardDefinition();
        portStandardDefinition.setSpecification("asyncapi");
        portStandardDefinition.setSpecificationVersion("3.0.0");
        portStandardDefinition.setDefinition(rawDefinition);

        PortStandardDefinitionEntitiesExtractor portStandardDefinitionEntitiesExtractor = new PortAsyncApi3EntitiesExtractor();
        Assertions.assertThat(portStandardDefinitionEntitiesExtractor.supports(portStandardDefinition)).isTrue();

        List<BDPhysicalEntityRes> extractedEntities = portStandardDefinitionEntitiesExtractor.extractEntities(portStandardDefinition);
        List<BDPhysicalEntityRes> expectedEntities = objectMapper.readValue(Resources.toByteArray(getClass().getResource("testAsyncApiV3_Avro_Analyzer_expectedEntities.json")), Entities.class).physicalEntities;

        Assertions.assertThat(extractedEntities).containsExactlyInAnyOrderElementsOf(expectedEntities);
    }

    @Test
    public void testAsyncApiV3JsonAnalyzer() throws IOException {
        ComponentBase rawDefinition = objectMapper.readValue(
                Resources.toByteArray(
                        getClass().getResource("testAsyncApiV3_Json_Analyzer_rawPortStandardDefinition.json")
                ),
                ComponentBase.class
        );

        StandardDefinition portStandardDefinition = new StandardDefinition();
        portStandardDefinition.setSpecification("asyncapi");
        portStandardDefinition.setSpecificationVersion("3.0.0");
        portStandardDefinition.setDefinition(rawDefinition);

        PortStandardDefinitionEntitiesExtractor portStandardDefinitionEntitiesExtractor = new PortAsyncApi3EntitiesExtractor();
        Assertions.assertThat(portStandardDefinitionEntitiesExtractor.supports(portStandardDefinition)).isTrue();

        List<BDPhysicalEntityRes> extractedEntities = portStandardDefinitionEntitiesExtractor.extractEntities(portStandardDefinition);
        List<BDPhysicalEntityRes> expectedEntities = objectMapper.readValue(Resources.toByteArray(getClass().getResource("testAsyncApiV3_Json_Analyzer_expectedEntities.json")), Entities.class).physicalEntities;

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
