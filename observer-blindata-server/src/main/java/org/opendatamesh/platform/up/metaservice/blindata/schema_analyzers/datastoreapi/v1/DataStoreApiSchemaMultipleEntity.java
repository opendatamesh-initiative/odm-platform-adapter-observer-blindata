package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = DataStoreApiSchemaMultipleEntity.class)
class DataStoreApiSchemaMultipleEntity extends DataStoreApiSchema {
    private int id;
    private String name;
    private String version;
    private String mediaType;
    private DataStoreApiSchemaContent content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public DataStoreApiSchemaContent getContent() {
        return content;
    }

    public void setContent(DataStoreApiSchemaContent content) {
        this.content = content;
    }
}


