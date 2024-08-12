package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.payload_schema;

public class UnsupportedFileEncodingException extends RuntimeException {
    public UnsupportedFileEncodingException(String message) {
        super("Unsupported file encoding: " + message);
    }
}
