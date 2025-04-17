package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

import com.google.common.collect.Lists;
import org.opendatamesh.dpds.datastoreapi.v1.extensions.DataStoreApiStandardDefinitionVisitor;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDAdditionalPropertiesRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt.*;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalEntityShortRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalFieldRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityStrategyRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1.model.DataStoreApiBlindataDefinition;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1.model.DataStoreApiBlindataDefinitionProperty;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1.model.quality.Quality;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1.model.quality.QualityIssuePolicy;
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
        BDPhysicalEntityRes physicalEntity = definitionToPhysicalEntity(definition);

        //Handle physical fields
        if (!CollectionUtils.isEmpty(definition.getProperties())) {
            for (DataStoreApiBlindataDefinitionProperty definitionProperty : definition.getProperties().values()) {
                BDPhysicalFieldRes physicalField = definitionPropertyToPhysicalField(definitionProperty);
                physicalField.setPhysicalEntity(new BDPhysicalEntityShortRes(physicalEntity));
                physicalEntity.getPhysicalFields().add(physicalField);
                //Handle physical fields quality checks
                extractPhysicalFieldQualityCheckFromDefinitionProperty(definitionProperty, physicalField);
            }
        }

        //Handle physical entity quality checks
        extractPhysicalEntityQualityCheckFromDefinition(definition, physicalEntity);
        physicalEntityPresenter.presentPhysicalEntity(physicalEntity);
    }

    private void extractPhysicalEntityQualityCheckFromDefinition(DataStoreApiBlindataDefinition definition, BDPhysicalEntityRes physicalEntity) {
        if (!CollectionUtils.isEmpty(definition.getQuality())) {
            for (Quality quality : definition.getQuality()) {
                QualityCheck qualityCheck = qualityToQualityCheck(quality);
                if (!StringUtils.hasText(qualityCheck.getCode())) {
                    getUseCaseLogger().warn("Quality check does not have code.");
                    continue;
                }
                qualityCheck.setPhysicalEntities(Lists.newArrayList(physicalEntity));
                qualityCheckPresenter.presentQualityCheck(qualityCheck);
            }
        }
    }

    private void extractPhysicalFieldQualityCheckFromDefinitionProperty(DataStoreApiBlindataDefinitionProperty definitionProperty, BDPhysicalFieldRes physicalField) {
        if (!CollectionUtils.isEmpty(definitionProperty.getQuality())) {
            for (Quality quality : definitionProperty.getQuality()) {
                QualityCheck qualityCheck = qualityToQualityCheck(quality);
                if (!StringUtils.hasText(qualityCheck.getCode())) {
                    getUseCaseLogger().warn("Quality check does not have code.");
                    continue;
                }
                qualityCheck.setPhysicalFields(Lists.newArrayList(physicalField));
                qualityCheckPresenter.presentQualityCheck(qualityCheck);
            }
        }
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
        qualityCheck.setIsEnabled(true); //Default, overwritten if customProperty field is present
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

        addIfPresent(qualityCheck.getAdditionalProperties(), "dimension", quality.getDimension());
        addIfPresent(qualityCheck.getAdditionalProperties(), "unit", quality.getUnit());
        addIfPresent(qualityCheck.getAdditionalProperties(), "constraint_type", quality.getType());
        addIfPresent(qualityCheck.getAdditionalProperties(), "quality_engine", quality.getEngine());

        handleQualityCustomProperties(quality, qualityCheck);
        handleQualityIssuePolicies(quality, qualityCheck);
        return qualityCheck;
    }

    private void handleQualityIssuePolicies(Quality quality, QualityCheck qualityCheck) {
        if (quality.getCustomProperties() == null || CollectionUtils.isEmpty(quality.getCustomProperties().getIssuePolicies())) {
            return;
        }
        for (QualityIssuePolicy qualityIssuePolicy : quality.getCustomProperties().getIssuePolicies()) {
            BDIssuePolicyRes issuePolicy = new BDIssuePolicyRes();
            issuePolicy.setName(qualityIssuePolicy.getName());
            issuePolicy.setPolicyType(BDIssuePolicyType.valueOf(qualityIssuePolicy.getPolicyType()));
            issuePolicy.setActive(true);

            if (!StringUtils.hasText(qualityIssuePolicy.getSemaphoreColor())) {
                qualityIssuePolicy.setSemaphoreColor(BDQualitySemaphoreRes.RED.name());
            }

            switch (issuePolicy.getPolicyType()) {
                case SINGLE_RESULT_SEMAPHORE:
                    BDIssuePolicyContentSingleResultRes singleResultPolicy = new BDIssuePolicyContentSingleResultRes();
                    singleResultPolicy.setSemaphores(Lists.newArrayList(BDQualitySemaphoreRes.valueOf(qualityIssuePolicy.getSemaphoreColor())));
                    issuePolicy.setPolicyContent(singleResultPolicy);
                    break;
                case RECURRENT_RESULT_SEMAPHORE:
                    BDIssuePolicyContentRecurrentResultRes recurrentPolicy = new BDIssuePolicyContentRecurrentResultRes();
                    recurrentPolicy.setSemaphoresNumber(qualityIssuePolicy.getSemaphoresNumber() == null ? 1 : qualityIssuePolicy.getSemaphoresNumber());
                    recurrentPolicy.setAutoClose(qualityIssuePolicy.getAutoClose() == null ? false : qualityIssuePolicy.getAutoClose());
                    recurrentPolicy.setSemaphores(Lists.newArrayList(BDQualitySemaphoreRes.valueOf(qualityIssuePolicy.getSemaphoreColor())));
                    issuePolicy.setPolicyContent(recurrentPolicy);
                    break;
                default:
                    getUseCaseLogger().warn("Unsupported issue policy type: " + issuePolicy.getPolicyType());
            }

            BDIssueRes issueTemplate = new BDIssueRes();
            issueTemplate.setIssueType(BDIssueTypeRes.ALERT);
            issueTemplate.setName("Quality Alert - " + issuePolicy.getName());
            issueTemplate.setIssueStatus(BDIssueStatusRes.TO_DO);
            issueTemplate.setSeverity(StringUtils.hasText(qualityIssuePolicy.getSeverity()) ? BDIssueSeverityLevelRes.valueOf(qualityIssuePolicy.getSeverity()) : BDIssueSeverityLevelRes.INFO);
            issueTemplate.setPriorityOrder(3);
            qualityIssuePolicy.getAdditionalProperties()
                    .forEach((propKey, propValue) -> {
                        if (propKey.startsWith("blindataCustomProp-")) {
                            issueTemplate.getAdditionalProperties()
                                    .add(new BDAdditionalPropertiesRes(propKey.replace("blindataCustomProp-", ""), propValue.isTextual() ? propValue.asText() : propValue.toString()));
                        }
                    });

            issuePolicy.setIssueTemplate(issueTemplate);
            qualityCheck.getIssuePolicies().add(issuePolicy);
        }
    }

    private void handleQualityCustomProperties(Quality quality, QualityCheck qualityCheck) {
        if (quality.getCustomProperties() == null) {
            return;
        }

        if (StringUtils.hasText(quality.getCustomProperties().getDisplayName())) {
            qualityCheck.setName(quality.getCustomProperties().getDisplayName());
            qualityCheck.getAdditionalProperties().add(new BDAdditionalPropertiesRes("displayName", quality.getCustomProperties().getDisplayName()));
        }

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
        qualityCheck.setIsEnabled(isCheckEnabled);

        //Handling Blindata Additional Properties
        if (quality.getCustomProperties().getAdditionalProperties() != null) {
            quality.getCustomProperties()
                    .getAdditionalProperties()
                    .forEach((name, value) -> {
                        if (name.startsWith("blindataCustomProp-")) {
                            qualityCheck.getAdditionalProperties().add(new BDAdditionalPropertiesRes(name.replace("blindataCustomProp-", ""), value.isTextual() ? value.asText() : value.toString()));
                        }
                    });
        }
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
