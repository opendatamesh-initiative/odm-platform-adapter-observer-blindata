package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.payload_schema;

import java.util.List;
import java.util.Map;

public abstract class AsyncApiPayloadSchemaAnalyzerFactory {
    private static final String SUPPORTED_FILE_ENCODING = "json";
    private static final Map<String, AsyncApiPayloadSchemaAnalyzer> FORMAT_ANALYZER_MAP = Map.of(
            "application/vnd.apache.avro", new AsyncApiPayloadAvroSchemaAnalyzer(),
            "application/schema", new AsyncApiPayloadJsonSchemaAnalyzer()
    );

    public static AsyncApiPayloadSchemaAnalyzer getPayloadAnalyzer(String fullSchemaFormat) throws UnsupportedSchemaFormatException, UnsupportedFileEncodingException {
        List<String> s = List.of(fullSchemaFormat.split("\\+"));
        String schemaFormat = s.get(0);
        String fileEncoding = s.size() > 1 ? s.get(1) : "json";

        if (!fileEncoding.toLowerCase().contains(SUPPORTED_FILE_ENCODING)) {
            throw new UnsupportedSchemaFormatException(fileEncoding);
        }

        return FORMAT_ANALYZER_MAP.entrySet()
                .stream()
                .filter(entry -> schemaFormat.toLowerCase().contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new UnsupportedSchemaFormatException(schemaFormat));
    }

}
