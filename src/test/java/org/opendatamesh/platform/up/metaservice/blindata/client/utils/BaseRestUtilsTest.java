package org.opendatamesh.platform.up.metaservice.blindata.client.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.exceptions.ClientException;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.http.HttpHeader;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.http.HttpMethod;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.jackson.PageUtility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class BaseRestUtilsTest {

    @Mock
    private RestUtilsTemplate mockRestTemplate;

    private BaseRestUtils baseRestUtils;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        AutoCloseable mocks = MockitoAnnotations.openMocks(this);
        baseRestUtils = new BaseRestUtils(mockRestTemplate);
        objectMapper = new ObjectMapper();
        mocks.close();
    }

    @Test
    void testGetPage() throws ClientException {
        // Prepare test data
        String url = "http://example.com/api";
        List<HttpHeader> headers = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 10);
        TestFilter filters = new TestFilter("test");

        // Mock response
        List<TestResponse> content = List.of(new TestResponse("test1"), new TestResponse("test2"));
        PageUtility<TestResponse> page = new PageUtility<>(content, pageable, 2);
        JsonNode responseNode = objectMapper.valueToTree(page);

        // Setup mock behavior
        when(mockRestTemplate.exchange(
                any(String.class),
                eq(HttpMethod.GET),
                any(),
                eq(JsonNode.class),
                any(Map.class)
        )).thenReturn(responseNode);

        // Execute test
        Page<TestResponse> result = baseRestUtils.getPage(url, headers, pageable, filters, TestResponse.class);

        // Verify
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getContent().size());
        assertEquals("test1", result.getContent().get(0).getResult());
        assertEquals("test2", result.getContent().get(1).getResult());
    }

    @Test
    void testGenericGet() throws ClientException {
        // Prepare test data
        String url = "http://example.com/api";
        List<HttpHeader> headers = new ArrayList<>();
        TestFilter filters = new TestFilter("test");

        // Mock response
        TestResponse response = new TestResponse("success");
        JsonNode responseNode = objectMapper.valueToTree(response);

        // Setup mock behavior
        when(mockRestTemplate.exchange(
                any(String.class),
                eq(HttpMethod.GET),
                any(),
                eq(JsonNode.class),
                any(Map.class)
        )).thenReturn(responseNode);

        // Execute test
        TestResponse result = baseRestUtils.genericGet(url, headers, filters, TestResponse.class);

        // Verify
        assertNotNull(result);
        assertEquals("success", result.getResult());
    }

    @Test
    void testGet() throws ClientException {
        // Prepare test data
        String url = "http://example.com/api";
        List<HttpHeader> headers = new ArrayList<>();
        String identifier = "123";

        // Mock response
        TestResponse response = new TestResponse("success");
        JsonNode responseNode = objectMapper.valueToTree(response);

        // Setup mock behavior
        when(mockRestTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(JsonNode.class),
                any(Map.class)
        )).thenReturn(responseNode);

        // Execute test
        TestResponse result = baseRestUtils.get(url, headers, identifier, TestResponse.class);

        // Verify
        assertNotNull(result);
        assertEquals("success", result.getResult());
    }

    @Test
    void testCreate() throws ClientException {
        // Prepare test data
        String url = "http://example.com/api";
        List<HttpHeader> headers = new ArrayList<>();
        TestResponse resourceToCreate = new TestResponse("test");

        // Mock response
        TestResponse response = new TestResponse("created");
        JsonNode responseNode = objectMapper.valueToTree(response);

        // Setup mock behavior
        when(mockRestTemplate.exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(),
                eq(JsonNode.class),
                any(Map.class)
        )).thenReturn(responseNode);

        // Execute test
        TestResponse result = baseRestUtils.create(url, headers, resourceToCreate, TestResponse.class);

        // Verify
        assertNotNull(result);
        assertEquals("created", result.getResult());
    }

    @Test
    void testPut() throws ClientException {
        // Prepare test data
        String url = "http://example.com/api";
        List<HttpHeader> headers = new ArrayList<>();
        String identifier = "123";
        TestResponse resourceToModify = new TestResponse("test");

        // Mock response
        TestResponse response = new TestResponse("updated");
        JsonNode responseNode = objectMapper.valueToTree(response);

        // Setup mock behavior
        when(mockRestTemplate.exchange(
                eq(url),
                eq(HttpMethod.PUT),
                any(),
                eq(JsonNode.class),
                any(Map.class)
        )).thenReturn(responseNode);

        // Execute test
        TestResponse result = baseRestUtils.put(url, headers, identifier, resourceToModify, TestResponse.class);

        // Verify
        assertNotNull(result);
        assertEquals("updated", result.getResult());
    }

    @Test
    void testPatch() throws ClientException {
        // Prepare test data
        String url = "http://example.com/api";
        List<HttpHeader> headers = new ArrayList<>();
        String identifier = "123";
        TestResponse resourceToModify = new TestResponse("test");

        // Mock response
        TestResponse response = new TestResponse("patched");
        JsonNode responseNode = objectMapper.valueToTree(response);

        // Setup mock behavior
        when(mockRestTemplate.exchange(
                eq(url),
                eq(HttpMethod.PATCH),
                any(),
                eq(JsonNode.class),
                any(Map.class)
        )).thenReturn(responseNode);

        // Execute test
        TestResponse result = baseRestUtils.patch(url, headers, identifier, resourceToModify, TestResponse.class);

        // Verify
        assertNotNull(result);
        assertEquals("patched", result.getResult());
    }

    @Test
    void testPatchWithError() {
        // Prepare test data
        String url = "http://example.com/api";
        List<HttpHeader> headers = new ArrayList<>();
        String identifier = "123";
        TestResponse resourceToModify = new TestResponse("test");

        // Setup mock behavior to throw exception
        when(mockRestTemplate.exchange(
                eq(url),
                eq(HttpMethod.PATCH),
                any(),
                eq(JsonNode.class),
                any(Map.class)
        )).thenThrow(new ClientException(404, "Resource not found"));

        // Execute test and verify exception
        assertThrows(ClientException.class, () -> {
            baseRestUtils.patch(url, headers, identifier, resourceToModify, TestResponse.class);
        });
    }

    @Test
    void testGenericPatch() throws ClientException {
        // Prepare test data
        String url = "http://example.com/api";
        List<HttpHeader> headers = new ArrayList<>();
        TestResponse resourceToModify = new TestResponse("test");

        // Mock response
        TestResponse response = new TestResponse("patched");
        JsonNode responseNode = objectMapper.valueToTree(response);

        // Setup mock behavior
        when(mockRestTemplate.exchange(
                eq(url),
                eq(HttpMethod.PATCH),
                any(),
                eq(JsonNode.class),
                any(Map.class)
        )).thenReturn(responseNode);

        // Execute test
        TestResponse result = baseRestUtils.genericPatch(url, headers, resourceToModify, TestResponse.class);

        // Verify
        assertNotNull(result);
        assertEquals("patched", result.getResult());
    }

    @Test
    void testGenericPatchWithError() {
        // Prepare test data
        String url = "http://example.com/api";
        List<HttpHeader> headers = new ArrayList<>();
        TestResponse resourceToModify = new TestResponse("test");

        // Setup mock behavior to throw exception
        when(mockRestTemplate.exchange(
                eq(url),
                eq(HttpMethod.PATCH),
                any(),
                eq(JsonNode.class),
                any(Map.class)
        )).thenThrow(new ClientException(400, "Bad request"));

        // Execute test and verify exception
        assertThrows(ClientException.class, () -> {
            baseRestUtils.genericPatch(url, headers, resourceToModify, TestResponse.class);
        });
    }

    @Test
    void testDeleteWithError() {
        // Prepare test data
        String url = "http://example.com/api";
        List<HttpHeader> headers = new ArrayList<>();
        String identifier = "123";

        // Setup mock behavior to throw exception
        when(mockRestTemplate.exchange(
                eq(url),
                eq(HttpMethod.DELETE),
                any(),
                eq(JsonNode.class),
                any(Map.class)
        )).thenThrow(new ClientException(404, "Resource not found"));

        // Execute test and verify exception
        assertThrows(ClientException.class, () -> {
            baseRestUtils.delete(url, headers, identifier);
        });
    }

    @Test
    void testGenericPost() throws ClientException {
        // Prepare test data
        String url = "http://example.com/api";
        List<HttpHeader> headers = new ArrayList<>();
        TestResponse resource = new TestResponse("test");

        // Mock response
        TestResponse response = new TestResponse("created");
        JsonNode responseNode = objectMapper.valueToTree(response);

        // Setup mock behavior
        when(mockRestTemplate.exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(),
                eq(JsonNode.class),
                any(Map.class)
        )).thenReturn(responseNode);

        // Execute test
        TestResponse result = baseRestUtils.genericPost(url, headers, resource, TestResponse.class);

        // Verify
        assertNotNull(result);
        assertEquals("created", result.getResult());
    }

    @Test
    void testGenericPostWithError() {
        // Prepare test data
        String url = "http://example.com/api";
        List<HttpHeader> headers = new ArrayList<>();
        TestResponse resource = new TestResponse("test");

        // Setup mock behavior to throw exception
        when(mockRestTemplate.exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(),
                eq(JsonNode.class),
                any(Map.class)
        )).thenThrow(new ClientException(400, "Bad request"));

        // Execute test and verify exception
        assertThrows(ClientException.class, () -> {
            baseRestUtils.genericPost(url, headers, resource, TestResponse.class);
        });
    }

    @Test
    void testGetPageWithError() {
        // Prepare test data
        String url = "http://example.com/api";
        List<HttpHeader> headers = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 10);
        TestFilter filters = new TestFilter("test");

        // Setup mock behavior to throw exception
        when(mockRestTemplate.exchange(
                any(String.class),
                eq(HttpMethod.GET),
                any(),
                eq(JsonNode.class),
                any(Map.class)
        )).thenThrow(new ClientException(500, "Internal server error"));

        // Execute test and verify exception
        assertThrows(ClientException.class, () -> {
            baseRestUtils.getPage(url, headers, pageable, filters, TestResponse.class);
        });
    }

    @Test
    void testGetWithError() {
        // Prepare test data
        String url = "http://example.com/api";
        List<HttpHeader> headers = new ArrayList<>();
        String identifier = "123";

        // Setup mock behavior to throw exception
        when(mockRestTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(JsonNode.class),
                any(Map.class)
        )).thenThrow(new ClientException(404, "Resource not found"));

        // Execute test and verify exception
        assertThrows(ClientException.class, () -> {
            baseRestUtils.get(url, headers, identifier, TestResponse.class);
        });
    }

    @Test
    void testCreateWithError() {
        // Prepare test data
        String url = "http://example.com/api";
        List<HttpHeader> headers = new ArrayList<>();
        TestResponse resourceToCreate = new TestResponse("test");

        // Setup mock behavior to throw exception
        when(mockRestTemplate.exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(),
                eq(JsonNode.class),
                any(Map.class)
        )).thenThrow(new ClientException(400, "Bad request"));

        // Execute test and verify exception
        assertThrows(ClientException.class, () -> {
            baseRestUtils.create(url, headers, resourceToCreate, TestResponse.class);
        });
    }

    @Test
    void testPutWithError() {
        // Prepare test data
        String url = "http://example.com/api";
        List<HttpHeader> headers = new ArrayList<>();
        String identifier = "123";
        TestResponse resourceToModify = new TestResponse("test");

        // Setup mock behavior to throw exception
        when(mockRestTemplate.exchange(
                eq(url),
                eq(HttpMethod.PUT),
                any(),
                eq(JsonNode.class),
                any(Map.class)
        )).thenThrow(new ClientException(404, "Resource not found"));

        // Execute test and verify exception
        assertThrows(ClientException.class, () -> {
            baseRestUtils.put(url, headers, identifier, resourceToModify, TestResponse.class);
        });
    }

    // Helper classes for testing
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

    private static class TestFilter {
        private String value;

        public TestFilter(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
} 