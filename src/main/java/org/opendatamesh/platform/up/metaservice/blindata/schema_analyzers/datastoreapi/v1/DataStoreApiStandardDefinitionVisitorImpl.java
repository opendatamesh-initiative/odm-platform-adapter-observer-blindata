package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.opendatamesh.dpds.model.core.ExternalDocs;
import org.opendatamesh.dpds.visitors.core.StandardDefinitionVisitor;
import org.opendatamesh.platform.up.metaservice.blindata.configurations.BdDataProductConfig;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDAdditionalPropertiesRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDShortUserRes;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;

class DataStoreApiStandardDefinitionVisitorImpl implements StandardDefinitionVisitor<DataStoreApiBlindataDefinition> {

    private final DataStoreApiVisitorEntitiesPresenter physicalEntityPresenter;
    private final DataStoreApiVisitorQualityDefinitionsPresenter qualityCheckPresenter;
    private final SemanticLinkManager semanticLinkManager;
    private final String databaseSchemaName;
    private final BdDataProductConfig bdDataProductConfig;

    protected DataStoreApiStandardDefinitionVisitorImpl(
            DataStoreApiVisitorEntitiesPresenter entitiesPresenter,
            DataStoreApiVisitorQualityDefinitionsPresenter qualityCheckPresenter,
            SemanticLinkManager semanticLinkManager,
            String databaseSchemaName,
            BdDataProductConfig bdDataProductConfig
    ) {
        this.physicalEntityPresenter = entitiesPresenter;
        this.qualityCheckPresenter = qualityCheckPresenter;
        this.semanticLinkManager = semanticLinkManager;
        this.databaseSchemaName = databaseSchemaName;
        this.bdDataProductConfig = bdDataProductConfig;
    }

    @Override
    public void visit(DataStoreApiBlindataDefinition definition) {
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
        if (definition.getsContext() != null) {
            semanticLinkManager.enrichWithSemanticContext(physicalEntity, definition.getsContext());
        }

        //Handle physical entity quality checks
        extractPhysicalEntityQualityCheckFromDefinition(definition, physicalEntity);
        physicalEntityPresenter.presentPhysicalEntity(physicalEntity);
    }

