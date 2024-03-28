package org.opendatamesh.platform.up.metaservice.blindata.resources.schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchemaContent {

    @JsonProperty("entities")
    private List<SchemaEntity> entities;

    @JsonCreator
    public SchemaContent(@JsonProperty("entities") List<SchemaEntity> entities) {
        this.entities = entities;
    }

}