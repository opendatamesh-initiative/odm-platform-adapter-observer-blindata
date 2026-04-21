package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SemanticLinkManagerTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private BdSemanticLinkingClient mockSemanticLinkingClient;

    private SemanticLinkManagerImpl semanticLinkManager;

    @BeforeEach
    void createSemanticLinkManager() {
        semanticLinkManager = new SemanticLinkManagerImpl(mockSemanticLinkingClient);
    }

    /**
     * {@code agentspecs/specs/semantic_linking/prefixed_concept_resolution/spec.md} — Non-regression: film-rental
     * fixture (unprefixed roots, nested {@code s-type}, {@code [Company]} path); namespace mock includes UUID for
     * pre-resolution.
     */
    @Test
    void testEnrichWithSemanticContext() throws IOException {
        reset(mockSemanticLinkingClient);
        semanticLinkManager = new SemanticLinkManagerImpl(mockSemanticLinkingClient);
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

    /**
     * {@code agentspecs/specs/semantic_linking/prefixed_concept_resolution/spec.md}
     * —
     * Feature: Leading bracket concept with namespace prefix — Scenario: Prefixed
     * root concept resolves in the prefix namespace.
     */
    @Test
    void prefixedRootConceptResolvesInPrefixNamespace() throws IOException {
            reset(mockSemanticLinkingClient);
            BDLogicalNamespaceRes defaultNs = new BDLogicalNamespaceRes();
            defaultNs.setIdentifier("https://demo.blindata.io/logical/namespaces/name/filmRentalInc#");
            defaultNs.setUuid("film-rental-namespace-uuid");
            defaultNs.setPrefix("fri");

            BDLogicalNamespaceRes otherNs = new BDLogicalNamespaceRes();
            otherNs.setName("OtherOntology");
            otherNs.setDisplayName("Other ontology");
            otherNs.setIdentifier("https://demo.blindata.io/logical/namespaces/name/otherOntology#");
            otherNs.setUuid("other-namespace-uuid");
            otherNs.setPrefix("other");
            otherNs.setDescription("Separate logical namespace reached via prefix other");

            defaultNs.setName("FilmRentalInc");
            defaultNs.setDisplayName("Film Rental Inc");
            defaultNs.setDescription("Namespace for the business ontology of Film Rental Inc");

            when(mockSemanticLinkingClient.getLogicalNamespaceByIdentifier(defaultNs.getIdentifier()))
                            .thenReturn(Optional.of(defaultNs));
            when(mockSemanticLinkingClient.getLogicalNamespaceByPrefix("other")).thenReturn(Optional.of(otherNs));

            BDDataCategoryRes movie = new BDDataCategoryRes();
            movie.setName("Movie");
            movie.setDisplayName("Movie");
            movie.setDescription("Movie data such as title, release year, length, rating, etc.");
            movie.setNamespace(defaultNs);

            BDDataCategoryRes conceptB = new BDDataCategoryRes();
            conceptB.setName("ConceptB");
            conceptB.setDisplayName("Concept B");
            conceptB.setDescription("Concept used for cross-namespace prefixed path tests.");
            conceptB.setNamespace(otherNs);

            when(mockSemanticLinkingClient.getDataCategoryByNameAndNamespaceUuid(anyString(), anyString()))
                            .thenAnswer(invocation -> {
                                    String name = invocation.getArgument(0);
                                    String uuid = invocation.getArgument(1);
                                    if ("Movie".equals(name) && defaultNs.getUuid().equals(uuid)) {
                                            return Optional.of(movie);
                                    }
                                    if ("ConceptB".equals(name) && otherNs.getUuid().equals(uuid)) {
                                            return Optional.of(conceptB);
                                    }
                                    return Optional.empty();
                            });

            Map<String, BDLogicalFieldSemanticLinkRes> semanticLinkElements = objectMapper.readValue(
                            Resources.toByteArray(getClass().getResource(
                                            "testSemanticLinking_prefixedConcept_mockedBlindataResponses_semanticLinkElements.json")),
                            new TypeReference<Map<String, BDLogicalFieldSemanticLinkRes>>() {
                            });
            when(mockSemanticLinkingClient.getSemanticLinkElements(anyString(), anyString()))
                            .thenAnswer(invocation -> semanticLinkElements.get(invocation.getArgument(0)));

            Map<String, Object> sContext = objectMapper.readValue(
                            Resources.toByteArray(getClass()
                                            .getResource("testSemanticLinking_prefixedConcept_semanticContext.json")),
                            Map.class);
            BDPhysicalEntityRes physicalEntity = objectMapper.readValue(
                            Resources.toByteArray(getClass()
                                            .getResource("testSemanticLinking_prefixedConcept_rawPhysicalEntity.json")),
                            BDPhysicalEntityRes.class);

            semanticLinkManager.enrichWithSemanticContext(physicalEntity, sContext);

            verify(mockSemanticLinkingClient, never()).getDataCategoryByNameAndNamespaceUuid(eq("other:ConceptB"),
                            anyString());
            verify(mockSemanticLinkingClient).getDataCategoryByNameAndNamespaceUuid("ConceptB", otherNs.getUuid());
            verify(mockSemanticLinkingClient).getSemanticLinkElements(
                            "[other:ConceptB].other:attribute2FromConceptB",
                            defaultNs.getIdentifier());
            assertThat(physicalEntity.getDataCategories())
                            .extracting(BDDataCategoryRes::getName)
                            .containsExactlyInAnyOrder("Movie", "ConceptB");
            assertThat(physicalEntity.getDataCategories())
                            .filteredOn(dc -> "ConceptB".equals(dc.getName()))
                            .singleElement()
                            .extracting(BDDataCategoryRes::getNamespace)
                            .returns("other", ns -> ns.getPrefix());
            BDPhysicalFieldRes crossField = physicalEntity.getPhysicalFields().stream()
                            .filter(f -> "cross_field".equals(f.getName()))
                            .findFirst()
                            .orElseThrow();
            assertThat(crossField.getLogicalFields()).hasSize(1);
            assertThat(crossField.getLogicalFields().get(0).getSemanticLink().getSemanticLinkString())
                            .isEqualTo("[other:ConceptB].other:attribute2FromConceptB");
    }

    /**
     * {@code agentspecs/specs/semantic_linking/prefixed_concept_resolution/spec.md}
     * —
     * Feature: Namespace prefix resolution — Scenario: Unknown prefix surfaces a
     * clear outcome (no literal composite name lookup in default namespace).
     */
    @Test
    void unknownPrefixDoesNotLookupLiteralCompositeNameInDefaultNamespace() throws IOException {
            reset(mockSemanticLinkingClient);
            BDLogicalNamespaceRes defaultNs = new BDLogicalNamespaceRes();
            defaultNs.setIdentifier("https://demo.blindata.io/logical/namespaces/name/filmRentalInc#");
            defaultNs.setUuid("film-rental-namespace-uuid");
            defaultNs.setPrefix("fri");

            when(mockSemanticLinkingClient.getLogicalNamespaceByIdentifier(defaultNs.getIdentifier()))
                            .thenReturn(Optional.of(defaultNs));
            when(mockSemanticLinkingClient.getLogicalNamespaceByPrefix("unknown")).thenReturn(Optional.empty());

            BDDataCategoryRes movie = new BDDataCategoryRes();
            movie.setName("Movie");
            movie.setNamespace(defaultNs);
            when(mockSemanticLinkingClient.getDataCategoryByNameAndNamespaceUuid("Movie", defaultNs.getUuid()))
                            .thenReturn(Optional.of(movie));

            Map<String, Object> sContext = objectMapper.readValue(
                            Resources.toByteArray(getClass()
                                            .getResource("testSemanticLinking_unknownPrefix_semanticContext.json")),
                            Map.class);
            BDPhysicalEntityRes physicalEntity = objectMapper.readValue(
                            Resources.toByteArray(getClass()
                                            .getResource("testSemanticLinking_unknownPrefix_rawPhysicalEntity.json")),
                            BDPhysicalEntityRes.class);

            semanticLinkManager.enrichWithSemanticContext(physicalEntity, sContext);

            verify(mockSemanticLinkingClient, never()).getDataCategoryByNameAndNamespaceUuid(eq("unknown:SomeConcept"),
                            anyString());
            verify(mockSemanticLinkingClient, never()).getDataCategoryByNameAndNamespaceUuid(eq("SomeConcept"),
                            eq(defaultNs.getUuid()));
    }

    /**
     * {@code agentspecs/specs/semantic_linking/prefixed_concept_resolution/spec.md}
     * —
     * Feature: Namespace prefix resolution — Scenario: Ambiguous prefix (client
     * returns no unique namespace).
     */
    @Test
    void ambiguousPrefixSkipsCategoryResolution() throws IOException {
            reset(mockSemanticLinkingClient);
            BDLogicalNamespaceRes defaultNs = new BDLogicalNamespaceRes();
            defaultNs.setIdentifier("https://demo.blindata.io/logical/namespaces/name/filmRentalInc#");
            defaultNs.setUuid("film-rental-namespace-uuid");

            when(mockSemanticLinkingClient.getLogicalNamespaceByIdentifier(defaultNs.getIdentifier()))
                            .thenReturn(Optional.of(defaultNs));
            when(mockSemanticLinkingClient.getLogicalNamespaceByPrefix("dup")).thenReturn(Optional.empty());

            BDDataCategoryRes movie = new BDDataCategoryRes();
            movie.setName("Movie");
            movie.setNamespace(defaultNs);
            when(mockSemanticLinkingClient.getDataCategoryByNameAndNamespaceUuid("Movie", defaultNs.getUuid()))
                            .thenReturn(Optional.of(movie));

            Map<String, Object> sContext = objectMapper.readValue(
                            Resources.toByteArray(getClass()
                                            .getResource("testSemanticLinking_ambiguousPrefix_semanticContext.json")),
                            Map.class);
            BDPhysicalEntityRes physicalEntity = objectMapper.readValue(
                            Resources.toByteArray(getClass()
                                            .getResource("testSemanticLinking_ambiguousPrefix_rawPhysicalEntity.json")),
                            BDPhysicalEntityRes.class);

            semanticLinkManager.enrichWithSemanticContext(physicalEntity, sContext);

            verify(mockSemanticLinkingClient, never()).getDataCategoryByNameAndNamespaceUuid(eq("AmbiguousConcept"),
                            anyString());
    }

    /**
     * {@code agentspecs/specs/semantic_linking/prefixed_concept_resolution/spec.md} —
     * Feature: Leading bracket concept with namespace prefix — Scenario: Default
     * {@code s-type} with prefix resolves root concept in that namespace.
     */
    @Test
    void sTypeWithNamespacePrefixResolvesRootConceptInPrefixedNamespace() throws IOException {
            reset(mockSemanticLinkingClient);
            BDLogicalNamespaceRes defaultNs = new BDLogicalNamespaceRes();
            defaultNs.setIdentifier("https://demo.blindata.io/logical/namespaces/name/filmRentalInc#");
            defaultNs.setUuid("film-rental-namespace-uuid");

            BDLogicalNamespaceRes otherNs = new BDLogicalNamespaceRes();
            otherNs.setUuid("other-namespace-uuid");
            otherNs.setPrefix("other");

            when(mockSemanticLinkingClient.getLogicalNamespaceByIdentifier(defaultNs.getIdentifier()))
                            .thenReturn(Optional.of(defaultNs));
            when(mockSemanticLinkingClient.getLogicalNamespaceByPrefix("other")).thenReturn(Optional.of(otherNs));

            BDDataCategoryRes conceptB = new BDDataCategoryRes();
            conceptB.setName("ConceptB");
            conceptB.setNamespace(otherNs);
            when(mockSemanticLinkingClient.getDataCategoryByNameAndNamespaceUuid("ConceptB", otherNs.getUuid()))
                            .thenReturn(Optional.of(conceptB));

            BDLogicalFieldSemanticLinkRes resolvedLink = new BDLogicalFieldSemanticLinkRes();
            when(mockSemanticLinkingClient.getSemanticLinkElements(anyString(), anyString())).thenReturn(resolvedLink);

            Map<String, Object> sContext = objectMapper.readValue(
                            Resources.toByteArray(getClass()
                                            .getResource("testSemanticLinking_sTypePrefixed_semanticContext.json")),
                            Map.class);
            BDPhysicalEntityRes physicalEntity = objectMapper.readValue(
                            Resources.toByteArray(getClass()
                                            .getResource("testSemanticLinking_sTypePrefixed_rawPhysicalEntity.json")),
                            BDPhysicalEntityRes.class);

            semanticLinkManager.enrichWithSemanticContext(physicalEntity, sContext);

            verify(mockSemanticLinkingClient, org.mockito.Mockito.times(2))
                            .getDataCategoryByNameAndNamespaceUuid("ConceptB", otherNs.getUuid());
            verify(mockSemanticLinkingClient, never()).getDataCategoryByNameAndNamespaceUuid(eq("other:ConceptB"),
                            anyString());
            verify(mockSemanticLinkingClient).getSemanticLinkElements(
                            "[other:ConceptB].other:attribute2FromConceptB",
                            defaultNs.getIdentifier());
    }

    /**
     * {@code agentspecs/specs/semantic_linking/prefixed_concept_resolution/spec.md} — Non-regression: Stock /
     * luxury-style relative paths (e.g. refersTo with {@code lux:ProductSku} segments).
     */
    @Test
    void testSemanticPathResolutionWithStockContext() {
        reset(mockSemanticLinkingClient);
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
        verify(mockSemanticLinkingClient, never()).getLogicalNamespaceByPrefix(anyString());
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

        lenient().when(mockSemanticLinkingClient.getLogicalNamespaceByPrefix(anyString()))
                        .thenReturn(Optional.empty());
    }

    private BDPhysicalFieldRes createPhysicalField(String name) {
        BDPhysicalFieldRes field = new BDPhysicalFieldRes();
        field.setName(name);
        field.setUuid("test-uuid-" + name);
        return field;
    }
}
