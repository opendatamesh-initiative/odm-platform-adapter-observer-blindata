package org.opendatamesh.platform.up.metaservice.blindata.resources.schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchemaRes {

    private int id;
    private String name;
    private String version;
    private String mediaType;
    private String content;

    @JsonCreator
    public SchemaRes(@JsonProperty("id") int id,
                     @JsonProperty("name") String name,
                     @JsonProperty("version") String version,
                     @JsonProperty("mediaType") String mediaType,
                     @JsonProperty("content") String content) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.mediaType = mediaType;
        this.content = content;
    }
}

