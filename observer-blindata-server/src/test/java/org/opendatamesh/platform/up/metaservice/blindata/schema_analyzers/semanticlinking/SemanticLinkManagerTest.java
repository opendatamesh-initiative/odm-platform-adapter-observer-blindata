package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDPhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDPhysicalFieldRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDSemanticLinkElement;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        final Set<BDPhysicalFieldRes> expectedFields = expectedEntity.getPhysicalFields();
        final Set<BDSemanticLinkElement> semanticLinkElementsFromExpectedFields = expectedFields.stream().findFirst().get().getLogicalFields().get(0).getSemanticLink().getSemanticLinkElements();
        final Set<BDPhysicalFieldRes> enrichedFields = initialState.getPhysicalEntityRes().getPhysicalFields();
        final Set<BDSemanticLinkElement> semanticLinkElementsFromEnrichedFields = initialState.getPhysicalEntityRes().getPhysicalFields().stream().findFirst().get().getLogicalFields().get(0).getSemanticLink().getSemanticLinkElements();

        List<BDSemanticLinkElement> sortedSemanticLinkElementsFromExpectedFields = semanticLinkElementsFromExpectedFields.stream()
                .sorted(Comparator.comparingInt(BDSemanticLinkElement::getOrdinalPosition))
                .collect(Collectors.toList());

        List<BDSemanticLinkElement> sortedSemanticLinkElementsFromEnrichedFields = semanticLinkElementsFromEnrichedFields.stream()
                .sorted(Comparator.comparingInt(BDSemanticLinkElement::getOrdinalPosition))
                .collect(Collectors.toList());

        Assertions.assertThat(expectedFields.size()).isEqualTo(enrichedFields.size());
        objectMapper.writeValueAsString(sortedSemanticLinkElementsFromEnrichedFields).equals(objectMapper.writeValueAsString(sortedSemanticLinkElementsFromExpectedFields));

    }
}
