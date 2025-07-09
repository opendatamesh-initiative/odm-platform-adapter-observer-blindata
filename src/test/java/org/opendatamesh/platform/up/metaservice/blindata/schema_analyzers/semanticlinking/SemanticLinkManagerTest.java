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
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical.BDLogicalFieldSemanticLinkRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical.BDLogicalNamespaceRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalFieldRes;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

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

    @Test
    void testSemanticPathResolutionWithStockContext() {
        // Test the specific case mentioned in the requirement
        Map<String, Object> sContext = Map.of(
                "s-base", "https://demo.blindata.io/logical/namespaces/name/logistics#",
                "s-type", "[Stock]",
                "sku_id", "refersTo[lux:ProductSku].lux:productSkuIdentifier",
                "site_cod", "refersTo[lux:Site].lux:siteCode",
                "ownership_cod", "refersTo[lux:ProductSku].refersTo[lux:ProductStoreProperties].productOwnershipCode",
                "store_cod", "refersTo[lux:Site].isLocatedWithin[lux:Store].storeCode",
                "stock_qty", "stockQuantity",
                "reserved_qty", "reservedQuantity",
                "available_qty", "availableQuantity",
                "stock_datetime", "stockDatetime"
        );

        // Create a simple physical entity with the fields
        BDPhysicalEntityRes physicalEntity = new BDPhysicalEntityRes();
        physicalEntity.setPhysicalFields(Set.of(
                createPhysicalField("sku_id"),
                createPhysicalField("site_cod"),
                createPhysicalField("ownership_cod"),
                createPhysicalField("store_cod"),
                createPhysicalField("stock_qty"),
                createPhysicalField("reserved_qty"),
                createPhysicalField("available_qty"),
                createPhysicalField("stock_datetime")
        ));

        // Mock the client responses
        BDLogicalNamespaceRes namespace = new BDLogicalNamespaceRes();
        namespace.setUuid("test-namespace-uuid");
        lenient().when(mockSemanticLinkingClient.getLogicalNamespaceByIdentifier("https://demo.blindata.io/logical/namespaces/name/logistics#"))
                .thenReturn(Optional.of(namespace));

        BDDataCategoryRes dataCategory = new BDDataCategoryRes();
        dataCategory.setName("Stock");
        lenient().when(mockSemanticLinkingClient.getDataCategoryByNameAndNamespaceUuid("Stock", "test-namespace-uuid"))
                .thenReturn(Optional.of(dataCategory));

        // Mock semantic link resolution for each field
        BDLogicalFieldSemanticLinkRes mockSemanticLink = new BDLogicalFieldSemanticLinkRes();
        lenient().when(mockSemanticLinkingClient.getSemanticLinkElements(anyString(), anyString()))
                .thenReturn(mockSemanticLink);

        // Execute the method
        semanticLinkManager.enrichWithSemanticContext(physicalEntity, sContext);

        // Verify that the correct semantic paths were called
        // All fields should be prefixed with [Stock]. unless already present
        verify(mockSemanticLinkingClient).getSemanticLinkElements("[Stock].refersTo[lux:ProductSku].lux:productSkuIdentifier", "https://demo.blindata.io/logical/namespaces/name/logistics#");
        verify(mockSemanticLinkingClient).getSemanticLinkElements("[Stock].refersTo[lux:Site].lux:siteCode", "https://demo.blindata.io/logical/namespaces/name/logistics#");
        verify(mockSemanticLinkingClient).getSemanticLinkElements("[Stock].refersTo[lux:ProductSku].refersTo[lux:ProductStoreProperties].productOwnershipCode", "https://demo.blindata.io/logical/namespaces/name/logistics#");
        verify(mockSemanticLinkingClient).getSemanticLinkElements("[Stock].refersTo[lux:Site].isLocatedWithin[lux:Store].storeCode", "https://demo.blindata.io/logical/namespaces/name/logistics#");
        verify(mockSemanticLinkingClient).getSemanticLinkElements("[Stock].stockQuantity", "https://demo.blindata.io/logical/namespaces/name/logistics#");
        verify(mockSemanticLinkingClient).getSemanticLinkElements("[Stock].reservedQuantity", "https://demo.blindata.io/logical/namespaces/name/logistics#");
        verify(mockSemanticLinkingClient).getSemanticLinkElements("[Stock].availableQuantity", "https://demo.blindata.io/logical/namespaces/name/logistics#");
        verify(mockSemanticLinkingClient).getSemanticLinkElements("[Stock].stockDatetime", "https://demo.blindata.io/logical/namespaces/name/logistics#");
    }

    private void setUpMocks() throws IOException {
        // Load mock response data from JSON
        Map<String, BDLogicalNamespaceRes> namespaces = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("testSemanticLinking_mockedBlindataResponses_namespace.json")),
                new TypeReference<Map<String, BDLogicalNamespaceRes>>() {
                }
        );
        Map<String, BDDataCategoryRes> dataCategories = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("testSemanticLinking_mockedBlindataResponses_dataCategories.json")),
                new TypeReference<Map<String, BDDataCategoryRes>>() {
                }
        );
        Map<String, BDLogicalFieldSemanticLinkRes> semanticLinkElements = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("testSemanticLinking_mockedBlindataResponses_semanticLinkElements.json")),
                new TypeReference<Map<String, BDLogicalFieldSemanticLinkRes>>() {
                }
        );

        // Mock responses
        lenient().when(mockSemanticLinkingClient.getLogicalNamespaceByIdentifier(anyString()))
                .thenAnswer(invocation -> Optional.ofNullable(namespaces.get(invocation.getArgument(0))));

        lenient().when(mockSemanticLinkingClient.getDataCategoryByNameAndNamespaceUuid(anyString(), any()))
                .thenAnswer(invocation -> Optional.ofNullable(dataCategories.get(invocation.getArgument(0))));

        lenient().when(mockSemanticLinkingClient.getSemanticLinkElements(anyString(), anyString()))
                .thenAnswer(invocation -> semanticLinkElements.get(invocation.getArgument(0)));
    }

    private BDPhysicalFieldRes createPhysicalField(String name) {
        BDPhysicalFieldRes field = new BDPhysicalFieldRes();
        field.setName(name);
        field.setUuid("test-uuid-" + name);
        return field;
    }
}
