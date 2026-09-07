package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking;

import com.google.common.collect.Lists;
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
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;

class SemanticLinkManagerImpl implements SemanticLinkManager {

    private final BdSemanticLinkingClient client;

    public SemanticLinkManagerImpl(BdSemanticLinkingClient client) {
        this.client = client;
    }

    @Override
    public void enrichWithSemanticContext(BDPhysicalEntityRes physicalEntity, Map<String, Object> semanticContext) {
        withErrorHandling(() -> {
            SemanticContext parsedSemanticContext = parseSemanticContext(semanticContext);

            Optional<BDLogicalNamespaceRes> resolvedDefaultNamespace = resolveDefaultNamespace(parsedSemanticContext.getDefaultNamespaceIdentifier());
            if (resolvedDefaultNamespace.isEmpty()) return;

            Optional<BDDataCategoryRes> resolvedDefaultDataCategory = resolveDefaultDataCategory(parsedSemanticContext.getDefaultDataCategoryInner(), resolvedDefaultNamespace.get());
            if (resolvedDefaultDataCategory.isEmpty()) return;

            Set<BDDataCategoryRes> resolvedDataCategories =
                    resolveDataCategories(parsedSemanticContext.getReferencedConcepts(), resolvedDefaultNamespace.get());
            Set<BDPhysicalFieldRes> enrichedPhysicalFields = enrichPhysicalFieldsWithSemanticLinks(physicalEntity.getPhysicalFields(), parsedSemanticContext.getSemanticLinks());
            physicalEntity.setDataCategories(resolvedDataCategories);
            physicalEntity.setPhysicalFields(enrichedPhysicalFields);
        });
    }

    private SemanticContext parseSemanticContext(Map<String, Object> semanticContext) {
        String defaultNamespaceIdentifier = Optional.ofNullable((String) semanticContext.get("s-base")).orElse("");
        String defaultDataCategoryInner = Optional.ofNullable((String) semanticContext.get("s-type")).orElse("").replaceAll("[\\[\\]]", "");
        BracketConceptRef defaultRootRef = BracketConceptRef.parse(defaultDataCategoryInner);
        Set<BracketConceptRef> initialRefs = new HashSet<>();
        initialRefs.add(defaultRootRef);

        return semanticContext.entrySet().stream()
                .filter(entry -> !"s-base".equals(entry.getKey()) && !"s-type".equals(entry.getKey()))
                .map(entry -> parseFieldContext(entry.getKey(), entry.getValue(), "", defaultNamespaceIdentifier, defaultDataCategoryInner))
                .reduce(
                        new SemanticContext(defaultNamespaceIdentifier, defaultDataCategoryInner, new HashMap<>(), initialRefs),
                        SemanticContext::merge
                );
    }

    private SemanticContext parseFieldContext(
            String fieldName,
            Object fieldContext,
            String parentPath,
            String defaultNamespaceIdentifier,
            String defaultDataCategoryInner) {

        if (fieldContext instanceof String) {
            return handleSimpleSemanticPath(fieldName, (String) fieldContext, parentPath, defaultNamespaceIdentifier, defaultDataCategoryInner);
        } else if (fieldContext instanceof Map) {
            return handleNestedSemanticPath(fieldName, (Map<String, Object>) fieldContext, parentPath, defaultNamespaceIdentifier, defaultDataCategoryInner);
        }
        return new SemanticContext();
    }

    private SemanticContext handleSimpleSemanticPath(
            String fieldName,
            String semanticPath,
            String parentPath,
            String defaultNamespaceIdentifier,
            String defaultDataCategoryInner
    ) {
        String fullPath = isAbsoluteSemanticPath(semanticPath) ? semanticPath : (parentPath.isEmpty() ? semanticPath : parentPath + "." + semanticPath);

        Set<BracketConceptRef> referencedConcepts = new HashSet<>();
        if (isAbsoluteSemanticPath(fullPath)) {
            String startingInner = fullPath.substring(0, fullPath.indexOf("]") + 1).replaceAll("[\\[\\]]", "");
            referencedConcepts.add(BracketConceptRef.parse(startingInner));
        } else {
            fullPath = StringUtils.hasText(defaultDataCategoryInner) ? "[" + defaultDataCategoryInner + "]" + "." + fullPath : fullPath;
        }

        BDSemanticLink semanticLink = new BDSemanticLink();
        semanticLink.setDefaultNamespaceIdentifier(defaultNamespaceIdentifier);
        semanticLink.setSemanticLinkString(fullPath);
        Map<String, BDSemanticLink> semanticLinks = Map.of(fieldName, semanticLink);

        return new SemanticContext(defaultNamespaceIdentifier, defaultDataCategoryInner, semanticLinks, referencedConcepts);
    }

