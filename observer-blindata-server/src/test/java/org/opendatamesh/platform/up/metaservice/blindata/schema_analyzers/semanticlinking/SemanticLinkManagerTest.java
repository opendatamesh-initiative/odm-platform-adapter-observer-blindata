package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class SemanticLinkManagerTest {

    private ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void testEnrichPhysicalPropertiesWithSemanticLinks() throws IOException {

        SemanticLinkingManagerInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(
                        getClass().getResource("semanticLinkingInitialState.json")
                ),
                SemanticLinkingManagerInitialState.class
        );
        SemanticLinkManager semanticLinkManager = new SemanticLinkManagerImpl(new BDSemanticLinkingClientMock());
        semanticLinkManager.enrichPhysicalPropertiesWithSemanticLinks(initialState.getSContext(), initialState.getPhysicalEntityRes());
        Assertions.assertThat(initialState.getPhysicalEntityRes().getPhysicalFields().size()).isEqualTo(1);
        Assertions.assertThat(initialState.getPhysicalEntityRes().getDataCategories().size()).isEqualTo(1);

    }
}
