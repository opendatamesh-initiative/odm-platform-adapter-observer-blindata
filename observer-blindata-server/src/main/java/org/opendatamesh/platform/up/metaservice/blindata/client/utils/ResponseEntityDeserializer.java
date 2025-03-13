package org.opendatamesh.platform.up.metaservice.blindata.client.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Base64;

public class ResponseEntityDeserializer extends JsonDeserializer<ResponseEntity<byte[]>> {
    @Override
    public ResponseEntity<byte[]> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        int statusCode = node.get("statusCodeValue").asInt();
        byte[] body = Base64.getDecoder().decode(node.get("body").asText());
        HttpHeaders headers = new ObjectMapper().convertValue(node.get("headers"), HttpHeaders.class);
        return ResponseEntity.status(statusCode).headers(headers).body(body);
    }
}