    // Used for nested field, like in AVRO
    // E.g.
    // "copyright": {
    //    "s-type": "copyrightHolder[Organization]",
    //    "organization_id": "vatNumber",
    //    "email": "contactPoint[ContactPoint].mail"
    //  }
    private SemanticContext handleNestedSemanticPath(
            String fieldName,
            Map<String, Object> nestedContext,
            String parentPath,
            String defaultNamespaceIdentifier,
            String defaultDataCategoryInner
    ) {
        SemanticContext semanticContext = new SemanticContext();

        String nestedType = (String) nestedContext.get("s-type");
        for (Map.Entry<String, Object> entry : nestedContext.entrySet()) {
            if (!"s-type".equals(entry.getKey())) {
                String nestedFieldName = entry.getKey();
                String effectiveParentPath = parentPath.isEmpty() ? nestedType : parentPath + "." + nestedType;
                SemanticContext childContext = parseFieldContext(fieldName + "." + nestedFieldName, entry.getValue(), effectiveParentPath, defaultNamespaceIdentifier, defaultDataCategoryInner);
                semanticContext = semanticContext.merge(childContext);
            }
        }
        return semanticContext;
    }

    private Optional<BDLogicalNamespaceRes> resolveDefaultNamespace(String defaultNamespaceIdentifier) {
        if (!StringUtils.hasText(defaultNamespaceIdentifier)) {
            getUseCaseLogger().warn("[#85] No default namespace identifier provided.");
            return Optional.empty();
        }

        Optional<BDLogicalNamespaceRes> resolvedNamespace = client.getLogicalNamespaceByIdentifier(defaultNamespaceIdentifier);
        if (resolvedNamespace.isEmpty()) {
            getUseCaseLogger().warn("[#86] Namespace not found for identifier: " + defaultNamespaceIdentifier);
        }
        return resolvedNamespace;
    }

    private Optional<BDDataCategoryRes> resolveDefaultDataCategory(String defaultDataCategoryInner, BDLogicalNamespaceRes defaultNamespace) {
        BracketConceptRef ref = BracketConceptRef.parse(defaultDataCategoryInner);
        if (!StringUtils.hasText(ref.getConceptName())) {
            getUseCaseLogger().warn("[#87] No default data category name provided.");
            return Optional.empty();
        }

        Optional<String> targetNamespaceUuid = resolveNamespaceUuidForConceptRef(ref, defaultNamespace);

        Optional<BDDataCategoryRes> resolvedDataCategory = client.getDataCategoryByNameAndNamespaceUuid(ref.getConceptName(), targetNamespaceUuid.orElse(null));
        if (resolvedDataCategory.isEmpty()) {
            String namespaceLabel = ref.getNamespacePrefix().map(p -> "prefix " + p).orElseGet(defaultNamespace::getIdentifier);
            getUseCaseLogger().warn("[#88] Data category: " + ref.getConceptName() + " not found in namespace " + namespaceLabel);
        }
        return resolvedDataCategory;
    }

    private Optional<String> resolveNamespaceUuidForConceptRef(BracketConceptRef ref, BDLogicalNamespaceRes defaultNamespace) {
        if (ref.getNamespacePrefix().isEmpty()) {
            return Optional.ofNullable(defaultNamespace.getUuid());
        }
        String prefix = ref.getNamespacePrefix().get();
        Optional<BDLogicalNamespaceRes> resolved = client.getLogicalNamespaceByPrefix(prefix);
        if (resolved.isEmpty()) {
            getUseCaseLogger().warn("[#120] No unique logical namespace found for prefix: " + prefix);
            return Optional.empty();
        }
        if (resolved.get().getUuid() == null) {
            getUseCaseLogger().warn("[#120] No unique logical namespace found for prefix: " + prefix);
            return Optional.empty();
        }
        return Optional.of(resolved.get().getUuid());
    }

