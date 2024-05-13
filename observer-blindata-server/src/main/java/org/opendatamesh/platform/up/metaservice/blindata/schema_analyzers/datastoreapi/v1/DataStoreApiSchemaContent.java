package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class DataStoreApiSchemaContent {

    @JsonProperty("entities")
    private List<DataStoreApiSchemaEntity> entities;

    @JsonCreator
    public DataStoreApiSchemaContent(@JsonProperty("entities") List<DataStoreApiSchemaEntity> entities) {
        this.entities = entities;
    }

}