package org.opendatamesh.platform.up.metaservice.blindata.client.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.exceptions.ClientException;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.http.HttpEntity;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.http.HttpHeader;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.http.HttpMethod;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.http.Oauth2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class RestTemplateWrapperTest {

    @Mock
    private RestTemplate mockRestTemplate;

    private RestTemplateWrapper restTemplateWrapper;

    @BeforeEach
    void setUp() throws Exception {
        AutoCloseable mocks = MockitoAnnotations.openMocks(this);
        restTemplateWrapper = RestTemplateWrapper.wrap(mockRestTemplate);
        mocks.close();
    }

    @Test
    void testExchangeSuccess() throws ClientException {
        // Prepare test data
        String url = "http://example.com/api";
        HttpMethod method = HttpMethod.GET;
        List<HttpHeader> headers = new ArrayList<>();
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        Map<String, ?> uriVariables = new HashMap<>();

        // Mock response
        TestResponse response = new TestResponse("success");
        ResponseEntity<TestResponse> responseEntity = ResponseEntity.ok(response);

        // Setup mock behavior
        when(mockRestTemplate.exchange(
                eq(url),
                eq(org.springframework.http.HttpMethod.GET),
                any(),
                eq(TestResponse.class),
                eq(uriVariables)
        )).thenReturn(responseEntity);

        // Execute test
        TestResponse result = restTemplateWrapper.exchange(url, method, requestEntity, TestResponse.class, uriVariables);

        // Verify
        assertNotNull(result);
        assertEquals("success", result.getResult());
    }

    @Test
    void testDownloadSuccess() throws ClientException {
        // Prepare test data
        String url = "http://example.com/download";
        List<HttpHeader> headers = new ArrayList<>();
        Object resource = new Object();
        File storeLocation = new File("test.txt");

        // Setup mock behavior
        when(mockRestTemplate.execute(
                eq(url),
                eq(org.springframework.http.HttpMethod.POST),
                any(),
                any()
        )).thenReturn(storeLocation);

        // Execute test
        File result = restTemplateWrapper.download(url, headers, resource, storeLocation);

        // Verify
        assertNotNull(result);
        assertTrue(result.exists());
        assertEquals(storeLocation, result);
    }

    @Test
    void testConfigureOauth2() {
        // Prepare test data
        List<HttpHeader> authHeaders = new ArrayList<>();
        Oauth2 oauth2 = new Oauth2();
        oauth2.setUrl("http://example.com/oauth");
        oauth2.setClientId("test-client");
        oauth2.setClientSecret("test-secret");

        // Execute test
        RestUtils result = restTemplateWrapper
                .authenticate(authHeaders, oauth2)
                .build();

        // Verify
        assertNotNull(result);
        assertTrue(result instanceof BaseRestUtils);
    }

    @Test
    void testConfigureAsync() {
        // Prepare test data
        String asyncRequest = "/async/request";
        String asyncPoll = "/async/poll";

        // Execute test
        RestUtils result = restTemplateWrapper
                .async(asyncRequest, asyncPoll)
                .build();

        // Verify
        assertNotNull(result);
        assertTrue(result instanceof BaseRestUtils);
    }

    // Helper class for testing
    private static class TestResponse {
        private String result;

        public TestResponse() {
        }

        public TestResponse(String result) {
            this.result = result;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }
} 