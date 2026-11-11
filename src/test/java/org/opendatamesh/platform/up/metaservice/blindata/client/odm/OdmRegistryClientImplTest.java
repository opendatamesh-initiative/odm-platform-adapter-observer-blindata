package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.up.metaservice.blindata.exceptions.client.ClientException;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.v2.OdmDataProductVersionResourceV2;
import org.opendatamesh.platform.up.metaservice.blindata.utils.client.RestUtilsFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Registry V2 read of the temporary DevOps 2.0 bridge (BDMD-5558): route, 404 handling and error propagation.
 */
class OdmRegistryClientImplTest {

    private static final String URL = "http://registry/api/v2/pp/registry/products-versions/dpv-uuid-1";

    private MockRestServiceServer server;
    private OdmRegistryClientImpl client;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        client = new OdmRegistryClientImpl(RestUtilsFactory.getRestUtils(restTemplate), "http://registry");
    }

    @Test
    void whenVersionExistsThenItIsReadFromTheV2Route() {
        server.expect(requestTo(URL)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"uuid\":\"dpv-uuid-1\",\"tag\":\"v1.0.0-release\",\"versionNumber\":\"1.0.0\",\"content\":{\"info\":{\"version\":\"1.0.0\"}}}", MediaType.APPLICATION_JSON));

        Optional<OdmDataProductVersionResourceV2> version = client.getDataProductVersion("dpv-uuid-1");

        assertThat(version).isPresent();
        assertThat(version.get().getVersionNumber()).isEqualTo("1.0.0");
        assertThat(version.get().getContent().get("info").get("version").asText()).isEqualTo("1.0.0");
        server.verify();
    }

    @Test
    void whenRegistryAnswers404ThenEmpty() {
        server.expect(requestTo(URL)).andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThat(client.getDataProductVersion("dpv-uuid-1")).isEmpty();
    }

    @Test
    void whenRegistryAnswers500ThenClientExceptionPropagates() {
        server.expect(requestTo(URL)).andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> client.getDataProductVersion("dpv-uuid-1"))
                .isInstanceOf(ClientException.class);
    }
}
