package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDPhysicalEntityRes;

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
        SemanticLinkManager semanticLinkManager = new SemanticLinkManagerImpl(new BDSemanticLinkingClientMock(initialState));
        semanticLinkManager.enrichPhysicalFieldsWithSemanticLinks(
                initialState.getSContext(),
                initialState.getPhysicalEntityRes()
        );
        BDPhysicalEntityRes expectedEntity = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("expectedPhysicalEntityResults.json")),
                BDPhysicalEntityRes.class
        );

        Assertions.assertThat(expectedEntity).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(initialState.getPhysicalEntityRes());

    }
}
