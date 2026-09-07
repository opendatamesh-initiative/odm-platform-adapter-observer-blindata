package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdClientImpl;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdSemanticLinkingClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.exceptions.BlindataClientException;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical.BDDataCategoryRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical.BDLogicalFieldSemanticLinkRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical.BDLogicalNamespaceRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical.BDSemanticLinkingResolveFieldPathRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical.BDSemanticLinkingResolveFieldPathResultRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical.BDSemanticLinkingResolveFieldsRequestRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical.BDSemanticLinkingResolveFieldsResultRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalFieldRes;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLogger;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SemanticLinkManagerTest {

    private static final String STOCK_NAMESPACE = "https://demo.blindata.io/logical/namespaces/name/logistics#";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private BdSemanticLinkingClient mockSemanticLinkingClient;

    @Mock
    private UseCaseLogger mockLogger;

    private SemanticLinkManagerImpl semanticLinkManager;
    private UseCaseLogger originalLogger;

    @BeforeEach
    void createSemanticLinkManager() {
        originalLogger = UseCaseLoggerContext.getUseCaseLogger();
        UseCaseLoggerContext.setUseCaseLogger(mockLogger);
        semanticLinkManager = new SemanticLinkManagerImpl(mockSemanticLinkingClient);
    }

    @AfterEach
    void restoreLogger() {
        UseCaseLoggerContext.setUseCaseLogger(originalLogger);
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
            stubResolveSemanticFields(semanticLinkElements);

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
            assertThat(capturedPathStrings()).contains("[other:ConceptB].other:attribute2FromConceptB");
            assertThat(capturedNamespaceIdentifiers()).contains(defaultNs.getIdentifier());
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
            stubResolveSemanticFields(Map.of());

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
            stubResolveSemanticFields(Map.of());

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
            stubResolveSemanticFields(Map.of("[other:ConceptB].other:attribute2FromConceptB", resolvedLink));

            Map<String, Object> sContext = objectMapper.readValue(
                            Resources.toByteArray(getClass()
                                            .getResource("testSemanticLinking_sTypePrefixed_semanticContext.json")),
                            Map.class);
            BDPhysicalEntityRes physicalEntity = objectMapper.readValue(
                            Resources.toByteArray(getClass()
                                            .getResource("testSemanticLinking_sTypePrefixed_rawPhysicalEntity.json")),
                            BDPhysicalEntityRes.class);

            semanticLinkManager.enrichWithSemanticContext(physicalEntity, sContext);

            verify(mockSemanticLinkingClient, times(2))
                            .getDataCategoryByNameAndNamespaceUuid("ConceptB", otherNs.getUuid());
            verify(mockSemanticLinkingClient, never()).getDataCategoryByNameAndNamespaceUuid(eq("other:ConceptB"),
                            anyString());
            assertThat(capturedPathStrings()).contains("[other:ConceptB].other:attribute2FromConceptB");
            assertThat(capturedNamespaceIdentifiers()).contains(defaultNs.getIdentifier());
    }

    /**
     * {@code agentspecs/specs/semantic_linking/prefixed_concept_resolution/spec.md} — Non-regression: Stock /
     * luxury-style relative paths (e.g. refersTo with {@code lux:ProductSku} segments).
     */
    @Test
    void testSemanticPathResolutionWithStockContext() {
        reset(mockSemanticLinkingClient);
        Map<String, Object> sContext = Map.of(
                "s-base", STOCK_NAMESPACE,
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

        stubResolvableStockNamespaceAndCategory();

        BDLogicalFieldSemanticLinkRes mockSemanticLink = new BDLogicalFieldSemanticLinkRes();
        stubResolveSemanticFieldsSucceedingAll(mockSemanticLink);

        semanticLinkManager.enrichWithSemanticContext(physicalEntity, sContext);

        List<String> pathStrings = capturedPathStrings();
        assertThat(pathStrings).containsExactlyInAnyOrder(
                "[Stock].refersTo[lux:ProductSku].lux:productSkuIdentifier",
                "[Stock].refersTo[lux:Site].lux:siteCode",
                "[Stock].refersTo[lux:ProductSku].refersTo[lux:ProductStoreProperties].productOwnershipCode",
                "[Stock].refersTo[lux:Site].isLocatedWithin[lux:Store].storeCode",
                "[Stock].stockQuantity",
                "[Stock].reservedQuantity",
                "[Stock].availableQuantity",
                "[Stock].stockDatetime"
        );
        assertThat(capturedNamespaceIdentifiers()).containsOnly(STOCK_NAMESPACE);
        verify(mockSemanticLinkingClient, never()).getLogicalNamespaceByPrefix(anyString());
    }

    // Scenario: Multiple linked fields on one entity are resolved in one bulk call
    // Given a physical entity with two fields linked to [Stock].stockQuantity and [Stock].reservedQuantity
    // And a resolvable default namespace and data category
    // When enrichWithSemanticContext runs
    // Then resolveSemanticFields is invoked exactly once
    // And the bulk request contains both paths with the entity default namespace
    // And both physical fields receive the corresponding resolved logical field
    @Test
    void testBulkResolve_multipleLinkedFields_singleCall() {
        stubResolvableStockNamespaceAndCategory();
        BDLogicalFieldSemanticLinkRes stockQuantity = resolvedLink("stockQuantity");
        BDLogicalFieldSemanticLinkRes reservedQuantity = resolvedLink("reservedQuantity");
        stubResolveSemanticFields(Map.of(
                "[Stock].stockQuantity", stockQuantity,
                "[Stock].reservedQuantity", reservedQuantity
        ));

        BDPhysicalEntityRes physicalEntity = entityWithFields("stock_qty", "reserved_qty");
        semanticLinkManager.enrichWithSemanticContext(physicalEntity, Map.of(
                "s-base", STOCK_NAMESPACE,
                "s-type", "[Stock]",
                "stock_qty", "stockQuantity",
                "reserved_qty", "reservedQuantity"
        ));

        verify(mockSemanticLinkingClient, times(1)).resolveSemanticFields(any());
        assertThat(capturedPathStrings()).containsExactlyInAnyOrder("[Stock].stockQuantity", "[Stock].reservedQuantity");
        assertThat(capturedNamespaceIdentifiers()).containsOnly(STOCK_NAMESPACE);
        assertThat(fieldNamed(physicalEntity, "stock_qty").getLogicalFields()).containsExactly(stockQuantity);
        assertThat(fieldNamed(physicalEntity, "reserved_qty").getLogicalFields()).containsExactly(reservedQuantity);
    }

    // Scenario: Duplicate path and namespace on two fields is resolved once
    // Given a physical entity with two fields that share the same semantic path and default namespace
    // And a resolvable default namespace and data category
    // When enrichWithSemanticContext runs
    // Then the bulk request contains exactly one path for that path and namespace
    // And both physical fields receive the same resolved logical field
    @Test
    void testBulkResolve_duplicatePathAndNamespace_resolvedOnce() {
        stubResolvableStockNamespaceAndCategory();
        BDLogicalFieldSemanticLinkRes stockQuantity = resolvedLink("stockQuantity");
        stubResolveSemanticFields(Map.of("[Stock].stockQuantity", stockQuantity));

        BDPhysicalEntityRes physicalEntity = entityWithFields("qty_a", "qty_b");
        semanticLinkManager.enrichWithSemanticContext(physicalEntity, Map.of(
                "s-base", STOCK_NAMESPACE,
                "s-type", "[Stock]",
                "qty_a", "stockQuantity",
                "qty_b", "stockQuantity"
        ));

        ArgumentCaptor<BDSemanticLinkingResolveFieldsRequestRes> captor =
                ArgumentCaptor.forClass(BDSemanticLinkingResolveFieldsRequestRes.class);
        verify(mockSemanticLinkingClient, times(1)).resolveSemanticFields(captor.capture());
        assertThat(captor.getValue().getPaths()).hasSize(1);
        assertThat(captor.getValue().getPaths().get(0).getPathString()).isEqualTo("[Stock].stockQuantity");
        assertThat(captor.getValue().getPaths().get(0).getDefaultNamespaceIdentifier()).isEqualTo(STOCK_NAMESPACE);
        assertThat(fieldNamed(physicalEntity, "qty_a").getLogicalFields()).containsExactly(stockQuantity);
        assertThat(fieldNamed(physicalEntity, "qty_b").getLogicalFields()).containsExactly(stockQuantity);
    }

    // Scenario: More than 500 unique linked fields are chunked and none are dropped
    // Given a physical entity with 501 fields each with a unique resolvable semantic path
    // And a resolvable default namespace and data category
    // When enrichWithSemanticContext runs
    // Then resolveSemanticFields is invoked twice
    // And the first call has 500 paths
    // And the second call has 1 path
    // And no call has more than 500 paths
    // And all 501 fields receive a resolved logical field
    @Test
    void testBulkResolve_moreThan500UniqueFields_chunkedWithoutDropping() {
        stubResolvableStockNamespaceAndCategory();
        stubResolveSemanticFieldsSucceedingAll(resolvedLink("chunked"));

        Map<String, Object> sContext = new HashMap<>();
        sContext.put("s-base", STOCK_NAMESPACE);
        sContext.put("s-type", "[Stock]");
        Set<BDPhysicalFieldRes> fields = new HashSet<>();
        for (int i = 0; i < 501; i++) {
            String fieldName = "field_" + i;
            fields.add(createPhysicalField(fieldName));
            sContext.put(fieldName, "attr_" + i);
        }
        BDPhysicalEntityRes physicalEntity = new BDPhysicalEntityRes();
        physicalEntity.setPhysicalFields(fields);

        semanticLinkManager.enrichWithSemanticContext(physicalEntity, sContext);

        ArgumentCaptor<BDSemanticLinkingResolveFieldsRequestRes> captor =
                ArgumentCaptor.forClass(BDSemanticLinkingResolveFieldsRequestRes.class);
        verify(mockSemanticLinkingClient, times(2)).resolveSemanticFields(captor.capture());
        List<Integer> chunkSizes = captor.getAllValues().stream()
                .map(request -> request.getPaths().size())
                .collect(Collectors.toList());
        assertThat(chunkSizes).containsExactly(500, 1);
        assertThat(chunkSizes).allMatch(size -> size <= BdClientImpl.MAX_RESOLVE_FIELDS_BATCH_SIZE);
        assertThat(physicalEntity.getPhysicalFields())
                .allMatch(field -> field.getLogicalFields() != null && field.getLogicalFields().size() == 1);
    }

    // Scenario: Fields without semantic links are omitted from the bulk request
    // Given a physical entity with one linked field and one field with no semantic path
    // And a resolvable default namespace and data category
    // When enrichWithSemanticContext runs
    // Then the bulk request contains only the linked field path
    // And the unlinked field remains without logical fields
    @Test
    void testBulkResolve_unlinkedFieldsOmittedFromRequest() {
        stubResolvableStockNamespaceAndCategory();
        BDLogicalFieldSemanticLinkRes stockQuantity = resolvedLink("stockQuantity");
        stubResolveSemanticFields(Map.of("[Stock].stockQuantity", stockQuantity));

        BDPhysicalEntityRes physicalEntity = entityWithFields("stock_qty", "unlinked");
        semanticLinkManager.enrichWithSemanticContext(physicalEntity, Map.of(
                "s-base", STOCK_NAMESPACE,
                "s-type", "[Stock]",
                "stock_qty", "stockQuantity"
        ));

        assertThat(capturedPathStrings()).containsExactly("[Stock].stockQuantity");
        assertThat(fieldNamed(physicalEntity, "stock_qty").getLogicalFields()).containsExactly(stockQuantity);
        assertThat(fieldNamed(physicalEntity, "unlinked").getLogicalFields()).isEmpty();
    }

    // Scenario: Entity with no linked fields does not call Blindata resolve
    // Given a physical entity whose fields have no semantic paths
    // And a resolvable default namespace and data category
    // When enrichWithSemanticContext runs
    // Then resolveSemanticFields is never invoked
    @Test
    void testBulkResolve_noLinkedFields_doesNotCallResolve() {
        stubResolvableStockNamespaceAndCategory();

        BDPhysicalEntityRes physicalEntity = entityWithFields("unlinked");
        semanticLinkManager.enrichWithSemanticContext(physicalEntity, Map.of(
                "s-base", STOCK_NAMESPACE,
                "s-type", "[Stock]"
        ));

        verify(mockSemanticLinkingClient, never()).resolveSemanticFields(any());
    }

    // Scenario: One unresolvable path does not block sibling fields
    // Given a physical entity with a valid path and an unknown path
    // And a resolvable default namespace and data category
    // And the bulk result marks the unknown path as failed with an error message
    // When enrichWithSemanticContext runs
    // Then the valid field receives its resolved logical field
    // And the unknown field is left unmodified
    // And a [#90] warning is logged for the unknown path
    @Test
    void testBulkResolve_oneUnresolvablePath_doesNotBlockSiblings() {
        stubResolvableStockNamespaceAndCategory();
        BDLogicalFieldSemanticLinkRes stockQuantity = resolvedLink("stockQuantity");
        stubResolveSemanticFields(Map.of("[Stock].stockQuantity", stockQuantity));

        BDPhysicalEntityRes physicalEntity = entityWithFields("stock_qty", "unknown_qty");
        semanticLinkManager.enrichWithSemanticContext(physicalEntity, Map.of(
                "s-base", STOCK_NAMESPACE,
                "s-type", "[Stock]",
                "stock_qty", "stockQuantity",
                "unknown_qty", "[UnknownConcept].missingField"
        ));

        assertThat(fieldNamed(physicalEntity, "stock_qty").getLogicalFields()).containsExactly(stockQuantity);
        assertThat(fieldNamed(physicalEntity, "unknown_qty").getLogicalFields()).isEmpty();
        verify(mockLogger).warn(contains("[#90] Unable to resolve semantic elements for semantic link path: [UnknownConcept].missingField"));
    }

    // Scenario: All paths unresolvable still completes enrichment
    // Given a physical entity with two linked fields
    // And a resolvable default namespace and data category
    // And the bulk result marks every path as failed
    // When enrichWithSemanticContext runs
    // Then both fields are left unmodified
    // And data categories are still set on the entity
    // And a [#90] warning is logged for each path
    @Test
    void testBulkResolve_allPathsUnresolvable_completesEnrichment() {
        stubResolvableStockNamespaceAndCategory();
        stubResolveSemanticFields(Map.of());

        BDPhysicalEntityRes physicalEntity = entityWithFields("stock_qty", "reserved_qty");
        semanticLinkManager.enrichWithSemanticContext(physicalEntity, Map.of(
                "s-base", STOCK_NAMESPACE,
                "s-type", "[Stock]",
                "stock_qty", "stockQuantity",
                "reserved_qty", "reservedQuantity"
        ));

        assertThat(fieldNamed(physicalEntity, "stock_qty").getLogicalFields()).isEmpty();
        assertThat(fieldNamed(physicalEntity, "reserved_qty").getLogicalFields()).isEmpty();
        assertThat(physicalEntity.getDataCategories())
                .extracting(BDDataCategoryRes::getName)
                .containsExactly("Stock");
        verify(mockLogger).warn(contains("[#90] Unable to resolve semantic elements for semantic link path: [Stock].stockQuantity"));
        verify(mockLogger).warn(contains("[#90] Unable to resolve semantic elements for semantic link path: [Stock].reservedQuantity"));
    }

    // Scenario: Batching unit stays per physical entity
    // Given two physical entities each with one linked field
    // And a resolvable default namespace and data category
    // When enrichWithSemanticContext runs for each entity
    // Then resolveSemanticFields is invoked once per entity
    // And neither request contains the other entity's path
    @Test
    void testBulkResolve_batchingUnitStaysPerPhysicalEntity() {
        stubResolvableStockNamespaceAndCategory();
        stubResolveSemanticFieldsSucceedingAll(resolvedLink("any"));

        BDPhysicalEntityRes firstEntity = entityWithFields("stock_qty");
        BDPhysicalEntityRes secondEntity = entityWithFields("reserved_qty");
        semanticLinkManager.enrichWithSemanticContext(firstEntity, Map.of(
                "s-base", STOCK_NAMESPACE,
                "s-type", "[Stock]",
                "stock_qty", "stockQuantity"
        ));
        semanticLinkManager.enrichWithSemanticContext(secondEntity, Map.of(
                "s-base", STOCK_NAMESPACE,
                "s-type", "[Stock]",
                "reserved_qty", "reservedQuantity"
        ));

        ArgumentCaptor<BDSemanticLinkingResolveFieldsRequestRes> captor =
                ArgumentCaptor.forClass(BDSemanticLinkingResolveFieldsRequestRes.class);
        verify(mockSemanticLinkingClient, times(2)).resolveSemanticFields(captor.capture());
        List<String> firstPaths = pathStringsOf(captor.getAllValues().get(0));
        List<String> secondPaths = pathStringsOf(captor.getAllValues().get(1));
        assertThat(firstPaths).containsExactly("[Stock].stockQuantity");
        assertThat(secondPaths).containsExactly("[Stock].reservedQuantity");
        assertThat(firstPaths).doesNotContain("[Stock].reservedQuantity");
        assertThat(secondPaths).doesNotContain("[Stock].stockQuantity");
    }

    // Scenario: Transport 5xx on bulk resolve aborts remaining enrichment
    // Given a physical entity with linked fields
    // And a resolvable default namespace and data category
    // And resolveSemanticFields throws BlindataClientException with status 500
    // When enrichWithSemanticContext runs
    // Then the exception is propagated
    // And physical fields are not replaced with partial links
    @Test
    void testBulkResolve_transport5xx_abortsRemainingEnrichment() {
        stubResolvableStockNamespaceAndCategory();
        when(mockSemanticLinkingClient.resolveSemanticFields(any()))
                .thenThrow(new BlindataClientException(500, "internal error"));

        BDPhysicalEntityRes physicalEntity = entityWithFields("stock_qty");
        Map<String, Object> sContext = Map.of(
                "s-base", STOCK_NAMESPACE,
                "s-type", "[Stock]",
                "stock_qty", "stockQuantity"
        );

        assertThatThrownBy(() -> semanticLinkManager.enrichWithSemanticContext(physicalEntity, sContext))
                .isInstanceOf(BlindataClientException.class)
                .extracting(ex -> ((BlindataClientException) ex).getCode())
                .isEqualTo(500);
        assertThat(fieldNamed(physicalEntity, "stock_qty").getLogicalFields()).isEmpty();
    }

    // Scenario: Transport 4xx on bulk resolve is logged and does not replace fields
    // Given a physical entity with linked fields
    // And a resolvable default namespace and data category
    // And resolveSemanticFields throws BlindataClientException with status 400
    // When enrichWithSemanticContext runs
    // Then the exception is not propagated
    // And a [#91] warning is logged
    // And physical fields are not replaced with resolved links
    @Test
    void testBulkResolve_transport4xx_loggedWithoutReplacingFields() {
        stubResolvableStockNamespaceAndCategory();
        when(mockSemanticLinkingClient.resolveSemanticFields(any()))
                .thenThrow(new BlindataClientException(400, "bad request"));

        BDPhysicalEntityRes physicalEntity = entityWithFields("stock_qty");
        semanticLinkManager.enrichWithSemanticContext(physicalEntity, Map.of(
                "s-base", STOCK_NAMESPACE,
                "s-type", "[Stock]",
                "stock_qty", "stockQuantity"
        ));

        verify(mockLogger).warn(contains("[#91]"), any(Exception.class));
        assertThat(fieldNamed(physicalEntity, "stock_qty").getLogicalFields()).isEmpty();
    }

    private void setUpMocks() throws IOException {
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

        lenient().when(mockSemanticLinkingClient.getLogicalNamespaceByIdentifier(anyString()))
                .thenAnswer(invocation -> Optional.ofNullable(namespaces.get(invocation.getArgument(0))));

        lenient().when(mockSemanticLinkingClient.getDataCategoryByNameAndNamespaceUuid(anyString(), any()))
                .thenAnswer(invocation -> Optional.ofNullable(dataCategories.get(invocation.getArgument(0))));

        stubResolveSemanticFields(semanticLinkElements);

        lenient().when(mockSemanticLinkingClient.getLogicalNamespaceByPrefix(anyString()))
                        .thenReturn(Optional.empty());
    }

    private void stubResolvableStockNamespaceAndCategory() {
        BDLogicalNamespaceRes namespace = new BDLogicalNamespaceRes();
        namespace.setUuid("test-namespace-uuid");
        namespace.setIdentifier(STOCK_NAMESPACE);
        when(mockSemanticLinkingClient.getLogicalNamespaceByIdentifier(STOCK_NAMESPACE))
                .thenReturn(Optional.of(namespace));

        BDDataCategoryRes dataCategory = new BDDataCategoryRes();
        dataCategory.setName("Stock");
        when(mockSemanticLinkingClient.getDataCategoryByNameAndNamespaceUuid("Stock", "test-namespace-uuid"))
                .thenReturn(Optional.of(dataCategory));
    }

    private void stubResolveSemanticFields(Map<String, BDLogicalFieldSemanticLinkRes> semanticLinkElementsByPath) {
        lenient().when(mockSemanticLinkingClient.resolveSemanticFields(any()))
                .thenAnswer(invocation -> {
                    BDSemanticLinkingResolveFieldsRequestRes request = invocation.getArgument(0);
                    BDSemanticLinkingResolveFieldsResultRes result = new BDSemanticLinkingResolveFieldsResultRes();
                    List<BDSemanticLinkingResolveFieldPathResultRes> paths = new ArrayList<>();
                    if (request != null && request.getPaths() != null) {
                        for (BDSemanticLinkingResolveFieldPathRes path : request.getPaths()) {
                            BDSemanticLinkingResolveFieldPathResultRes pathResult = new BDSemanticLinkingResolveFieldPathResultRes();
                            pathResult.setPathString(path.getPathString());
                            pathResult.setDefaultNamespaceIdentifier(path.getDefaultNamespaceIdentifier());
                            BDLogicalFieldSemanticLinkRes resolved = semanticLinkElementsByPath.get(path.getPathString());
                            if (resolved != null) {
                                pathResult.setLogicalField(resolved);
                            } else {
                                pathResult.setErrorMessage("Unable to resolve " + path.getPathString());
                            }
                            paths.add(pathResult);
                        }
                    }
                    result.setPaths(paths);
                    return result;
                });
    }

    private void stubResolveSemanticFieldsSucceedingAll(BDLogicalFieldSemanticLinkRes resolvedLink) {
        when(mockSemanticLinkingClient.resolveSemanticFields(any()))
                .thenAnswer(invocation -> {
                    BDSemanticLinkingResolveFieldsRequestRes request = invocation.getArgument(0);
                    BDSemanticLinkingResolveFieldsResultRes result = new BDSemanticLinkingResolveFieldsResultRes();
                    List<BDSemanticLinkingResolveFieldPathResultRes> paths = new ArrayList<>();
                    if (request != null && request.getPaths() != null) {
                        for (BDSemanticLinkingResolveFieldPathRes path : request.getPaths()) {
                            BDSemanticLinkingResolveFieldPathResultRes pathResult = new BDSemanticLinkingResolveFieldPathResultRes();
                            pathResult.setPathString(path.getPathString());
                            pathResult.setDefaultNamespaceIdentifier(path.getDefaultNamespaceIdentifier());
                            pathResult.setLogicalField(resolvedLink);
                            paths.add(pathResult);
                        }
                    }
                    result.setPaths(paths);
                    return result;
                });
    }

    private List<String> capturedPathStrings() {
        ArgumentCaptor<BDSemanticLinkingResolveFieldsRequestRes> captor =
                ArgumentCaptor.forClass(BDSemanticLinkingResolveFieldsRequestRes.class);
        verify(mockSemanticLinkingClient, times(1)).resolveSemanticFields(captor.capture());
        return pathStringsOf(captor.getValue());
    }

    private List<String> capturedNamespaceIdentifiers() {
        ArgumentCaptor<BDSemanticLinkingResolveFieldsRequestRes> captor =
                ArgumentCaptor.forClass(BDSemanticLinkingResolveFieldsRequestRes.class);
        verify(mockSemanticLinkingClient, times(1)).resolveSemanticFields(captor.capture());
        return captor.getValue().getPaths().stream()
                .map(BDSemanticLinkingResolveFieldPathRes::getDefaultNamespaceIdentifier)
                .collect(Collectors.toList());
    }

    private List<String> pathStringsOf(BDSemanticLinkingResolveFieldsRequestRes request) {
        return request.getPaths().stream()
                .map(BDSemanticLinkingResolveFieldPathRes::getPathString)
                .collect(Collectors.toList());
    }

    private BDLogicalFieldSemanticLinkRes resolvedLink(String uuid) {
        BDLogicalFieldSemanticLinkRes link = new BDLogicalFieldSemanticLinkRes();
        link.setUuid(uuid);
        return link;
    }

    private BDPhysicalEntityRes entityWithFields(String... names) {
        BDPhysicalEntityRes physicalEntity = new BDPhysicalEntityRes();
        Set<BDPhysicalFieldRes> fields = new HashSet<>();
        for (String name : names) {
            fields.add(createPhysicalField(name));
        }
        physicalEntity.setPhysicalFields(fields);
        return physicalEntity;
    }

    private BDPhysicalFieldRes fieldNamed(BDPhysicalEntityRes entity, String name) {
        return entity.getPhysicalFields().stream()
                .filter(field -> name.equals(field.getName()))
                .findFirst()
                .orElseThrow();
    }

    private BDPhysicalFieldRes createPhysicalField(String name) {
        BDPhysicalFieldRes field = new BDPhysicalFieldRes();
        field.setName(name);
        field.setUuid("test-uuid-" + name);
        return field;
    }
}
