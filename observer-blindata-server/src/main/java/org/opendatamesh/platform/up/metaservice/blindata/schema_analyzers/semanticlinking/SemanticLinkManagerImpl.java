package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDSemanticLinkingClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.*;
import org.opendatamesh.platform.up.metaservice.blindata.resources.exceptions.BlindataClientException;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;

class SemanticLinkManagerImpl implements SemanticLinkManager {

    private final BDSemanticLinkingClient client;

    public SemanticLinkManagerImpl(BDSemanticLinkingClient client) {
        this.client = client;
    }

    @Override
    public void enrichWithSemanticContext(BDPhysicalEntityRes physicalEntity, Map<String, Object> semanticContext) {
        withErrorHandling(() -> {
            SemanticContext parsedSemanticContext = parseSemanticContext(semanticContext);

            Optional<BDLogicalNamespaceRes> resolvedDefaultNamespace = resolveDefaultNamespace(parsedSemanticContext.getDefaultNamespaceIdentifier());
            if (resolvedDefaultNamespace.isEmpty()) return;

            Optional<BDDataCategoryRes> resolvedDefaultDataCategory = resolveDefaultDataCategory(parsedSemanticContext.getDefaultDataCategoryName(), resolvedDefaultNamespace.get());
            if (resolvedDefaultDataCategory.isEmpty()) return;

            Set<BDDataCategoryRes> resolvedDataCategories =
                    resolveDataCategories(parsedSemanticContext.getDataCategories(), resolvedDefaultNamespace.get());
            Set<BDPhysicalFieldRes> enrichedPhysicalFields = enrichPhysicalFieldsWithSemanticLinks(physicalEntity.getPhysicalFields(), parsedSemanticContext.getSemanticLinks());
            physicalEntity.setDataCategories(resolvedDataCategories);
            physicalEntity.setPhysicalFields(enrichedPhysicalFields);
        });
    }

    private SemanticContext parseSemanticContext(Map<String, Object> semanticContext) {
        String defaultNamespaceIdentifier = Optional.ofNullable((String) semanticContext.get("s-base")).orElse("");
        String defaultDataCategoryName = Optional.ofNullable((String) semanticContext.get("s-type")).orElse("").replaceAll("[\\[\\]]", "");

        return semanticContext.entrySet().stream()
                .filter(entry -> !"s-base".equals(entry.getKey()) && !"s-type".equals(entry.getKey()))
                .map(entry -> parseFieldContext(entry.getKey(), entry.getValue(), "", defaultNamespaceIdentifier, defaultDataCategoryName))
                .reduce(
                        new SemanticContext(defaultNamespaceIdentifier, defaultDataCategoryName, new HashMap<>(), Set.of(defaultDataCategoryName)),
                        SemanticContext::merge
                );
    }

    private SemanticContext parseFieldContext(
            String fieldName,
            Object fieldContext,
            String parentPath,
            String defaultNamespaceIdentifier,
            String defaultDataCategoryName) {

        if (fieldContext instanceof String) {
            return handleSimpleSemanticPath(fieldName, (String) fieldContext, parentPath, defaultNamespaceIdentifier, defaultDataCategoryName);
        } else if (fieldContext instanceof Map) {
            return handleNestedSemanticPath(fieldName, (Map<String, Object>) fieldContext, parentPath, defaultNamespaceIdentifier, defaultDataCategoryName);
        }
        return new SemanticContext();
    }

