package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.asyncapi.payload_schema;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDPhysicalFieldRes;

import java.util.List;

public interface AsyncApiPayloadSchemaAnalyzer {
    List<BDPhysicalFieldRes> payloadSchemaToBlindataPhysicalFields(String rawSchema, String rootName);
}
