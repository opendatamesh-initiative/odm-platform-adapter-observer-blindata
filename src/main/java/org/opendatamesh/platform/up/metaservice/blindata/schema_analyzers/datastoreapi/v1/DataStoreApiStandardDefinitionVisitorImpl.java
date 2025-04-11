package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

import com.google.common.collect.Lists;
import org.opendatamesh.dpds.datastoreapi.v1.extensions.DataStoreApiStandardDefinitionVisitor;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDAdditionalPropertiesRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalEntityShortRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalFieldRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityStrategyRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1.model.DataStoreApiBlindataDefinition;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1.model.DataStoreApiBlindataDefinitionProperty;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1.model.quality.Quality;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking.SemanticLinkManager;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;

class DataStoreApiStandardDefinitionVisitorImpl extends DataStoreApiStandardDefinitionVisitor<DataStoreApiBlindataDefinition> {

    private final DataStoreApiVisitorEntitiesPresenter physicalEntityPresenter;
    private final DataStoreApiVisitorQualityDefinitionsPresenter qualityCheckPresenter;
    private final SemanticLinkManager semanticLinkManager;
    private final String databaseSchemaName;

    protected DataStoreApiStandardDefinitionVisitorImpl(
            DataStoreApiVisitorEntitiesPresenter entitiesPresenter,
            DataStoreApiVisitorQualityDefinitionsPresenter qualityCheckPresenter,
            SemanticLinkManager semanticLinkManager,
            String databaseSchemaName
    ) {
        super(DataStoreApiBlindataDefinition.class);
        this.physicalEntityPresenter = entitiesPresenter;
        this.qualityCheckPresenter = qualityCheckPresenter;
        this.semanticLinkManager = semanticLinkManager;
        this.databaseSchemaName = databaseSchemaName;
    }

    @Override
    protected void visitDefinition(DataStoreApiBlindataDefinition definition) {
        List<QualityCheck> qualityChecks = new ArrayList<>();
        BDPhysicalEntityRes physicalEntity = definitionToPhysicalEntity(definition);

        //Handle physical fields
        if (!CollectionUtils.isEmpty(definition.getProperties())) {
            for (DataStoreApiBlindataDefinitionProperty definitionProperty : definition.getProperties().values()) {
                BDPhysicalFieldRes physicalField = definitionPropertyToPhysicalField(definitionProperty);
                physicalField.setPhysicalEntity(new BDPhysicalEntityShortRes(physicalEntity));
                physicalEntity.getPhysicalFields().add(physicalField);
                //Handle physical fields quality checks
                List<QualityCheck> physicalFieldQualityChecks = definitionPropertyQualityToPhysicalFieldQualityChecks(definitionProperty, physicalField);
                qualityChecks.addAll(physicalFieldQualityChecks);
            }
        }

        //Handle physical entity quality checks
        List<QualityCheck> physicalEntityQualityChecks = definitionQualityToPhysicalEntityQualityChecks(definition, physicalEntity);
        qualityChecks.addAll(physicalEntityQualityChecks);

        physicalEntityPresenter.presentPhysicalEntities(Lists.newArrayList(physicalEntity));
        qualityCheckPresenter.presentQualityChecks(qualityChecks);
    }

    private List<QualityCheck> definitionQualityToPhysicalEntityQualityChecks(DataStoreApiBlindataDefinition definition, BDPhysicalEntityRes physicalEntity) {
        List<QualityCheck> qualityChecks = new ArrayList<>();
        if (!CollectionUtils.isEmpty(definition.getQuality())) {
            for (Quality quality : definition.getQuality()) {
                QualityCheck qualityCheck = qualityToQualityCheck(quality);
                if (!StringUtils.hasText(qualityCheck.getCode())) {
                    getUseCaseLogger().warn("Quality check does not have code.");
                    continue;
                }
                qualityCheck.setPhysicalEntities(Lists.newArrayList(physicalEntity));
                qualityChecks.add(qualityCheck);
            }
        }
        return qualityChecks;
    }

    private List<QualityCheck> definitionPropertyQualityToPhysicalFieldQualityChecks(DataStoreApiBlindataDefinitionProperty definitionProperty, BDPhysicalFieldRes physicalField) {
        List<QualityCheck> qualityChecks = new ArrayList<>();
        if (!CollectionUtils.isEmpty(definitionProperty.getQuality())) {
            for (Quality quality : definitionProperty.getQuality()) {
                QualityCheck qualityCheck = qualityToQualityCheck(quality);
                if (!StringUtils.hasText(qualityCheck.getCode())) {
                    getUseCaseLogger().warn("Quality check does not have code.");
                    continue;
                }
                qualityCheck.setPhysicalFields(Lists.newArrayList(physicalField));
                qualityChecks.add(qualityCheck);
            }
        }
        return qualityChecks;
    }

