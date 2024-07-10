package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = DataStoreApiSchemaEntity.class)
class DataStoreApiSchemaEntity extends DataStoreApiSchema {

    private String specification;
    private String specificationVersion;
    private SchemaEntityDefinition definition;


    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getSpecificationVersion() {
        return specificationVersion;
    }

    public void setSpecificationVersion(String specificationVersion) {
        this.specificationVersion = specificationVersion;
    }

    public SchemaEntityDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(SchemaEntityDefinition definition) {
        this.definition = definition;
    }
}


