package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.avro;

import java.util.List;

class AvscField {
    private String fieldName;
    private String fieldType;
    private List<AvscField> nestedFields;

    public AvscField(String fieldName, String fieldType) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    public AvscField(String fieldName, String fieldType, List<AvscField> nestedFields) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.nestedFields = nestedFields;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public List<AvscField> getNestedFields() {
        return nestedFields;
    }

    public void setNestedFields(List<AvscField> nestedFields) {
        this.nestedFields = nestedFields;
    }
}