    private BDPhysicalEntityRes definitionToPhysicalEntity(DataStoreApiBlindataDefinition dataStoreApiBlindataDefinition) {
        BDPhysicalEntityRes physicalEntity = new BDPhysicalEntityRes();
        physicalEntity.setSchema(databaseSchemaName);
        physicalEntity.setName(dataStoreApiBlindataDefinition.getName());
        physicalEntity.setDescription(dataStoreApiBlindataDefinition.getDescription());
        physicalEntity.setTableType(dataStoreApiBlindataDefinition.getPhysicalType());
        if (dataStoreApiBlindataDefinition.getsContext() != null) {
            semanticLinkManager.enrichWithSemanticContext(physicalEntity, dataStoreApiBlindataDefinition.getsContext());
        }
        physicalEntity.setAdditionalProperties(getExtractAdditionalPropertiesForEntities(dataStoreApiBlindataDefinition));
        return physicalEntity;
    }

    private QualityCheck qualityToQualityCheck(Quality quality) {
        QualityCheck qualityCheck = new QualityCheck();
        if (isReference(quality)) {
            qualityCheck.setCode(quality.getCustomProperties().getRefName());
            qualityCheck.setReference(true);
            return qualityCheck;
        }
        qualityCheck.setCode(quality.getName());
        qualityCheck.setName(quality.getName());
        qualityCheck.setDescription(quality.getDescription());

        if (quality.getMustBeGreaterOrEqualTo() != null) {
            qualityCheck.setScoreLeftValue(BigDecimal.valueOf(quality.getMustBeGreaterOrEqualTo()));
        }
        if (quality.getMustBeLessOrEqualTo() != null) {
            qualityCheck.setScoreRightValue(BigDecimal.valueOf(quality.getMustBeLessOrEqualTo()));
        }
        if (quality.getMustBe() != null) {
            qualityCheck.setScoreExpectedValue(BigDecimal.valueOf(quality.getMustBe()));
        }

        if (quality.getCustomProperties() != null) {
            String strategy = quality.getCustomProperties().getScoreStrategy();
            if (strategy != null) {
                qualityCheck.setScoreStrategy(BDQualityStrategyRes.valueOf(strategy));
            }
            Float warningThreshold = quality.getCustomProperties().getScoreWarningThreshold();
            if (warningThreshold != null) {
                qualityCheck.setWarningThreshold(BigDecimal.valueOf(warningThreshold));
            }
            Float successThreshold = quality.getCustomProperties().getScoreSuccessThreshold();
            if (successThreshold != null) {
                qualityCheck.setSuccessThreshold(BigDecimal.valueOf(successThreshold));
            }
            boolean isCheckEnabled = quality.getCustomProperties().getCheckEnabled();
            qualityCheck.setEnabled(isCheckEnabled);
        }

        //Additional Properties
        List<BDAdditionalPropertiesRes> qualityCheckAdditionalProperties = qualityCheck.getAdditionalProperties();
        addIfPresent(qualityCheckAdditionalProperties, "displayName", quality.getCustomProperties().getDisplayName());
        addIfPresent(qualityCheckAdditionalProperties, "dimension", quality.getDimension());
        addIfPresent(qualityCheckAdditionalProperties, "unit", quality.getUnit());
        addIfPresent(qualityCheckAdditionalProperties, "constraint_type", quality.getType());
        addIfPresent(qualityCheckAdditionalProperties, "quality_engine", quality.getEngine());
        if (quality.getCustomProperties().getAdditionalProperties() != null) {
            quality.getCustomProperties()
                    .getAdditionalProperties()
                    .forEach((name, value) -> {
                        if (name.startsWith("blindataCustomProp-")) {
                            qualityCheckAdditionalProperties.add(new BDAdditionalPropertiesRes(name.replace("blindataCustomProp-", ""), value.isTextual() ? value.asText() : value.toString()));
                        }
                    });
        }
        return qualityCheck;
    }

    private boolean isReference(Quality quality) {
        return quality.getCustomProperties() != null && StringUtils.hasText(quality.getCustomProperties().getRefName());
    }

    private List<BDAdditionalPropertiesRes> getExtractAdditionalPropertiesForEntities(DataStoreApiBlindataDefinition dataStoreApiSchemaEntity) {
        List<BDAdditionalPropertiesRes> additionalPropertiesRes = new ArrayList<>();
        if (StringUtils.hasText(dataStoreApiSchemaEntity.getStatus())) {
            additionalPropertiesRes.add(new BDAdditionalPropertiesRes("status", dataStoreApiSchemaEntity.getStatus()));
        }
        if (!CollectionUtils.isEmpty(dataStoreApiSchemaEntity.getTags())) {
            dataStoreApiSchemaEntity.getTags().forEach(tag -> additionalPropertiesRes.add(new BDAdditionalPropertiesRes("tags", tag)));
        }
        if (StringUtils.hasText(dataStoreApiSchemaEntity.getDomain())) {
            additionalPropertiesRes.add(new BDAdditionalPropertiesRes("domain", dataStoreApiSchemaEntity.getDomain()));
        }
        if (StringUtils.hasText(dataStoreApiSchemaEntity.getContactPoints())) {
            additionalPropertiesRes.add(new BDAdditionalPropertiesRes("contactPoints", dataStoreApiSchemaEntity.getContactPoints()));
        }
        if (StringUtils.hasText(dataStoreApiSchemaEntity.getScope())) {
            additionalPropertiesRes.add(new BDAdditionalPropertiesRes("scope", dataStoreApiSchemaEntity.getScope()));
        }
        if (StringUtils.hasText(dataStoreApiSchemaEntity.getExternalDocs())) {
            additionalPropertiesRes.add(new BDAdditionalPropertiesRes("externalDocs", dataStoreApiSchemaEntity.getExternalDocs()));
        }
        return additionalPropertiesRes;
    }