    private Set<BDDataCategoryRes> resolveDataCategories(Set<BracketConceptRef> referencedConcepts, BDLogicalNamespaceRes defaultNamespace) {
        return referencedConcepts.stream()
                .map(ref -> {
                    if (!StringUtils.hasText(ref.getConceptName())) {
                        return Optional.<BDDataCategoryRes>empty();
                    }
                    Optional<String> namespaceUuid = resolveNamespaceUuidForConceptRef(ref, defaultNamespace);
                    if (namespaceUuid.isEmpty()) {
                        return Optional.<BDDataCategoryRes>empty();
                    }
                    Optional<BDDataCategoryRes> resolved = client.getDataCategoryByNameAndNamespaceUuid(ref.getConceptName(), namespaceUuid.get());
                    if (resolved.isEmpty()) {
                        getUseCaseLogger().warn("[#89] Data category: " + ref.getConceptName() + " not found");
                    }
                    return resolved;
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    private Set<BDPhysicalFieldRes> enrichPhysicalFieldsWithSemanticLinks(Set<BDPhysicalFieldRes> fields, Map<String, BDSemanticLink> semanticLinks) {
        if (fields == null || fields.isEmpty()) {
            return fields;
        }

        List<BDSemanticLinkingResolveFieldPathRes> uniquePaths = collectUniqueResolvePaths(fields, semanticLinks);
        if (uniquePaths.isEmpty()) {
            return fields;
        }

        Map<String, BDSemanticLinkingResolveFieldPathResultRes> resultsByKey = resolveSemanticFieldsInChunks(uniquePaths);
        return applyResolveResults(fields, semanticLinks, resultsByKey);
    }

    private List<BDSemanticLinkingResolveFieldPathRes> collectUniqueResolvePaths(
            Set<BDPhysicalFieldRes> fields,
            Map<String, BDSemanticLink> semanticLinks
    ) {
        LinkedHashMap<String, BDSemanticLinkingResolveFieldPathRes> uniquePaths = new LinkedHashMap<>();
        for (BDPhysicalFieldRes field : fields) {
            BDSemanticLink semanticLink = semanticLinks.get(field.getName());
            if (semanticLink == null) {
                continue;
            }
            uniquePaths.computeIfAbsent(
                    resolveKey(semanticLink.getSemanticLinkString(), semanticLink.getDefaultNamespaceIdentifier()),
                    key -> toResolvePath(semanticLink)
            );
        }
        return new ArrayList<>(uniquePaths.values());
    }

    private Map<String, BDSemanticLinkingResolveFieldPathResultRes> resolveSemanticFieldsInChunks(
            List<BDSemanticLinkingResolveFieldPathRes> uniquePaths
    ) {
        Map<String, BDSemanticLinkingResolveFieldPathResultRes> resultsByKey = new HashMap<>();
        for (List<BDSemanticLinkingResolveFieldPathRes> chunk : Lists.partition(
                uniquePaths,
                BdClientImpl.MAX_RESOLVE_FIELDS_BATCH_SIZE
        )) {
            BDSemanticLinkingResolveFieldsRequestRes request = new BDSemanticLinkingResolveFieldsRequestRes();
            request.setPaths(chunk);
            BDSemanticLinkingResolveFieldsResultRes result = client.resolveSemanticFields(request);
            List<BDSemanticLinkingResolveFieldPathResultRes> pathResults =
                    result != null && result.getPaths() != null ? result.getPaths() : List.of();
            for (BDSemanticLinkingResolveFieldPathResultRes pathResult : pathResults) {
                resultsByKey.put(
                        resolveKey(pathResult.getPathString(), pathResult.getDefaultNamespaceIdentifier()),
                        pathResult
                );
            }
        }
        return resultsByKey;
    }

    private Set<BDPhysicalFieldRes> applyResolveResults(
            Set<BDPhysicalFieldRes> fields,
            Map<String, BDSemanticLink> semanticLinks,
            Map<String, BDSemanticLinkingResolveFieldPathResultRes> resultsByKey
    ) {
        Set<String> warnedKeys = new HashSet<>();
        return fields.stream()
                .map(field -> applyResolveResult(field, semanticLinks, resultsByKey, warnedKeys))
                .collect(Collectors.toSet());
    }

    private BDPhysicalFieldRes applyResolveResult(
            BDPhysicalFieldRes field,
            Map<String, BDSemanticLink> semanticLinks,
            Map<String, BDSemanticLinkingResolveFieldPathResultRes> resultsByKey,
            Set<String> warnedKeys
    ) {
        BDSemanticLink semanticLink = semanticLinks.get(field.getName());
        if (semanticLink == null) {
            return field;
        }

        String key = resolveKey(semanticLink.getSemanticLinkString(), semanticLink.getDefaultNamespaceIdentifier());
        BDSemanticLinkingResolveFieldPathResultRes pathResult = resultsByKey.get(key);
        if (pathResult != null && pathResult.isSuccessful()) {
            return withLogicalField(field, pathResult.getLogicalField());
        }

        if (warnedKeys.add(key)) {
            getUseCaseLogger().warn("[#90] Unable to resolve semantic elements for semantic link path: " + semanticLink.getSemanticLinkString());
        }
        return field;
    }

    private BDPhysicalFieldRes withLogicalField(BDPhysicalFieldRes field, BDLogicalFieldSemanticLinkRes logicalField) {
        return new BDPhysicalFieldRes(
                field.getUuid(),
                field.getName(),
                field.getType(),
                field.getOrdinalPosition(),
                field.getDescription(),
                field.getCreationDate(),
                field.getModificationDate(),
                field.getAdditionalProperties(),
                List.of(logicalField)
        );
    }

    private BDSemanticLinkingResolveFieldPathRes toResolvePath(BDSemanticLink semanticLink) {
        BDSemanticLinkingResolveFieldPathRes path = new BDSemanticLinkingResolveFieldPathRes();
        path.setPathString(semanticLink.getSemanticLinkString());
        path.setDefaultNamespaceIdentifier(semanticLink.getDefaultNamespaceIdentifier());
        return path;
    }

    private String resolveKey(String pathString, String defaultNamespaceIdentifier) {
        return Objects.toString(pathString, "") + '\0' + Objects.toString(defaultNamespaceIdentifier, "");
    }

    private boolean isAbsoluteSemanticPath(String path) {
        return path.matches("^\\[[^\\]]+\\].*");
    }

    private class SemanticContext {

        String defaultNamespaceIdentifier;
        String defaultDataCategoryInner;
        Map<String, BDSemanticLink> semanticLinks;
        Set<BracketConceptRef> referencedConcepts;

        public SemanticContext() {
        }

        public SemanticContext(String defaultNamespaceIdentifier, String defaultDataCategoryInner, Map<String, BDSemanticLink> semanticLinks, Set<BracketConceptRef> referencedConcepts) {
            this.defaultNamespaceIdentifier = defaultNamespaceIdentifier;
            this.defaultDataCategoryInner = defaultDataCategoryInner;
            this.semanticLinks = semanticLinks;
            this.referencedConcepts = referencedConcepts;
        }

        public String getDefaultNamespaceIdentifier() {
            return defaultNamespaceIdentifier;
        }

        public void setDefaultNamespaceIdentifier(String defaultNamespaceIdentifier) {
            this.defaultNamespaceIdentifier = defaultNamespaceIdentifier;
        }

        public String getDefaultDataCategoryInner() {
            return defaultDataCategoryInner;
        }

        public void setDefaultDataCategoryInner(String defaultDataCategoryInner) {
            this.defaultDataCategoryInner = defaultDataCategoryInner;
        }

        public Map<String, BDSemanticLink> getSemanticLinks() {
            return semanticLinks;
        }

        public void setSemanticLinks(Map<String, BDSemanticLink> semanticLinks) {
            this.semanticLinks = semanticLinks;
        }

        public Set<BracketConceptRef> getReferencedConcepts() {
            return referencedConcepts;
        }

        public void setReferencedConcepts(Set<BracketConceptRef> referencedConcepts) {
            this.referencedConcepts = referencedConcepts;
        }

        public SemanticContext merge(SemanticContext other) {
            String mergedNamespaceIdentifier =
                    other.defaultNamespaceIdentifier != null ? other.defaultNamespaceIdentifier : this.defaultNamespaceIdentifier;

            String mergedDataCategoryInner =
                    other.defaultDataCategoryInner != null ? other.defaultDataCategoryInner : this.defaultDataCategoryInner;

            Map<String, BDSemanticLink> mergedLinks = new HashMap<>(this.semanticLinks != null ? this.semanticLinks : Map.of());
            if (other.semanticLinks != null) {
                mergedLinks.putAll(other.semanticLinks);
            }

            Set<BracketConceptRef> mergedRefs = new HashSet<>(this.referencedConcepts != null ? this.referencedConcepts : Set.of());
            if (other.referencedConcepts != null) {
                mergedRefs.addAll(other.referencedConcepts);
            }

            return new SemanticContext(mergedNamespaceIdentifier, mergedDataCategoryInner, mergedLinks, mergedRefs);
        }
    }

    private void withErrorHandling(Runnable runnable) {
        try {
            runnable.run();
        } catch (BlindataClientException e) {
            if (e.getCode() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                throw e;
            } else {
                getUseCaseLogger().warn("[#91] " + e.getMessage(), e);
            }
        }
    }
}
