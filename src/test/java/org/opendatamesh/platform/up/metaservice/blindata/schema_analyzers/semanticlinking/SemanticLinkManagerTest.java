package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdSemanticLinkingClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical.BDDataCategoryRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical.BDLogicalNamespaceRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical.BDLogicalFieldSemanticLinkRes;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class SemanticLinkManagerTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private BdSemanticLinkingClient mockSemanticLinkingClient;

    @InjectMocks
    private SemanticLinkManagerImpl semanticLinkManager;

    @Test
    void testEnrichWithSemanticContext() throws IOException {
        // set up
        setUpMocks();
        Map<String, Object> sContext = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("testSemanticLinking_semanticContext.json")),
                Map.class
        );
        BDPhysicalEntityRes physicalEntity = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("testSemanticLinking_rawPhysicalEntity.json")),
                BDPhysicalEntityRes.class
        );
        BDPhysicalEntityRes expectedPhysicalEntity = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("testSemanticLinking_expectedPhysicalEntity.json")),
                BDPhysicalEntityRes.class
        );

        semanticLinkManager.enrichWithSemanticContext(physicalEntity, sContext);
        assertThat(physicalEntity).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expectedPhysicalEntity);
    }

    private void setUpMocks() throws IOException {
        // Load mock response data from JSON
        Map<String, BDLogicalNamespaceRes> namespaces = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("testSemanticLinking_mockedBlindataResponses_namespace.json")),
                new TypeReference<Map<String, BDLogicalNamespaceRes>>() {}
        );
        Map<String, BDDataCategoryRes> dataCategories = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("testSemanticLinking_mockedBlindataResponses_dataCategories.json")),
                new TypeReference<Map<String, BDDataCategoryRes>>() {}
        );
        Map<String, BDLogicalFieldSemanticLinkRes> semanticLinkElements = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("testSemanticLinking_mockedBlindataResponses_semanticLinkElements.json")),
                new TypeReference<Map<String, BDLogicalFieldSemanticLinkRes>>() {}
        );

        // Mock responses
        lenient().when(mockSemanticLinkingClient.getLogicalNamespaceByIdentifier(anyString()))
                .thenAnswer(invocation -> Optional.ofNullable(namespaces.get(invocation.getArgument(0))));

        lenient().when(mockSemanticLinkingClient.getDataCategoryByNameAndNamespaceUuid(anyString(), any()))
                .thenAnswer(invocation -> Optional.ofNullable(dataCategories.get(invocation.getArgument(0))));

        lenient().when(mockSemanticLinkingClient.getSemanticLinkElements(anyString(), anyString()))
                .thenAnswer(invocation -> semanticLinkElements.get(invocation.getArgument(0)));
    }
}
