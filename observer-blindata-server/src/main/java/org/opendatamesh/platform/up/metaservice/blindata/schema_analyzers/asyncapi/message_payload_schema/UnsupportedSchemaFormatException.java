package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.message_payload_schema;

public class UnsupportedSchemaFormatException extends RuntimeException {
    public UnsupportedSchemaFormatException(String message) {
        super("Unsupported schema format:" + message);
    }
}