    private SemanticContext handleSimpleSemanticPath(
            String fieldName,
            String semanticPath,
            String parentPath,
            String defaultNamespaceIdentifier,
            String defaultDataCategoryName
    ) {
        String fullPath = isAbsoluteSemanticPath(semanticPath) ? semanticPath : (parentPath.isEmpty() ? semanticPath : parentPath + "." + semanticPath);

        Set<String> dataCategories = new HashSet<>();
        if (isAbsoluteSemanticPath(fullPath)) {
            String startingConcept = fullPath.substring(0, fullPath.indexOf("]") + 1).replaceAll("[\\[\\]]", "");
            dataCategories.add(startingConcept);
        } else {
            fullPath = StringUtils.hasText(defaultDataCategoryName) ? "[" + defaultDataCategoryName + "]" + "." + fullPath : fullPath;
        }

        BDSemanticLink semanticLink = new BDSemanticLink();
        semanticLink.setDefaultNamespaceIdentifier(defaultNamespaceIdentifier);
        semanticLink.setSemanticLinkString(fullPath);
        Map<String, BDSemanticLink> semanticLinks = Map.of(fieldName, semanticLink);

        return new SemanticContext(defaultNamespaceIdentifier, defaultDataCategoryName, semanticLinks, dataCategories);
    }

    private SemanticContext handleNestedSemanticPath(
            String fieldName,
            Map<String, Object> nestedContext,
            String parentPath,
            String defaultNamespaceIdentifier,
            String defaultDataCategoryName
    ) {
        SemanticContext semanticContext = new SemanticContext();

        String nestedType = (String) nestedContext.get("s-type");
        for (Map.Entry<String, Object> entry : nestedContext.entrySet()) {
            if (!"s-type".equals(entry.getKey())) {
                String nestedFieldName = entry.getKey();
                String effectiveParentPath = parentPath.isEmpty() ? nestedType : parentPath + "." + nestedType;
                SemanticContext childContext = parseFieldContext(fieldName + "." + nestedFieldName, entry.getValue(), effectiveParentPath, defaultNamespaceIdentifier, defaultDataCategoryName);
                semanticContext = semanticContext.merge(childContext);
            }
        }
        return semanticContext;
    }

    private Optional<BDLogicalNamespaceRes> resolveDefaultNamespace(String defaultNamespaceIdentifier) {
        if (!StringUtils.hasText(defaultNamespaceIdentifier)) {
            getUseCaseLogger().warn("No default namespace identifier provided.");
            return Optional.empty();
        }

        Optional<BDLogicalNamespaceRes> resolvedNamespace = client.getLogicalNamespaceByIdentifier(defaultNamespaceIdentifier);
        if (resolvedNamespace.isEmpty()) {
            getUseCaseLogger().warn("Namespace not found for identifier: " + defaultNamespaceIdentifier);
        }
        return resolvedNamespace;
    }

    private Optional<BDDataCategoryRes> resolveDefaultDataCategory(String defaultDataCategoryName, BDLogicalNamespaceRes defaultNamespace) {
        if (!StringUtils.hasText(defaultDataCategoryName)) {
            getUseCaseLogger().warn("No default data category name provided.");
            return Optional.empty();
        }

        Optional<BDDataCategoryRes> resolvedDataCategory = client.getDataCategoryByNameAndNamespaceUuid(defaultDataCategoryName, defaultNamespace.getUuid());
        if (resolvedDataCategory.isEmpty()) {
            getUseCaseLogger().warn("Data category: " + defaultDataCategoryName + " not found in namespace " + defaultNamespace.getIdentifier());
        }
        return resolvedDataCategory;
    }

