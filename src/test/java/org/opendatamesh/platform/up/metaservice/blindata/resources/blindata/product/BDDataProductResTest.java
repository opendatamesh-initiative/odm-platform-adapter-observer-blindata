package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BDDataProductResTest {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Test
    public void testTeamCodeRoundTrip() throws Exception {
        String json = "{\"uuid\":\"u1\",\"identifier\":\"FQN\",\"teamCode\":\"team-a\",\"unknownField\":\"x\"}";

        BDDataProductRes dataProduct = objectMapper.readValue(json, BDDataProductRes.class);

        assertThat(dataProduct.getTeamCode()).isEqualTo("team-a");
        JsonNode serialized = objectMapper.readTree(objectMapper.writeValueAsString(dataProduct));
        assertThat(serialized.get("teamCode").asText()).isEqualTo("team-a");
    }
}
