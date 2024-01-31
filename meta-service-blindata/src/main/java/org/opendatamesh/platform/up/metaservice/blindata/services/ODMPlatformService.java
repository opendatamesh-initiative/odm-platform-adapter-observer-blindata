package org.opendatamesh.platform.up.metaservice.blindata.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.client.BlindataCredentials;
import org.opendatamesh.platform.up.metaservice.blindata.client.PlatformClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.*;
import org.opendatamesh.platform.up.metaservice.blindata.resources.schema.SchemaColumn;
import org.opendatamesh.platform.up.metaservice.blindata.resources.schema.SchemaEntity;
import org.opendatamesh.platform.up.metaservice.blindata.resources.schema.SchemaResDeserializeEntitiesList;
import org.opendatamesh.platform.up.metaservice.blindata.resources.schema.SchemaResDeserializeSingleEntity;
import org.opendatamesh.platform.up.metaservice.server.services.MetaServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ODMPlatformService {

    @Autowired
    private BlindataCredentials credentials;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private PlatformClient platformClient;


    private static final Logger logger = LoggerFactory.getLogger(ODMPlatformService.class);


    public List<DataProductPortAssetDetailRes> extractPhysicalResourcesFromPorts(List<PortDPDS> portDPDS) throws MetaServiceException {
        List<DataProductPortAssetDetailRes> dataProductPortAssetDetailRes = new ArrayList<>();
        try {

            for (PortDPDS port : portDPDS) {
                logger.info("Trying to get schema ids");
                final List<Integer> schemasId = platformClient.getSchemasId(port.getPromises().getApi().getId(), credentials);
                for (Integer id : schemasId) {
                    logger.info("Trying to get Schema with id: " + id);
                    String schemaContent = platformClient.getSchemaContent(id, credentials);
                    logger.info("Schema retrieved");
                    dataProductPortAssetDetailRes.add(extractSchemaPropertiesFromSchemaContent("schema_name", port.getFullyQualifiedName(), schemaContent, port.getPromises().getPlatform()));
                }
            }
        } catch (Exception e) {
            throw new MetaServiceException("Unable to extract schemas: " + e.getMessage());
        }
        return dataProductPortAssetDetailRes;
    }

    private DataProductPortAssetDetailRes extractSchemaPropertiesFromSchemaContent(String schemaName, String portId, String schemaContent, String platform) throws JsonProcessingException {
        DataProductPortAssetDetailRes dataProductPortAssetDetailRes = new DataProductPortAssetDetailRes();
        DataProductPortAssetSystemRes assetSystemRes = new DataProductPortAssetSystemRes();
        List<PhysicalEntityRes> physicalEntityResList = new ArrayList<>();
        List<DataProductPortAssetSystemRes> assets = new ArrayList<>();
        final List<SchemaEntity> schemaEntities = objectMapper.readValue(schemaContent, SchemaResDeserializeEntitiesList.class).getContent().getEntities();
        if (schemaEntities == null) {
            SchemaResDeserializeSingleEntity schemaResDeserializeSingle = objectMapper.readValue(schemaContent, SchemaResDeserializeSingleEntity.class);
            assetSystemRes.setSystem(getSystem(platform));
            PhysicalEntityRes extractedEntityFromSchema = fromSchemaEntityToPhysicalEntity(schemaName, schemaResDeserializeSingle.getContent(), assetSystemRes.getSystem());
            physicalEntityResList.add(extractedEntityFromSchema);
            assetSystemRes.setPhysicalEntities(physicalEntityResList);
            dataProductPortAssetDetailRes.setPortIdentifier(portId);
            assets.add(assetSystemRes);
            dataProductPortAssetDetailRes.setAssets(assets);
        } else {
            assetSystemRes.setSystem(getSystem(platform));
            for (SchemaEntity entity : schemaEntities) {
                PhysicalEntityRes extractedEntityFromSchema = fromSchemaEntityToPhysicalEntity(schemaName, entity, assetSystemRes.getSystem());
                physicalEntityResList.add(extractedEntityFromSchema);
            }
            assetSystemRes.setPhysicalEntities(physicalEntityResList);
            assets.add(assetSystemRes);
            dataProductPortAssetDetailRes.setPortIdentifier(portId);
            dataProductPortAssetDetailRes.setAssets(assets);
        }
        return dataProductPortAssetDetailRes;
    }

    private PhysicalEntityRes fromSchemaEntityToPhysicalEntity(String schema, SchemaEntity schemaEntity, SystemRes systemRes) {
        PhysicalEntityRes physicalEntityRes = new PhysicalEntityRes();
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
        if (StringUtils.hasText(schemaEntity.getKind())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("kind", schemaEntity.getKind()));
        }
        if (StringUtils.hasText(schemaEntity.getComments())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("comments", schemaEntity.getComments()));
        }
        if (StringUtils.hasText(schemaEntity.getStatus())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("status", schemaEntity.getStatus()));
        }
        if (StringUtils.hasText(schemaEntity.getTags())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("tags", schemaEntity.getTags()));
        }
        if (StringUtils.hasText(schemaEntity.getDomain())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("domain", schemaEntity.getTags()));
        }
        if (StringUtils.hasText(schemaEntity.getContactpoints())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("contactpoints", schemaEntity.getContactpoints()));
        }
        return additionalPropertiesRes;
    }

    private SystemRes getSystem(String platform) {
        SystemRes systemRes = new SystemRes();
        systemRes.setName(extractSystemName(platform));
        systemRes.setTechnology(extractSystemTechnology(platform));
        return systemRes;
    }


    private String extractSystemTechnology(String platform) {
        if (credentials.getSystemTechnologyRegex().isPresent()) {
            Pattern pattern = Pattern.compile(credentials.getSystemTechnologyRegex().get());
            Matcher matcher = pattern.matcher(platform);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    private String extractSystemName(String platform) {
        if (credentials.getSystemNameRegex().isPresent()) {
            Pattern pattern = Pattern.compile(credentials.getSystemNameRegex().get());
            Matcher matcher = pattern.matcher(platform);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return platform;
    }


    private PhysicalFieldRes extractPhysicalFieldsFromColumn(SchemaColumn schema) {
        PhysicalFieldRes fieldRes = new PhysicalFieldRes();
        fieldRes.setName(schema.getName());
        fieldRes.setType(schema.getPhysicalType());
        fieldRes.setDescription(StringUtils.hasText(schema.getDescription()) ? schema.getDescription() : null);
        fieldRes.setAdditionalProperties(extractAdditionalPropertiesForFields(schema));
        return fieldRes;
    }

    private List<AdditionalPropertiesRes> extractAdditionalPropertiesForFields(SchemaColumn schemaColumn) {
        List<AdditionalPropertiesRes> additionalPropertiesRes = new ArrayList<>();
        if (StringUtils.hasText(schemaColumn.getKind())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("kind", schemaColumn.getKind()));
        }
        if (StringUtils.hasText(schemaColumn.getComments())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("comments", schemaColumn.getComments()));
        }
        if (StringUtils.hasText(schemaColumn.getStatus())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("status", schemaColumn.getStatus()));
        }
        if (!CollectionUtils.isEmpty(schemaColumn.getExamples())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("examples", schemaColumn.getExamples().toString()));
        }
        if (!CollectionUtils.isEmpty(schemaColumn.getTags())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("tags", schemaColumn.getTags().toString()));
        }
        if (StringUtils.hasText(schemaColumn.getExternalDocs())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("externalDocs", schemaColumn.getExternalDocs()));
        }
        if (StringUtils.hasText(schemaColumn.getClassificationLevel())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("isClassified", schemaColumn.getClassificationLevel()));
        }
        if (StringUtils.hasText(schemaColumn.getPattern())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("pattern", schemaColumn.getPattern()));
        }
        if (StringUtils.hasText(schemaColumn.getFormat())) {
            additionalPropertiesRes.add(new AdditionalPropertiesRes("format", schemaColumn.getFormat()));
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

        additionalPropertiesRes.add(new AdditionalPropertiesRes("isClassified", schemaColumn.isClassified() ? "true" : "false"));
        additionalPropertiesRes.add(new AdditionalPropertiesRes("isUnique", schemaColumn.isUnique() ? "true" : "false"));
        additionalPropertiesRes.add(new AdditionalPropertiesRes("exclusiveMinimum", schemaColumn.isExclusiveMinimum() ? "true" : "false"));
        additionalPropertiesRes.add(new AdditionalPropertiesRes("exclusiveMaximum", schemaColumn.isExclusiveMaximum() ? "true" : "false"));
        additionalPropertiesRes.add(new AdditionalPropertiesRes("readOnly", schemaColumn.isReadOnly() ? "true" : "false"));
        additionalPropertiesRes.add(new AdditionalPropertiesRes("writeOnly", schemaColumn.isWriteOnly() ? "true" : "false"));

        return additionalPropertiesRes;
    }
}
