package org.opendatamesh.platform.up.metaservice.blindata.client.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.exceptions.ClientException;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.http.HttpEntity;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.http.HttpHeader;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.http.HttpMethod;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsyncRestUtilsTemplateTest {

    @Mock
    private RestUtilsTemplate mockRestTemplate;

    private AsyncRestUtilsTemplate asyncRestUtilsTemplate;
    private static final String ASYNC_ENDPOINT_REQUEST = "/async/request";
    private static final String ASYNC_ENDPOINT_POLL = "/async/poll";

    @BeforeEach
    void setUp() {
        asyncRestUtilsTemplate = new AsyncRestUtilsTemplate(mockRestTemplate, ASYNC_ENDPOINT_REQUEST, ASYNC_ENDPOINT_POLL);
    }

    @Test
    void testExchangeSuccess() throws ClientException {
        // Prepare test data
        String url = "http://example.com/api";
        HttpMethod method = HttpMethod.GET;
        List<HttpHeader> headers = new ArrayList<>();
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        Map<String, ?> uriVariables = new HashMap<>();

        // Mock async request response
        AsyncRestTask asyncRequest = new AsyncRestTask();
        asyncRequest.setId("task-123");
        asyncRequest.setStatus(AsyncRestTask.Status.IN_PROGRESS);

        // Mock final result
        AsyncRestTask finalResult = new AsyncRestTask();
        finalResult.setId("task-123");
        finalResult.setStatus(AsyncRestTask.Status.DONE);
        finalResult.setResponseBody("{\"result\":\"success\"}".getBytes());

        // Setup mock behavior
        when(mockRestTemplate.exchange(
                any(String.class),
                eq(method),
                any(HttpEntity.class),
                eq(AsyncRestTask.class),
                eq(uriVariables)
        )).thenReturn(asyncRequest);

        when(mockRestTemplate.exchange(
                any(String.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(AsyncRestTask.class),
                any(Map.class)
        )).thenReturn(finalResult);

        // Execute test
        TestResponse response = asyncRestUtilsTemplate.exchange(url, method, requestEntity, TestResponse.class, uriVariables);

        // Verify
        assertNotNull(response);
        assertEquals("success", response.getResult());
    }

    @Test
    void testExchangeWithError() {
        // Prepare test data
        String url = "http://example.com/api";
        HttpMethod method = HttpMethod.GET;
        List<HttpHeader> headers = new ArrayList<>();
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        Map<String, ?> uriVariables = new HashMap<>();

        // Mock the initial async request
        AsyncRestTask initialTask = new AsyncRestTask();
        initialTask.setId("task-1");
        initialTask.setStatus(AsyncRestTask.Status.IN_PROGRESS);
        initialTask.setResponseBody(null);

        // Mock the poll response: first IN_PROGRESS, then FAILED
        AsyncRestTask pollInProgress = new AsyncRestTask();
        pollInProgress.setId("task-1");
        pollInProgress.setStatus(AsyncRestTask.Status.IN_PROGRESS);
        pollInProgress.setResponseBody(null);

        AsyncRestTask pollFailed = new AsyncRestTask();
        pollFailed.setId("task-1");
        pollFailed.setStatus(AsyncRestTask.Status.FAILED);
        pollFailed.setResponseBody("failure reason".getBytes());

        // Setup mock behavior
        when(mockRestTemplate.exchange(
                any(String.class),
                eq(method),
                any(),
                eq(AsyncRestTask.class),
                any(Map.class)
        )).thenReturn(initialTask);

        when(mockRestTemplate.exchange(
                contains("/async/poll"),
                eq(HttpMethod.GET),
                any(),
                eq(AsyncRestTask.class),
                any(Map.class)
        )).thenReturn(pollInProgress).thenReturn(pollFailed);

        // Execute test and verify exception
        assertThrows(ClientException.class, () -> {
            asyncRestUtilsTemplate.exchange(url, method, requestEntity, TestResponse.class, uriVariables);
        });
    }


    @Test
    void testExchangeWithRetry() throws ClientException {
        // Prepare test data
        String url = "http://example.com/api";
        HttpMethod method = HttpMethod.GET;
        List<HttpHeader> headers = new ArrayList<>();
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        Map<String, ?> uriVariables = new HashMap<>();

        // Mock the initial async request
        AsyncRestTask initialTask = new AsyncRestTask();
        initialTask.setId("task-1");
        initialTask.setStatus(AsyncRestTask.Status.IN_PROGRESS);
        initialTask.setResponseBody(null);

        // Mock the poll responses: first IN_PROGRESS, then DONE
        AsyncRestTask pollInProgress = new AsyncRestTask();
        pollInProgress.setId("task-1");
        pollInProgress.setStatus(AsyncRestTask.Status.IN_PROGRESS);
        pollInProgress.setResponseBody(null);

        AsyncRestTask pollDone = new AsyncRestTask();
        pollDone.setId("task-1");
        pollDone.setStatus(AsyncRestTask.Status.DONE);
        pollDone.setResponseBody("{\"result\":\"success\"}".getBytes());

        // Setup mock behavior
        when(mockRestTemplate.exchange(
                any(String.class),
                eq(method),
                any(),
                eq(AsyncRestTask.class),
                any(Map.class)
        )).thenReturn(initialTask);

        when(mockRestTemplate.exchange(
                contains("/async/poll"),
                eq(HttpMethod.GET),
                any(),
                eq(AsyncRestTask.class),
                any(Map.class)
        )).thenReturn(pollInProgress).thenReturn(pollDone);

        // Execute test
        TestResponse response = asyncRestUtilsTemplate.exchange(url, method, requestEntity, TestResponse.class, uriVariables);

        // Verify
        assertNotNull(response);
        assertEquals("success", response.getResult());
    }

    @Test
    void testDownloadSuccess() throws ClientException {
        // Prepare test data
        String url = "http://example.com/download";
        List<HttpHeader> headers = new ArrayList<>();
        Object resource = new Object();
        File storeLocation = new File("test.txt");

        // Mock async request response
        AsyncRestTask asyncRequest = new AsyncRestTask();
        asyncRequest.setId("task-123");
        asyncRequest.setStatus(AsyncRestTask.Status.IN_PROGRESS);

        // Mock final result
        AsyncRestTask finalResult = new AsyncRestTask();
        finalResult.setId("task-123");
        finalResult.setStatus(AsyncRestTask.Status.DONE);
        finalResult.setResponseBody("test content".getBytes());

        // Setup mock behavior
        when(mockRestTemplate.exchange(
                any(String.class),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(AsyncRestTask.class),
                any(Map.class)
        )).thenReturn(asyncRequest);

        when(mockRestTemplate.exchange(
                any(String.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(AsyncRestTask.class),
                any(Map.class)
        )).thenReturn(finalResult);

        // Execute test
        File result = asyncRestUtilsTemplate.download(url, headers, resource, storeLocation);

        // Verify
        assertNotNull(result);
        assertTrue(result.exists());
        assertEquals(storeLocation, result);
    }


    @Test
    void testDownloadWithRetry() throws ClientException {
        // Prepare test data
        String url = "http://example.com/download";
        List<HttpHeader> headers = new ArrayList<>();
        Object resource = new Object();
        File storeLocation = new File("test.txt");

        // Mock async request response
        AsyncRestTask asyncRequest = new AsyncRestTask();
        asyncRequest.setId("task-123");
        asyncRequest.setStatus(AsyncRestTask.Status.IN_PROGRESS);

        // Mock poll responses: first IN_PROGRESS, then DONE
        AsyncRestTask pollInProgress = new AsyncRestTask();
        pollInProgress.setId("task-123");
        pollInProgress.setStatus(AsyncRestTask.Status.IN_PROGRESS);
        pollInProgress.setResponseBody(null);

        AsyncRestTask pollDone = new AsyncRestTask();
        pollDone.setId("task-123");
        pollDone.setStatus(AsyncRestTask.Status.DONE);
        pollDone.setResponseBody("test content".getBytes());

        // Setup mock behavior
        when(mockRestTemplate.exchange(
                any(String.class),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(AsyncRestTask.class),
                any(Map.class)
        )).thenReturn(asyncRequest);

        when(mockRestTemplate.exchange(
                any(String.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(AsyncRestTask.class),
                any(Map.class)
        )).thenReturn(pollInProgress).thenReturn(pollDone);

        // Execute test
        File result = asyncRestUtilsTemplate.download(url, headers, resource, storeLocation);

        // Verify
        assertNotNull(result);
        assertTrue(result.exists());
        assertEquals(storeLocation, result);
    }

    // Helper class for testing
    private static class TestResponse {
        private String result;

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }
} 