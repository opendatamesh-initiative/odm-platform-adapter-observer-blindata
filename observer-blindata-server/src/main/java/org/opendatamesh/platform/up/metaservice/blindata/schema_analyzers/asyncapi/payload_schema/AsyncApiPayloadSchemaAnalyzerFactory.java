package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.payload_schema;

import java.util.List;
import java.util.Map;

public abstract class AsyncApiPayloadSchemaAnalyzerFactory {
    private static final String supportedFileEncoding = "json";
    private static final Map<String, AsyncApiPayloadSchemaAnalyzer> formatAnalyzerMap = Map.of(
            "application/vnd.apache.avro", new AvroAnalyzer(),
            "application/schema", new JsonSchemaAnalyzer()
    );

    public static AsyncApiPayloadSchemaAnalyzer getPayloadAnalyzer(String fullSchemaFormat) throws UnsupportedSchemaFormatException, UnsupportedFileEncodingException {
        List<String> s = List.of(fullSchemaFormat.split("\\+"));
        String schemaFormat = s.get(0);
        String fileEncoding = s.size() > 1 ? s.get(1) : "json";

        if (!fileEncoding.toLowerCase().contains(supportedFileEncoding)) {
            throw new UnsupportedSchemaFormatException(fileEncoding);
        }

        return formatAnalyzerMap.entrySet()
                .stream()
                .filter(entry -> schemaFormat.toLowerCase().contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new UnsupportedSchemaFormatException(schemaFormat));
    }

}
