package org.opendatamesh.platform.up.metaservice.blindata.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.client.odm.OdmRegistryClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.*;
import org.opendatamesh.platform.up.metaservice.blindata.resources.schema.SchemaColumn;
import org.opendatamesh.platform.up.metaservice.blindata.resources.schema.SchemaEntity;
import org.opendatamesh.platform.up.metaservice.blindata.resources.schema.SchemaResDeserializeEntitiesList;
import org.opendatamesh.platform.up.metaservice.blindata.resources.schema.SchemaResDeserializeSingleEntity;
import org.opendatamesh.platform.up.metaservice.server.services.MetaServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ODMRegistryProxy {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private OdmRegistryClient registryClient;
    
    @Value("${blindata.systemNameRegex}")
    private String systemNameRegex;
    @Value("${blindata.systemTechnologyRegex}")
    private String systemTechnologyRegex;


    private static final Logger logger = LoggerFactory.getLogger(ODMRegistryProxy.class);


    public List<BDDataProductPortAssetDetailRes> extractPhysicalResourcesFromPorts(List<PortDPDS> portDPDS) throws MetaServiceException {
        List<BDDataProductPortAssetDetailRes> dataProductPortAssetDetailRes = new ArrayList<>();
        try {

            for (PortDPDS port : portDPDS) {
                logger.info("Trying to get schema ids");
                final List<Integer> schemasId = registryClient.getSchemasId(port.getPromises().getApi().getId());
                for (Integer id : schemasId) {
                    logger.info("Trying to get Schema with id: " + id);
                    String schemaContent = registryClient.getSchemaContent(id);
                    logger.info("Schema retrieved");
                    dataProductPortAssetDetailRes.add(extractSchemaPropertiesFromSchemaContent("schema_name", port.getFullyQualifiedName(), schemaContent, port.getPromises().getPlatform()));
                }
            }
        } catch (Exception e) {
            throw new MetaServiceException("Unable to extract schemas: " + e.getMessage());
        }
        return dataProductPortAssetDetailRes;
    }

    private BDDataProductPortAssetDetailRes extractSchemaPropertiesFromSchemaContent(String schemaName, String portId, String schemaContent, String platform) throws JsonProcessingException {
        BDDataProductPortAssetDetailRes dataProductPortAssetDetailRes = new BDDataProductPortAssetDetailRes();
        BDProductPortAssetSystemRes assetSystemRes = new BDProductPortAssetSystemRes();
        List<BDPhysicalEntityRes> physicalEntityResList = new ArrayList<>();
        List<BDProductPortAssetSystemRes> assets = new ArrayList<>();
        final List<SchemaEntity> schemaEntities = objectMapper.readValue(schemaContent, SchemaResDeserializeEntitiesList.class).getContent().getEntities();
        if (schemaEntities == null) {
            SchemaResDeserializeSingleEntity schemaResDeserializeSingle = objectMapper.readValue(schemaContent, SchemaResDeserializeSingleEntity.class);
            assetSystemRes.setSystem(getSystem(platform));
            BDPhysicalEntityRes extractedEntityFromSchema = fromSchemaEntityToPhysicalEntity(schemaName, schemaResDeserializeSingle.getContent(), assetSystemRes.getSystem());
            physicalEntityResList.add(extractedEntityFromSchema);
            assetSystemRes.setPhysicalEntities(physicalEntityResList);
            dataProductPortAssetDetailRes.setPortIdentifier(portId);
            assets.add(assetSystemRes);
            dataProductPortAssetDetailRes.setAssets(assets);
        } else {
            assetSystemRes.setSystem(getSystem(platform));
            for (SchemaEntity entity : schemaEntities) {
                BDPhysicalEntityRes extractedEntityFromSchema = fromSchemaEntityToPhysicalEntity(schemaName, entity, assetSystemRes.getSystem());
                physicalEntityResList.add(extractedEntityFromSchema);
            }
            assetSystemRes.setPhysicalEntities(physicalEntityResList);
            assets.add(assetSystemRes);
            dataProductPortAssetDetailRes.setPortIdentifier(portId);
            dataProductPortAssetDetailRes.setAssets(assets);
        }
        return dataProductPortAssetDetailRes;
    }

    private BDPhysicalEntityRes fromSchemaEntityToPhysicalEntity(String schema, SchemaEntity schemaEntity, BDSystemRes systemRes) {
        BDPhysicalEntityRes physicalEntityRes = new BDPhysicalEntityRes();
        physicalEntityRes.setSchema(schema);
        physicalEntityRes.setName(schemaEntity.getName());
        physicalEntityRes.setDescription(schemaEntity.getDescription());
        physicalEntityRes.setTableType(schemaEntity.getPhysicalType());
        physicalEntityRes.setSystem(systemRes);
        if (!CollectionUtils.isEmpty(schemaEntity.getProperties())) {
            physicalEntityRes.setPhysicalFields(schemaEntity.getProperties().values().stream().map(this::extractPhysicalFieldsFromColumn).collect(Collectors.toSet()));
        }
        physicalEntityRes.setAdditionalProperties(getExtractAdditionalPropertiesForEntities(schemaEntity));
        return physicalEntityRes;
    }

    private List<AdditionalPropertiesRes> getExtractAdditionalPropertiesForEntities(SchemaEntity schemaEntity) {
        List<AdditionalPropertiesRes> additionalPropertiesRes = new ArrayList<>();
        if (StringUtils.hasText(schemaEntity.getStatus())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("status", schemaEntity.getStatus()));
        }
        if (!CollectionUtils.isEmpty(schemaEntity.getTags())) {
            schemaEntity.getTags().forEach(tag -> additionalPropertiesRes.add(new AdditionalPropertiesRes("tags", tag)));
        }
        if (StringUtils.hasText(schemaEntity.getDomain())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("domain", schemaEntity.getDomain()));
        }
        if (StringUtils.hasText(schemaEntity.getContactPoints())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("contactPoints", schemaEntity.getContactPoints()));
        }
        if (StringUtils.hasText(schemaEntity.getScope())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("scope", schemaEntity.getScope()));
        }
        if (StringUtils.hasText(schemaEntity.getExternalDocs())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("externalDocs", schemaEntity.getExternalDocs()));
        }
        return additionalPropertiesRes;
    }

    private BDSystemRes getSystem(String platform) {
        BDSystemRes systemRes = new BDSystemRes();
        systemRes.setName(extractSystemName(platform));
        systemRes.setTechnology(extractSystemTechnology(platform));
        return systemRes;
    }


    private String extractSystemTechnology(String platform) {
        if (StringUtils.hasText(systemTechnologyRegex)) {
            Pattern pattern = Pattern.compile(systemNameRegex);
            Matcher matcher = pattern.matcher(platform);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    private String extractSystemName(String platform) {
        if (StringUtils.hasText(systemNameRegex)) {
            Pattern pattern = Pattern.compile(systemNameRegex);
            Matcher matcher = pattern.matcher(platform);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return platform;
    }


    private BDPhysicalFieldRes extractPhysicalFieldsFromColumn(SchemaColumn schema) {
        BDPhysicalFieldRes fieldRes = new BDPhysicalFieldRes();
        fieldRes.setName(schema.getName());
        fieldRes.setType(schema.getPhysicalType());
        fieldRes.setDescription(StringUtils.hasText(schema.getDescription()) ? schema.getDescription() : null);
        fieldRes.setAdditionalProperties(extractAdditionalPropertiesForFields(schema));
        return fieldRes;
    }

    private List<AdditionalPropertiesRes> extractAdditionalPropertiesForFields(SchemaColumn schemaColumn) {
        List<AdditionalPropertiesRes> additionalPropertiesRes = new ArrayList<>();
        if (StringUtils.hasText(schemaColumn.getDisplayName())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("displayName", schemaColumn.getDisplayName()));
        }
        if (StringUtils.hasText(schemaColumn.getSummary())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("summary", schemaColumn.getSummary()));
        }
        if (StringUtils.hasText(schemaColumn.getStatus())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("status", schemaColumn.getStatus()));
        }
        if (!CollectionUtils.isEmpty(schemaColumn.getTags())) {
            schemaColumn.getTags().forEach(tag -> additionalPropertiesRes.add(new AdditionalPropertiesRes("tags", tag)));
        }
        if (!CollectionUtils.isEmpty(schemaColumn.getEnumValues())) {
            schemaColumn.getEnumValues().forEach(tag -> additionalPropertiesRes.add(new AdditionalPropertiesRes("enum", tag)));
        }
        if (StringUtils.hasText(schemaColumn.getExternalDocs())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("externalDocs", schemaColumn.getExternalDocs()));
        }
        if (StringUtils.hasText(schemaColumn.getClassificationLevel())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("classificationLevel", schemaColumn.getClassificationLevel()));
        }
        if (StringUtils.hasText(schemaColumn.getPattern())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("pattern", schemaColumn.getPattern()));
        }
        if (StringUtils.hasText(schemaColumn.getFormat())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("format", schemaColumn.getFormat()));
        }
        if (StringUtils.hasText(schemaColumn.getDefaultValue())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("default", schemaColumn.getDefaultValue()));
        }
        if (schemaColumn.getMinLength() >= 0) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("minLength", String.valueOf(schemaColumn.getMinLength())));
        }
        if (schemaColumn.getMaxLength() >= 0) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("maxLength", String.valueOf(schemaColumn.getMaxLength())));
        }
        if (StringUtils.hasText(schemaColumn.getContentEncoding())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("contentEncoding", schemaColumn.getContentEncoding()));
        }
        if (StringUtils.hasText(schemaColumn.getContentMediaType())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("contentMediaType", schemaColumn.getContentMediaType()));
        }
        if (schemaColumn.getPrecision() >= 0) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("precision", String.valueOf(schemaColumn.getPrecision())));
        }
        if (schemaColumn.getScale() >= 0) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("scale", String.valueOf(schemaColumn.getScale())));
        }
        if (schemaColumn.getMinimum() >= 0) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("minimum", String.valueOf(schemaColumn.getMinimum())));
        }
        if (schemaColumn.getMaximum() >= 0) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("maximum", String.valueOf(schemaColumn.getMaximum())));
        }
        if (schemaColumn.getPartitionKeyPosition() >= 0) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("partitionKeyPosition", String.valueOf(schemaColumn.getMaximum())));
        }

        if (schemaColumn.getClusterKeyPosition() >= 0) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("clusterKeyPosition", String.valueOf(schemaColumn.getClusterKeyPosition())));
        }

        extractBooleanValuesFromSchemaColumn(schemaColumn, additionalPropertiesRes);

        return additionalPropertiesRes;
    }

    private static void extractBooleanValuesFromSchemaColumn(SchemaColumn schemaColumn, List<AdditionalPropertiesRes> additionalPropertiesRes) {
        additionalPropertiesRes.add(new AdditionalPropertiesRes("isClassified", schemaColumn.isClassified() ? "true" : "false"));
        additionalPropertiesRes.add(new AdditionalPropertiesRes("isUnique", schemaColumn.isUnique() ? "true" : "false"));
        additionalPropertiesRes.add(new AdditionalPropertiesRes("exclusiveMinimum", schemaColumn.isExclusiveMinimum() ? "true" : "false"));
        additionalPropertiesRes.add(new AdditionalPropertiesRes("exclusiveMaximum", schemaColumn.isExclusiveMaximum() ? "true" : "false"));
        additionalPropertiesRes.add(new AdditionalPropertiesRes("readOnly", schemaColumn.isReadOnly() ? "true" : "false"));
        additionalPropertiesRes.add(new AdditionalPropertiesRes("writeOnly", schemaColumn.isWriteOnly() ? "true" : "false"));
        additionalPropertiesRes.add(new AdditionalPropertiesRes("isNullable", schemaColumn.isNullable() ? "true" : "false"));
        additionalPropertiesRes.add(new AdditionalPropertiesRes("isPartitionStatus", schemaColumn.isPartitionStatus() ? "true" : "false"));
        additionalPropertiesRes.add(new AdditionalPropertiesRes("isClusterStatus", schemaColumn.isClusterStatus() ? "true" : "false"));
        additionalPropertiesRes.add(new AdditionalPropertiesRes("isRequired", schemaColumn.isRequired() ? "true" : "false"));
    }
}
