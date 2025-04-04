package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.message_payload_schema;

import com.google.common.collect.Sets;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Parser;
import org.apache.avro.Schema.Type;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDPhysicalFieldRes;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseLoggerContext.getUseCaseLogger;

class AsyncApiPayloadAvroSchemaAnalyzer implements AsyncApiPayloadSchemaAnalyzer {

    public List<BDPhysicalFieldRes> payloadSchemaToBlindataPhysicalFields(String rawSchema, String rootName) {
        List<BDPhysicalFieldRes> mappedPhysicalFieldsList = new ArrayList<>();

        Schema schema = new Parser().parse(rawSchema);
        if (schema.getType() != Type.RECORD) {
            getUseCaseLogger().warn(String.format("Avro root schema must be a RECORD instead of %s", schema.getType().getName()));
            return mappedPhysicalFieldsList;
        }

        List<AvscField> avroFields = new ArrayList<>();

        for (Field field : schema.getFields()) {
            avroFields.add(mapSchema(field.schema(), field.name()));
        }

        List<AvscField> flattenedAvroFields = new ArrayList<>();
        for (AvscField pf : avroFields) {
            flattenedAvroFields.add(pf);
            if (!CollectionUtils.isEmpty(pf.getNestedFields())) {
                flattenedAvroFields.addAll(flattenNestedFields(pf.getNestedFields()));
            }
        }

        for (AvscField pf : flattenedAvroFields) {
            BDPhysicalFieldRes mappedPhysicalField = new BDPhysicalFieldRes();
            mappedPhysicalField.setName(StringUtils.hasText(rootName) ? rootName + "." + pf.getFieldName() : pf.getFieldName());
            mappedPhysicalField.setType(pf.getFieldType());
            mappedPhysicalField.setOrdinalPosition(mappedPhysicalFieldsList.size() + 1);
            mappedPhysicalFieldsList.add(mappedPhysicalField);
        }
        return mappedPhysicalFieldsList;
    }

    private AvscField mapSchema(Schema avroSchema, String schemaName) {
        if (isPrimitiveSchema(avroSchema.getType())) {
            return mapPrimitiveSchemas(avroSchema, schemaName);
        } else {
            return mapComplexSchemas(avroSchema, schemaName);
        }
    }

    private AvscField mapPrimitiveSchemas(Schema avroSchema, String schemaName) {
        String fieldType = null;

        if (avroSchema.getLogicalType() != null) {
            fieldType = avroSchema.getLogicalType().getName();
        } else if (Type.BYTES.equals(avroSchema.getType())) {
            fieldType = String.format("%s(%s,%s)", avroSchema.getLogicalType(), avroSchema.getProp("precision"), avroSchema.getProp("scale"));
        } else if (isPrimitiveSchema(avroSchema.getType())) {
            fieldType = avroSchema.getType().getName();
        } else {
            getUseCaseLogger().warn(String.format("Avro schema %s of type %s is not supported", schemaName, avroSchema.getType().getName()));
        }
        return new AvscField(schemaName, fieldType);
    }

    private AvscField mapComplexSchemas(Schema avroSchema, String schemaName) {
        String fieldName = schemaName;
        String fieldType = null;
        List<AvscField> nestedFields = new ArrayList<>();

        switch (avroSchema.getType()) {
            case RECORD:
                List<AvscField> mappedFields = new ArrayList<>();
                for (Field field : avroSchema.getFields()) {
                    mappedFields.add(mapSchema(field.schema(), fieldName + "." + field.name()));
                }
                fieldType = avroSchema.getName();
                nestedFields.addAll(mappedFields);
                break;
            case ENUM:
                fieldType = avroSchema.getType().getName();/* + avroSchema.getEnumSymbols() avoided because it can surpass 255 characters limit */
                break;
            case ARRAY:
                AvscField mappedField = mapSchema(avroSchema.getElementType(), fieldName);
                fieldType = avroSchema.getType().getName() + "<" + mappedField.getFieldType() + ">";
                if (mappedField.getNestedFields() != null) {
                    nestedFields.addAll(mappedField.getNestedFields());
                }
                break;
            case UNION:
                List<AvscField> unionFields = new ArrayList<>();
                for (Schema s : avroSchema.getTypes()) {
                    unionFields.add(mapSchema(s, fieldName));
                }
                fieldType = avroSchema.getType().getName() + unionFields;
                for (AvscField f : unionFields) {
                    if (f.getNestedFields() != null) {
                        nestedFields.addAll(f.getNestedFields());
                    }
                }
                break;
            default:
                getUseCaseLogger().warn(String.format("Schema %s of type %s is not supported", schemaName, avroSchema.getType().getName()));
        }

        return new AvscField(fieldName, fieldType, nestedFields);
    }

    private List<AvscField> flattenNestedFields(List<AvscField> list) {
        List<AvscField> result = new ArrayList<>();
        for (AvscField el : list) {
            if (!CollectionUtils.isEmpty(el.getNestedFields())) {
                result.add(el);
                result.addAll(flattenNestedFields(el.getNestedFields()));
            } else {
                result.add(el);
            }
        }
        return result;
    }

    private boolean isPrimitiveSchema(Type schemaType) {
        return Sets.newHashSet(
                Type.NULL,
                Type.BOOLEAN,
                Type.STRING,
                Type.BYTES,
                Type.INT,
                Type.LONG,
                Type.FLOAT,
                Type.DOUBLE
        ).contains(schemaType);
    }
}