    private Set<BDDataCategoryRes> resolveDataCategories(Set<String> dataCategories, BDLogicalNamespaceRes defaultNamespace) {
        return dataCategories.stream()
                .map(dataCategoryName -> {
                    Optional<BDDataCategoryRes> resolved = client.getDataCategoryByNameAndNamespaceUuid(dataCategoryName, defaultNamespace.getUuid());
                    if (resolved.isEmpty()) {
                        getUseCaseLogger().warn("Data category: " + dataCategoryName + " not found");
                    }
                    return resolved;
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    private Set<BDPhysicalFieldRes> enrichPhysicalFieldsWithSemanticLinks(Set<BDPhysicalFieldRes> fields, Map<String, BDSemanticLink> semanticLinks) {
        return fields.stream()
                .map(field -> {
                    BDSemanticLink semanticLink = semanticLinks.get(field.getName());
                    if (semanticLink == null) return field;

                    LogicalFieldSemanticLinkRes resolvedSemanticLink = client.getSemanticLinkElements(semanticLink.getSemanticLinkString(), semanticLink.getDefaultNamespaceIdentifier());
                    if (resolvedSemanticLink == null) {
                        getUseCaseLogger().warn("Unable to resolve semantic elements for semantic link path: " + semanticLink.getSemanticLinkString());
                        return field; // Return unmodified if resolution fails
                    }

                    return new BDPhysicalFieldRes(
                            field.getUuid(),
                            field.getName(),
                            field.getType(),
                            field.getOrdinalPosition(),
                            field.getDescription(),
                            field.getCreationDate(),
                            field.getModificationDate(),
                            field.getAdditionalProperties(),
                            List.of(resolvedSemanticLink)
                    );
                })
                .collect(Collectors.toSet());
    }

    private boolean isAbsoluteSemanticPath(String path) {
        return path.matches("^\\[[^\\]]+\\].*");
    }

    private class SemanticContext {

        String defaultNamespaceIdentifier;
        String defaultDataCategoryName;
        Map<String, BDSemanticLink> semanticLinks;
        Set<String> dataCategories;

        public SemanticContext() {
        }

        public SemanticContext(String defaultNamespaceIdentifier, String defaultDataCategoryName, Map<String, BDSemanticLink> semanticLinks, Set<String> dataCategories) {
            this.defaultNamespaceIdentifier = defaultNamespaceIdentifier;
            this.defaultDataCategoryName = defaultDataCategoryName;
            this.semanticLinks = semanticLinks;
            this.dataCategories = dataCategories;
        }

        public String getDefaultNamespaceIdentifier() {
            return defaultNamespaceIdentifier;
        }

        public void setDefaultNamespaceIdentifier(String defaultNamespaceIdentifier) {
            this.defaultNamespaceIdentifier = defaultNamespaceIdentifier;
        }

        public String getDefaultDataCategoryName() {
            return defaultDataCategoryName;
        }

        public void setDefaultDataCategoryName(String defaultDataCategoryName) {
            this.defaultDataCategoryName = defaultDataCategoryName;
        }

        public Map<String, BDSemanticLink> getSemanticLinks() {
            return semanticLinks;
        }

        public void setSemanticLinks(Map<String, BDSemanticLink> semanticLinks) {
            this.semanticLinks = semanticLinks;
        }

        public Set<String> getDataCategories() {
            return dataCategories;
        }

        public void setDataCategories(Set<String> dataCategories) {
            this.dataCategories = dataCategories;
        }

        public SemanticContext merge(SemanticContext other) {
            String mergedNamespaceIdentifier =
                    other.defaultNamespaceIdentifier != null ? other.defaultNamespaceIdentifier : this.defaultNamespaceIdentifier;

            String mergedDataCategoryName =
                    other.defaultDataCategoryName != null ? other.defaultDataCategoryName : this.defaultDataCategoryName;

            Map<String, BDSemanticLink> mergedLinks = new HashMap<>(this.semanticLinks != null ? this.semanticLinks : Map.of());
            if (other.semanticLinks != null) {
                mergedLinks.putAll(other.semanticLinks);
            }

            Set<String> mergedCategories = new HashSet<>(this.dataCategories != null ? this.dataCategories : Set.of());
            if (other.dataCategories != null) {
                mergedCategories.addAll(other.dataCategories);
            }

            return new SemanticContext(mergedNamespaceIdentifier, mergedDataCategoryName, mergedLinks, mergedCategories);
        }
    }

    private void withErrorHandling(Runnable runnable) {
        try {
            runnable.run();
        } catch (BlindataClientException e) {
            if (e.getCode() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                throw e;
            } else {
                getUseCaseLogger().warn(e.getMessage(), e);
            }
        }
    }
}
