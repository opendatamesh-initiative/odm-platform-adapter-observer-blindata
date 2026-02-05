package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.v3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import org.opendatamesh.dpds.model.core.StandardDefinition;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalFieldRes;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.PortStandardDefinitionEntitiesExtractor;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.message_payload_schema.AsyncApiPayloadSchemaAnalyzer;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.message_payload_schema.AsyncApiPayloadSchemaAnalyzerFactory;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.message_payload_schema.UnsupportedSchemaFormatException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;

@Component
public class PortAsyncApi3EntitiesExtractor implements PortStandardDefinitionEntitiesExtractor {
    private final String SPECIFICATION = "asyncapi";
    private final String VERSION = "3.*.*";

    private final String TABLE_TYPE_TOPIC = "TOPIC";

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

    @Override
    public boolean supports(StandardDefinition portStandardDefinition) {
        return SPECIFICATION.equalsIgnoreCase(portStandardDefinition.getSpecification()) &&
                portStandardDefinition.getSpecificationVersion().matches(VERSION);
    }

    @Override
    public List<BDPhysicalEntityRes> extractEntities(StandardDefinition portStandardDefinition) {
        try {
            return extractSchemaPropertiesFromSchemaContent(portStandardDefinition);
        } catch (JsonProcessingException e) {
            getUseCaseLogger().warn(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private List<BDPhysicalEntityRes> extractSchemaPropertiesFromSchemaContent(StandardDefinition portStandardDefinition) throws JsonProcessingException {
        AsyncApi asyncAPI = objectMapper.convertValue(portStandardDefinition.getDefinition(), AsyncApi.class);

        List<BDPhysicalEntityRes> extractedPhysicalEntities = new ArrayList<>();
        for (Map.Entry<String, AsyncApiChannel> channel : asyncAPI.getChannels().entrySet()) {
            BDPhysicalEntityRes physicalEntity = new BDPhysicalEntityRes();
            physicalEntity.setName(channel.getKey());
            physicalEntity.setTableType(TABLE_TYPE_TOPIC);
            physicalEntity.setDescription(channel.getValue().getDescription());

            for (Map.Entry<String, AsyncApiMessage> message : channel.getValue().getMessages().entrySet()) {
                List<BDPhysicalFieldRes> messagePhysicalFields = messageToPhysicalFields(message.getKey(), message.getValue());
                physicalEntity.setPhysicalFields(Sets.newHashSet(messagePhysicalFields));
            }
            extractedPhysicalEntities.add(physicalEntity);
        }

        return extractedPhysicalEntities;
    }

    private List<BDPhysicalFieldRes> messageToPhysicalFields(String rootName, AsyncApiMessage message) throws JsonProcessingException {
        List<BDPhysicalFieldRes> extractedPhysicalFields = new ArrayList<>();

        BDPhysicalFieldRes rootPhysicalField = buildRootPhysicalField(rootName, message);
        extractedPhysicalFields.add(rootPhysicalField);

        if (message.getPayload() == null || !StringUtils.hasText(message.getPayload().getSchemaFormat())) {
            getUseCaseLogger().warn(String.format("Missing schema format on message: %s, default AsyncApi Schema Object is not supported", message.getTitle()));
            return extractedPhysicalFields;
        }

        try {
            AsyncApiPayloadSchemaAnalyzer payloadSchemaAnalyzer = AsyncApiPayloadSchemaAnalyzerFactory.getPayloadAnalyzer(message.getPayload().getSchemaFormat());
            String payload = objectMapper.writeValueAsString(message.getPayload().getSchema());
            List<BDPhysicalFieldRes> avroPhysicalFields = payloadSchemaAnalyzer.payloadSchemaToBlindataPhysicalFields(payload, rootPhysicalField.getName());
            extractedPhysicalFields.addAll(avroPhysicalFields);
        } catch (UnsupportedSchemaFormatException e) {
            getUseCaseLogger().warn(e.getMessage(), e);
        }

        return extractedPhysicalFields;
    }

    private BDPhysicalFieldRes buildRootPhysicalField(String rootName, AsyncApiMessage message) {
        BDPhysicalFieldRes rootField = new BDPhysicalFieldRes();
        rootField.setName(rootName);
        rootField.setOrdinalPosition(0);
        if (message.getContentType() != null) {
            rootField.setType(message.getContentType());
        }
        return rootField;
    }
}
