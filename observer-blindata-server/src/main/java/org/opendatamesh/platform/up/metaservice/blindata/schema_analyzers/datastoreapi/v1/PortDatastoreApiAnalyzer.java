package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.AdditionalPropertiesRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDPhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDPhysicalFieldRes;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.PortStandardDefinition;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.PortStandardDefinitionAnalyzer;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking.SemanticLinkManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;

@Component
public class PortDatastoreApiAnalyzer implements PortStandardDefinitionAnalyzer {

    private final SemanticLinkManager semanticLinkManager;

    private final String SPECIFICATION = "datastoreapi";
    private final String VERSION = "1.*.*";
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

    @Autowired
    public PortDatastoreApiAnalyzer(SemanticLinkManager semanticLinkManager) {
        this.semanticLinkManager = semanticLinkManager;
    }

    @Override
    public boolean supportsPortStandardDefinition(PortStandardDefinition portStandardDefinition) {
        return SPECIFICATION.equalsIgnoreCase(portStandardDefinition.getSpecification()) &&
                portStandardDefinition.getSpecificationVersion().matches(VERSION);
    }

    @Override
    public List<BDPhysicalEntityRes> getBDAssetsFromPortStandardDefinition(PortStandardDefinition portStandardDefinition) {
        try {
            return extractSchemaPropertiesFromSchemaContent(portStandardDefinition);
        } catch (JsonProcessingException e) {
            getUseCaseLogger().warn(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private List<BDPhysicalEntityRes> extractSchemaPropertiesFromSchemaContent(PortStandardDefinition portStandardDefinition) throws JsonProcessingException {
        DataStoreApiDefinition dataStoreApiDefinition = objectMapper.readValue(portStandardDefinition.getDefinition(), DataStoreApiDefinition.class);
        List<BDPhysicalEntityRes> physicalEntityResList = new ArrayList<>();

        DataStoreApiSchema schema = dataStoreApiDefinition.getSchema();
        if (schema == null) {
            getUseCaseLogger().warn("Missing schema, impossible to extract properties");
            return physicalEntityResList;
        }
        if (schema instanceof DataStoreApiSchemaResource) {
            DataStoreApiSchemaResource schemaResource = (DataStoreApiSchemaResource) schema;
            for (DataStoreApiSchemaEntity entity : schemaResource.getTables()) {
                final String schemaName = StringUtils.hasText(schemaResource.getDatabaseSchemaName())
                        ? schemaResource.getDatabaseSchemaName()
                        : "schema_name";
                BDPhysicalEntityRes extractedEntityFromSchema = fromSchemaEntityToPhysicalEntity(schemaName, entity.getDefinition());
                physicalEntityResList.add(extractedEntityFromSchema);
            }
        } else {
            getUseCaseLogger().warn("Schema is not of type DataStoreApiSchemaResource, skipping extraction.");
        }

        return physicalEntityResList;
    }

    // ============================= OLD Code =================================================================================

    private BDPhysicalEntityRes fromSchemaEntityToPhysicalEntity(String schema, DataStoreAPISchemaEntityDefinition dataStoreApiSchemaEntity) {
        BDPhysicalEntityRes physicalEntityRes = new BDPhysicalEntityRes();
        physicalEntityRes.setSchema(schema);
        physicalEntityRes.setName(dataStoreApiSchemaEntity.getName());
        physicalEntityRes.setDescription(dataStoreApiSchemaEntity.getDescription());
        physicalEntityRes.setTableType(dataStoreApiSchemaEntity.getPhysicalType());
        if (!CollectionUtils.isEmpty(dataStoreApiSchemaEntity.getProperties())) {
            physicalEntityRes.setPhysicalFields(dataStoreApiSchemaEntity.getProperties().values().stream().map(this::extractPhysicalFieldsFromColumn).collect(Collectors.toSet()));
        }
        if (dataStoreApiSchemaEntity.getsContext() != null) {
            semanticLinkManager.enrichPhysicalFieldsWithSemanticLinks(dataStoreApiSchemaEntity.getsContext(), physicalEntityRes);
            semanticLinkManager.linkPhysicalEntityToDataCategory(dataStoreApiSchemaEntity.getsContext(), physicalEntityRes);
        }
        physicalEntityRes.setAdditionalProperties(getExtractAdditionalPropertiesForEntities(dataStoreApiSchemaEntity));
        return physicalEntityRes;
    }

    private List<AdditionalPropertiesRes> getExtractAdditionalPropertiesForEntities(DataStoreAPISchemaEntityDefinition dataStoreApiSchemaEntity) {
        List<AdditionalPropertiesRes> additionalPropertiesRes = new ArrayList<>();
        if (StringUtils.hasText(dataStoreApiSchemaEntity.getStatus())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("status", dataStoreApiSchemaEntity.getStatus()));
        }
        if (!CollectionUtils.isEmpty(dataStoreApiSchemaEntity.getTags())) {
            dataStoreApiSchemaEntity.getTags().forEach(tag -> additionalPropertiesRes.add(new AdditionalPropertiesRes("tags", tag)));
        }
        if (StringUtils.hasText(dataStoreApiSchemaEntity.getDomain())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("domain", dataStoreApiSchemaEntity.getDomain()));
        }
        if (StringUtils.hasText(dataStoreApiSchemaEntity.getContactPoints())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("contactPoints", dataStoreApiSchemaEntity.getContactPoints()));
        }
        if (StringUtils.hasText(dataStoreApiSchemaEntity.getScope())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("scope", dataStoreApiSchemaEntity.getScope()));
        }
        if (StringUtils.hasText(dataStoreApiSchemaEntity.getExternalDocs())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("externalDocs", dataStoreApiSchemaEntity.getExternalDocs()));
        }
        return additionalPropertiesRes;
    }

    private BDPhysicalFieldRes extractPhysicalFieldsFromColumn(DataStoreApiSchemaColumn schema) {
        BDPhysicalFieldRes fieldRes = new BDPhysicalFieldRes();
        fieldRes.setName(schema.getName());
        fieldRes.setType(schema.getPhysicalType());
        fieldRes.setDescription(StringUtils.hasText(schema.getDescription()) ? schema.getDescription() : null);
        fieldRes.setAdditionalProperties(extractAdditionalPropertiesForFields(schema));
        return fieldRes;
    }

    private List<AdditionalPropertiesRes> extractAdditionalPropertiesForFields(DataStoreApiSchemaColumn dataStoreApiSchemaColumn) {
        List<AdditionalPropertiesRes> additionalPropertiesRes = new ArrayList<>();
        if (StringUtils.hasText(dataStoreApiSchemaColumn.getDisplayName())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("displayName", dataStoreApiSchemaColumn.getDisplayName()));
        }
        if (StringUtils.hasText(dataStoreApiSchemaColumn.getSummary())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("summary", dataStoreApiSchemaColumn.getSummary()));
        }
        if (StringUtils.hasText(dataStoreApiSchemaColumn.getStatus())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("status", dataStoreApiSchemaColumn.getStatus()));
        }
        if (!CollectionUtils.isEmpty(dataStoreApiSchemaColumn.getTags())) {
            dataStoreApiSchemaColumn.getTags().forEach(tag -> additionalPropertiesRes.add(new AdditionalPropertiesRes("tags", tag)));
        }
        if (!CollectionUtils.isEmpty(dataStoreApiSchemaColumn.getEnumValues())) {
            dataStoreApiSchemaColumn.getEnumValues().forEach(tag -> additionalPropertiesRes.add(new AdditionalPropertiesRes("enum", tag)));
        }
        if (StringUtils.hasText(dataStoreApiSchemaColumn.getExternalDocs())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("externalDocs", dataStoreApiSchemaColumn.getExternalDocs()));
        }
        if (StringUtils.hasText(dataStoreApiSchemaColumn.getClassificationLevel())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("classificationLevel", dataStoreApiSchemaColumn.getClassificationLevel()));
        }
        if (StringUtils.hasText(dataStoreApiSchemaColumn.getPattern())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("pattern", dataStoreApiSchemaColumn.getPattern()));
        }
        if (StringUtils.hasText(dataStoreApiSchemaColumn.getFormat())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("format", dataStoreApiSchemaColumn.getFormat()));
        }
        if (StringUtils.hasText(dataStoreApiSchemaColumn.getDefaultValue())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("default", dataStoreApiSchemaColumn.getDefaultValue()));
        }
        if (dataStoreApiSchemaColumn.getMinLength() >= 0) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("minLength", String.valueOf(dataStoreApiSchemaColumn.getMinLength())));
        }
        if (dataStoreApiSchemaColumn.getMaxLength() >= 0) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("maxLength", String.valueOf(dataStoreApiSchemaColumn.getMaxLength())));
        }
        if (StringUtils.hasText(dataStoreApiSchemaColumn.getContentEncoding())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("contentEncoding", dataStoreApiSchemaColumn.getContentEncoding()));
        }
        if (StringUtils.hasText(dataStoreApiSchemaColumn.getContentMediaType())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("contentMediaType", dataStoreApiSchemaColumn.getContentMediaType()));
        }
        if (dataStoreApiSchemaColumn.getPrecision() >= 0) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("precision", String.valueOf(dataStoreApiSchemaColumn.getPrecision())));
        }
        if (dataStoreApiSchemaColumn.getScale() >= 0) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("scale", String.valueOf(dataStoreApiSchemaColumn.getScale())));
        }
        if (dataStoreApiSchemaColumn.getMinimum() >= 0) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("minimum", String.valueOf(dataStoreApiSchemaColumn.getMinimum())));
        }
        if (dataStoreApiSchemaColumn.getMaximum() >= 0) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("maximum", String.valueOf(dataStoreApiSchemaColumn.getMaximum())));
        }
        if (dataStoreApiSchemaColumn.getPartitionKeyPosition() >= 0) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("partitionKeyPosition", String.valueOf(dataStoreApiSchemaColumn.getMaximum())));
        }

        if (dataStoreApiSchemaColumn.getClusterKeyPosition() >= 0) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("clusterKeyPosition", String.valueOf(dataStoreApiSchemaColumn.getClusterKeyPosition())));
        }

        extractBooleanValuesFromSchemaColumn(dataStoreApiSchemaColumn, additionalPropertiesRes);

        return additionalPropertiesRes;
    }

    private static void extractBooleanValuesFromSchemaColumn(DataStoreApiSchemaColumn dataStoreApiSchemaColumn, List<AdditionalPropertiesRes> additionalPropertiesRes) {
        additionalPropertiesRes.add(new AdditionalPropertiesRes("isClassified", dataStoreApiSchemaColumn.isClassified() ? "true" : "false"));
        additionalPropertiesRes.add(new AdditionalPropertiesRes("isUnique", dataStoreApiSchemaColumn.isUnique() ? "true" : "false"));
        additionalPropertiesRes.add(new AdditionalPropertiesRes("exclusiveMinimum", dataStoreApiSchemaColumn.isExclusiveMinimum() ? "true" : "false"));
        additionalPropertiesRes.add(new AdditionalPropertiesRes("exclusiveMaximum", dataStoreApiSchemaColumn.isExclusiveMaximum() ? "true" : "false"));
        additionalPropertiesRes.add(new AdditionalPropertiesRes("readOnly", dataStoreApiSchemaColumn.isReadOnly() ? "true" : "false"));
        additionalPropertiesRes.add(new AdditionalPropertiesRes("writeOnly", dataStoreApiSchemaColumn.isWriteOnly() ? "true" : "false"));
        additionalPropertiesRes.add(new AdditionalPropertiesRes("isNullable", dataStoreApiSchemaColumn.isNullable() ? "true" : "false"));
        additionalPropertiesRes.add(new AdditionalPropertiesRes("isPartitionStatus", dataStoreApiSchemaColumn.isPartitionStatus() ? "true" : "false"));
        additionalPropertiesRes.add(new AdditionalPropertiesRes("isClusterStatus", dataStoreApiSchemaColumn.isClusterStatus() ? "true" : "false"));
        additionalPropertiesRes.add(new AdditionalPropertiesRes("isRequired", dataStoreApiSchemaColumn.isRequired() ? "true" : "false"));
    }
}