    private void extractPhysicalEntityQualityCheckFromDefinition(DataStoreApiBlindataDefinition definition, BDPhysicalEntityRes physicalEntity) {
        if (!CollectionUtils.isEmpty(definition.getQuality())) {
            for (Quality quality : definition.getQuality()) {
                if (qualityIsNotValid(quality)) continue;

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
                if (qualityIsNotValid(quality)) continue;

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

    //It should have the name, or it should be a reference
    private boolean qualityIsNotValid(Quality quality) {
        boolean isNotValid = !StringUtils.hasText(quality.getName()) && !isReference(quality);
        if (isNotValid) {
            try {
                getUseCaseLogger().warn("Quality object inside datastoreApi is not valid: " + new ObjectMapper().setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL).writeValueAsString(quality));
            } catch (JsonProcessingException e) {
                getUseCaseLogger().warn(e.getMessage(), e);
            }
        }
        return isNotValid;
    }

    private BDPhysicalEntityRes definitionToPhysicalEntity(DataStoreApiBlindataDefinition dataStoreApiBlindataDefinition) {
        BDPhysicalEntityRes physicalEntity = new BDPhysicalEntityRes();
        physicalEntity.setSchema(databaseSchemaName);
        physicalEntity.setName(dataStoreApiBlindataDefinition.getName());
        physicalEntity.setDescription(dataStoreApiBlindataDefinition.getDescription());
        physicalEntity.setTableType(dataStoreApiBlindataDefinition.getPhysicalType());
        physicalEntity.setAdditionalProperties(extractAdditionalPropertiesForEntity(dataStoreApiBlindataDefinition));
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
            if (qualityIssuePolicyIsNotValid(qualityCheck, qualityIssuePolicy)) break;

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
            issueTemplate.setDescription(qualityIssuePolicy.getIssueDescription());

            if (StringUtils.hasText(qualityIssuePolicy.getIssueOwner())) {
                BDShortUserRes ownerUser = new BDShortUserRes();
                ownerUser.setUsername(qualityIssuePolicy.getIssueOwner());
                issueTemplate.setAssignee(ownerUser);
            }

            if (StringUtils.hasText(qualityIssuePolicy.getIssueReporter())) {
                BDShortUserRes reporterUser = new BDShortUserRes();
                reporterUser.setUsername(qualityIssuePolicy.getIssueReporter());
                issueTemplate.setReporter(reporterUser);
            }

            qualityIssuePolicy.getAdditionalProperties()
                    .forEach((propKey, propValue) -> {
                        if (propKey.startsWith("blindataCustomProp-")) {
                            String propName = propKey.replace("blindataCustomProp-", "");
                            addAdditionalPropertyValue(issueTemplate.getAdditionalProperties(), propName, propValue);
                        }
                    });

            issuePolicy.setIssueTemplate(issueTemplate);
            qualityCheck.getIssuePolicies().add(issuePolicy);
        }
    }

    private boolean qualityIssuePolicyIsNotValid(QualityCheck qualityCheck, QualityIssuePolicy qualityIssuePolicy) {
        if (!StringUtils.hasText(qualityIssuePolicy.getName())) {
            getUseCaseLogger().warn("Missing quality issue policy name for quality check: " + qualityCheck.getName());
            return true;
        }
        if (!StringUtils.hasText(qualityIssuePolicy.getPolicyType())) {
            getUseCaseLogger().warn("Missing quality issue policy type for quality check: " + qualityCheck.getName());
            return true;
        }
        return false;
    }

    private void handleQualityCustomProperties(Quality quality, QualityCheck qualityCheck) {
        if (quality.getCustomProperties() == null) {
            return;
        }

        if (StringUtils.hasText(quality.getCustomProperties().getDisplayName())) {
            qualityCheck.setName(quality.getCustomProperties().getDisplayName());
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

        boolean isCheckEnabled = Boolean.TRUE.equals(quality.getCustomProperties().getCheckEnabled());
        qualityCheck.setIsEnabled(isCheckEnabled);

        //Handling Blindata Additional Properties
        if (quality.getCustomProperties().getAdditionalProperties() != null) {
            quality.getCustomProperties()
                    .getAdditionalProperties()
                    .forEach((name, value) -> {
                        if (name.startsWith("blindataCustomProp-")) {
                            String propName = name.replace("blindataCustomProp-", "");
                            addAdditionalPropertyValue(qualityCheck.getAdditionalProperties(), propName, value);
                        }
                    });
        }
    }

    private boolean isReference(Quality quality) {
        return quality.getCustomProperties() != null && StringUtils.hasText(quality.getCustomProperties().getRefName());
    }

    private BDPhysicalFieldRes definitionPropertyToPhysicalField(DataStoreApiBlindataDefinitionProperty schema) {
        BDPhysicalFieldRes fieldRes = new BDPhysicalFieldRes();
        fieldRes.setName(schema.getName());
        fieldRes.setType(schema.getPhysicalType());
        fieldRes.setDescription(StringUtils.hasText(schema.getDescription()) ? schema.getDescription() : null);
        fieldRes.setOrdinalPosition(schema.getOrdinalPosition());
        fieldRes.setAdditionalProperties(extractAdditionalPropertiesForField(schema));
        return fieldRes;
    }

    private List<BDAdditionalPropertiesRes> extractAdditionalPropertiesForEntity(DataStoreApiBlindataDefinition dataStoreApiSchemaEntity) {
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
        handlePhysicalEntityCustomAdditionalProperties(additionalPropertiesRes, dataStoreApiSchemaEntity);
        return additionalPropertiesRes;
    }

    private List<BDAdditionalPropertiesRes> extractAdditionalPropertiesForField(DataStoreApiBlindataDefinitionProperty dataStoreApiSchemaColumn) {
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
        handlePhysicalFieldCustomAdditionalProperties(additionalPropertiesRes, dataStoreApiSchemaColumn);
        return additionalPropertiesRes;
    }

    private void handlePhysicalFieldCustomAdditionalProperties(List<BDAdditionalPropertiesRes> additionalProperties, DataStoreApiBlindataDefinitionProperty definitionProperty) {
        String addPropRegex = bdDataProductConfig.getAdditionalPropertiesRegex();
        if (!StringUtils.hasText(addPropRegex)) return;
        try {
            Pattern compiledPattern = Pattern.compile(addPropRegex);
            if (!CollectionUtils.isEmpty(definitionProperty.getAdditionalProperties()) && StringUtils.hasText(addPropRegex)) {
                definitionProperty.getAdditionalProperties().forEach((key, value) -> {
                    Matcher matcher = compiledPattern.matcher(key);
                    if (matcher.find()) {
                        String propName = matcher.group(1);
                        addAdditionalPropertyValue(additionalProperties, propName, value);
                    }
                });
            }
        } catch (PatternSyntaxException e) {
            getUseCaseLogger().warn("Invalid regex for additional properties: " + addPropRegex, e);
        }
    }

    private void handlePhysicalEntityCustomAdditionalProperties(List<BDAdditionalPropertiesRes> additionalProperties, DataStoreApiBlindataDefinition definition) {
        String addPropRegex = bdDataProductConfig.getAdditionalPropertiesRegex();
        if (!StringUtils.hasText(addPropRegex)) return;
        try {
            Pattern compiledPattern = Pattern.compile(addPropRegex);
            if (!CollectionUtils.isEmpty(definition.getAdditionalProperties()) && StringUtils.hasText(addPropRegex)) {
                definition.getAdditionalProperties().forEach((key, value) -> {
                    Matcher matcher = compiledPattern.matcher(key);
                    if (matcher.find()) {
                        String propName = matcher.group(1);
                        addAdditionalPropertyValue(additionalProperties, propName, value);
                    }
                });
            }
        } catch (PatternSyntaxException e) {
            getUseCaseLogger().warn("Invalid regex for additional properties: " + addPropRegex, e);
        }
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

    private void addAdditionalPropertyValue(List<BDAdditionalPropertiesRes> additionalProperties, String propName, JsonNode value) {
        if (value.isArray()) {
            // Create a separate additional property for each array element
            value.forEach(element -> {
                String elementValue = element.isTextual() ? element.asText() : element.toString();
                additionalProperties.add(new BDAdditionalPropertiesRes(propName, elementValue));
            });
        } else {
            // Handle single values as before
            additionalProperties.add(new BDAdditionalPropertiesRes(
                    propName,
                    value.isTextual() ? value.asText() : value.toString()
            ));
        }
    }

    @Override
    public void visit(ExternalDocs externalDocs) {
        //DO NOTHING
    }
}