    private BDPhysicalFieldRes definitionPropertyToPhysicalField(DataStoreApiBlindataDefinitionProperty schema) {
        BDPhysicalFieldRes fieldRes = new BDPhysicalFieldRes();
        fieldRes.setName(schema.getName());
        fieldRes.setType(schema.getPhysicalType());
        fieldRes.setDescription(StringUtils.hasText(schema.getDescription()) ? schema.getDescription() : null);
        fieldRes.setOrdinalPosition(schema.getOrdinalPosition());
        fieldRes.setAdditionalProperties(extractAdditionalPropertiesForFields(schema));
        return fieldRes;
    }

    private List<BDAdditionalPropertiesRes> extractAdditionalPropertiesForFields(DataStoreApiBlindataDefinitionProperty dataStoreApiSchemaColumn) {
        List<BDAdditionalPropertiesRes> additionalPropertiesRes = new ArrayList<>();

        extractStringValuesFromSchemaColumn(dataStoreApiSchemaColumn, additionalPropertiesRes);
        extractNumericValuesFromSchemaColumn(dataStoreApiSchemaColumn, additionalPropertiesRes);
        extractBooleanValuesFromSchemaColumn(dataStoreApiSchemaColumn, additionalPropertiesRes);

        if (!CollectionUtils.isEmpty(dataStoreApiSchemaColumn.getTags())) {
            dataStoreApiSchemaColumn.getTags().forEach(tag -> additionalPropertiesRes.add(new BDAdditionalPropertiesRes("tags", tag)));
        }
        if (!CollectionUtils.isEmpty(dataStoreApiSchemaColumn.getEnumValues())) {
            dataStoreApiSchemaColumn.getEnumValues().forEach(tag -> additionalPropertiesRes.add(new BDAdditionalPropertiesRes("enum", tag)));
        }

        return additionalPropertiesRes;
    }

    private void extractStringValuesFromSchemaColumn(DataStoreApiBlindataDefinitionProperty dataStoreApiSchemaColumn, List<BDAdditionalPropertiesRes> additionalPropertiesRes) {
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

    private void addStringPropertyIfNotEmpty(List<BDAdditionalPropertiesRes> properties, String key, String value) {
        if (StringUtils.hasText(value)) {
            properties.add(new BDAdditionalPropertiesRes(key, value));
        }
    }

    private void extractNumericValuesFromSchemaColumn(DataStoreApiBlindataDefinitionProperty dataStoreApiSchemaColumn, List<BDAdditionalPropertiesRes> additionalPropertiesRes) {
        addIntegerPropertyIfValid(additionalPropertiesRes, "minLength", dataStoreApiSchemaColumn.getMinLength());
        addIntegerPropertyIfValid(additionalPropertiesRes, "maxLength", dataStoreApiSchemaColumn.getMaxLength());
        addIntegerPropertyIfValid(additionalPropertiesRes, "precision", dataStoreApiSchemaColumn.getPrecision());
        addIntegerPropertyIfValid(additionalPropertiesRes, "scale", dataStoreApiSchemaColumn.getScale());
        addIntegerPropertyIfValid(additionalPropertiesRes, "minimum", dataStoreApiSchemaColumn.getMinimum());
        addIntegerPropertyIfValid(additionalPropertiesRes, "maximum", dataStoreApiSchemaColumn.getMaximum());
        addIntegerPropertyIfValid(additionalPropertiesRes, "partitionKeyPosition", dataStoreApiSchemaColumn.getPartitionKeyPosition());
        addIntegerPropertyIfValid(additionalPropertiesRes, "clusterKeyPosition", dataStoreApiSchemaColumn.getClusterKeyPosition());
    }

    private void addIntegerPropertyIfValid(List<BDAdditionalPropertiesRes> properties, String key, Integer value) {
        Optional.ofNullable(value)
                .filter(v -> v >= 0)
                .ifPresent(v -> properties.add(new BDAdditionalPropertiesRes(key, String.valueOf(v))));
    }

    private void extractBooleanValuesFromSchemaColumn(DataStoreApiBlindataDefinitionProperty dataStoreApiSchemaColumn, List<BDAdditionalPropertiesRes> additionalPropertiesRes) {
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

    private void addBooleanPropertyIfNotNull(List<BDAdditionalPropertiesRes> properties, String key, Boolean value) {
        Optional.ofNullable(value)
                .ifPresent(v -> properties.add(new BDAdditionalPropertiesRes(key, Boolean.TRUE.equals(v) ? "true" : "false")));
    }

    private void addIfPresent(List<BDAdditionalPropertiesRes> props, String name, Object value) {
        if (value != null) {
            props.add(new BDAdditionalPropertiesRes(name, value.toString()));
        }
    }
}
