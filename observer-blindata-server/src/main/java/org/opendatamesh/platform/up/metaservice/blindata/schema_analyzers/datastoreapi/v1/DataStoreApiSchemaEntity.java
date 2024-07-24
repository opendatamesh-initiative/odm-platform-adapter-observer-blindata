package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = DataStoreApiSchemaEntity.class)
class DataStoreApiSchemaEntity extends DataStoreApiSchema {

    private String specification;
    private String specificationVersion;
    private DataStoreAPISchemaEntityDefinition definition;


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

    public DataStoreAPISchemaEntityDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(DataStoreAPISchemaEntityDefinition definition) {
        this.definition = definition;
    }
}


