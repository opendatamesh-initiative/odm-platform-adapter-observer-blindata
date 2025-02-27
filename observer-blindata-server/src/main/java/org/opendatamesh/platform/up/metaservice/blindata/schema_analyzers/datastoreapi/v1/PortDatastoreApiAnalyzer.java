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
import java.util.Optional;
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
        fieldRes.setOrdinalPosition(schema.getOrdinalPosition());
        fieldRes.setAdditionalProperties(extractAdditionalPropertiesForFields(schema));
        return fieldRes;
    }

    private List<AdditionalPropertiesRes> extractAdditionalPropertiesForFields(DataStoreApiSchemaColumn dataStoreApiSchemaColumn) {
        List<AdditionalPropertiesRes> additionalPropertiesRes = new ArrayList<>();

        extractStringValuesFromSchemaColumn(dataStoreApiSchemaColumn, additionalPropertiesRes);
        extractNumericValuesFromSchemaColumn(dataStoreApiSchemaColumn, additionalPropertiesRes);
        extractBooleanValuesFromSchemaColumn(dataStoreApiSchemaColumn, additionalPropertiesRes);

        if (!CollectionUtils.isEmpty(dataStoreApiSchemaColumn.getTags())) {
            dataStoreApiSchemaColumn.getTags().forEach(tag -> additionalPropertiesRes.add(new AdditionalPropertiesRes("tags", tag)));
        }
        if (!CollectionUtils.isEmpty(dataStoreApiSchemaColumn.getEnumValues())) {
            dataStoreApiSchemaColumn.getEnumValues().forEach(tag -> additionalPropertiesRes.add(new AdditionalPropertiesRes("enum", tag)));
        }

        return additionalPropertiesRes;
    }

    private void extractStringValuesFromSchemaColumn(DataStoreApiSchemaColumn dataStoreApiSchemaColumn, List<AdditionalPropertiesRes> additionalPropertiesRes) {
        addStringPropertyIfNotEmpty(additionalPropertiesRes, "displayName", dataStoreApiSchemaColumn.getDisplayName());
        addStringPropertyIfNotEmpty(additionalPropertiesRes, "summary", dataStoreApiSchemaColumn.getSummary());
        addStringPropertyIfNotEmpty(additionalPropertiesRes, "status", dataStoreApiSchemaColumn.getStatus());
        addStringPropertyIfNotEmpty(additionalPropertiesRes, "externalDocs", dataStoreApiSchemaColumn.getExternalDocs());
        addStringPropertyIfNotEmpty(additionalPropertiesRes, "classificationLevel", dataStoreApiSchemaColumn.getClassificationLevel());
        addStringPropertyIfNotEmpty(additionalPropertiesRes, "pattern", dataStoreApiSchemaColumn.getPattern());
        addStringPropertyIfNotEmpty(additionalPropertiesRes, "format", dataStoreApiSchemaColumn.getFormat());
        addStringPropertyIfNotEmpty(additionalPropertiesRes, "default", dataStoreApiSchemaColumn.getDefaultValue());
        addStringPropertyIfNotEmpty(additionalPropertiesRes, "contentEncoding", dataStoreApiSchemaColumn.getContentEncoding());
        addStringPropertyIfNotEmpty(additionalPropertiesRes, "contentMediaType", dataStoreApiSchemaColumn.getContentMediaType());
    }

    private void addStringPropertyIfNotEmpty(List<AdditionalPropertiesRes> properties, String key, String value) {
        if (StringUtils.hasText(value)) {
            properties.add(new AdditionalPropertiesRes(key, value));
        }
    }

    private void extractNumericValuesFromSchemaColumn(DataStoreApiSchemaColumn dataStoreApiSchemaColumn, List<AdditionalPropertiesRes> additionalPropertiesRes) {
        addIntegerPropertyIfValid(additionalPropertiesRes, "minLength", dataStoreApiSchemaColumn.getMinLength());
        addIntegerPropertyIfValid(additionalPropertiesRes, "maxLength", dataStoreApiSchemaColumn.getMaxLength());
        addIntegerPropertyIfValid(additionalPropertiesRes, "precision", dataStoreApiSchemaColumn.getPrecision());
        addIntegerPropertyIfValid(additionalPropertiesRes, "scale", dataStoreApiSchemaColumn.getScale());
        addIntegerPropertyIfValid(additionalPropertiesRes, "minimum", dataStoreApiSchemaColumn.getMinimum());
        addIntegerPropertyIfValid(additionalPropertiesRes, "maximum", dataStoreApiSchemaColumn.getMaximum());
        addIntegerPropertyIfValid(additionalPropertiesRes, "partitionKeyPosition", dataStoreApiSchemaColumn.getPartitionKeyPosition());
        addIntegerPropertyIfValid(additionalPropertiesRes, "clusterKeyPosition", dataStoreApiSchemaColumn.getClusterKeyPosition());
    }

    private void addIntegerPropertyIfValid(List<AdditionalPropertiesRes> properties, String key, Integer value) {
        Optional.ofNullable(value)
                .filter(v -> v >= 0)
                .ifPresent(v -> properties.add(new AdditionalPropertiesRes(key, String.valueOf(v))));
    }

    private void extractBooleanValuesFromSchemaColumn(DataStoreApiSchemaColumn dataStoreApiSchemaColumn, List<AdditionalPropertiesRes> additionalPropertiesRes) {
        addBooleanPropertyIfNotNull(additionalPropertiesRes, "isClassified", dataStoreApiSchemaColumn.getIsClassified());
        addBooleanPropertyIfNotNull(additionalPropertiesRes, "isUnique", dataStoreApiSchemaColumn.getIsUnique());
        addBooleanPropertyIfNotNull(additionalPropertiesRes, "exclusiveMinimum", dataStoreApiSchemaColumn.getExclusiveMinimum());
        addBooleanPropertyIfNotNull(additionalPropertiesRes, "exclusiveMaximum", dataStoreApiSchemaColumn.getExclusiveMaximum());
        addBooleanPropertyIfNotNull(additionalPropertiesRes, "readOnly", dataStoreApiSchemaColumn.getReadOnly());
        addBooleanPropertyIfNotNull(additionalPropertiesRes, "writeOnly", dataStoreApiSchemaColumn.getWriteOnly());
        addBooleanPropertyIfNotNull(additionalPropertiesRes, "isNullable", dataStoreApiSchemaColumn.getIsNullable());
        addBooleanPropertyIfNotNull(additionalPropertiesRes, "isPartitionStatus", dataStoreApiSchemaColumn.getPartitionStatus());
        addBooleanPropertyIfNotNull(additionalPropertiesRes, "isClusterStatus", dataStoreApiSchemaColumn.getClusterStatus());
        addBooleanPropertyIfNotNull(additionalPropertiesRes, "isRequired", dataStoreApiSchemaColumn.getRequired());
    }

    private void addBooleanPropertyIfNotNull(List<AdditionalPropertiesRes> properties, String key, Boolean value) {
        Optional.ofNullable(value)
                .ifPresent(v -> properties.add(new AdditionalPropertiesRes(key, Boolean.TRUE.equals(v) ? "true" : "false")));
    }
}
