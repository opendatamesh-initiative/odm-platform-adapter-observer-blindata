package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = DataStoreApiSchemaDeserializer.class)
public abstract class DataStoreApiSchema {
}
