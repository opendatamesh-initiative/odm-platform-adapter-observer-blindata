package org.opendatamesh.platform.up.metaservice.blindata.client.utils;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.exceptions.ClientException;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.exceptions.ClientResourceMappingException;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.http.HttpEntity;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.http.HttpHeader;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.http.HttpMethod;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.jackson.PageUtility;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.exceptions.OdmPlatformInternalServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class AsyncRestUtilsTemplate implements RestUtilsTemplate {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final int MAX_POLL_ATTEMPTS = 100;
    private final long INITIAL_POLL_WAIT_MILLIS = 500;
    private final long MAX_POLL_WAIT_MILLIS = 60000;

    private final RestUtilsTemplate wrappedInstance;
    private final String asyncEndpointRequest;
    private final String asyncEndpointPoll;
    private final ObjectMapper objectMapper;

    public AsyncRestUtilsTemplate(RestUtilsTemplate restTemplate, String asyncEndpointRequest, String asyncEndpointPoll) {
        this.wrappedInstance = restTemplate;
        this.asyncEndpointRequest = asyncEndpointRequest;
        this.asyncEndpointPoll = asyncEndpointPoll;
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule simpleModule = new SimpleModule()
                .addAbstractTypeMapping(Page.class, PageUtility.class);
        objectMapper.registerModule(simpleModule);
    }


    private AsyncRestTask pollResult(String url, List<HttpHeader> requestHeaders, AsyncRestTask task) throws ClientException {
        int attempt = 0;
        long waitTime = INITIAL_POLL_WAIT_MILLIS;

        Map<String, String> uriVariables = Map.of("taskId", task.getId());

        logger.info("Polling async task {} started", task.getId());
        while (attempt < MAX_POLL_ATTEMPTS) {
            AsyncRestTask pollResponse = wrappedInstance.exchange(
                    buildPollUrl(url) + "/{taskId}",
                    HttpMethod.GET,
                    new HttpEntity<>(requestHeaders),
                    AsyncRestTask.class,
                    uriVariables
            );

            if (pollResponse.getStatus() == null) {
                throw new IllegalStateException("Async Task status should never be null: " + pollResponse);
            }

            switch (pollResponse.getStatus()) {
                case IN_PROGRESS:
                    try {
                        Thread.sleep(waitTime);
                        waitTime = Math.min(waitTime * 2, MAX_POLL_WAIT_MILLIS);
                        logger.info("Async task {} is in progress. Waiting for {} milliseconds before next attempt.", task.getId(), waitTime);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new OdmPlatformInternalServerException(e);
                    }
                    break;
                case DONE:
                    logger.info("Async task {} completed", task.getId());
                    return pollResponse;
                case FAILED:
                    String responseBody = pollResponse.getResponseBody() == null ? "Empty Body" : new String(pollResponse.getResponseBody(), StandardCharsets.UTF_8);
                    throw new ClientException(500, "Async task failed: " + responseBody);
                case NOT_FOUND:
                    throw new ClientException(404, "Async task not found: " + task.getId());
                default:
                    throw new ClientException(500, "Unknown async task status: " + pollResponse.getStatus());
            }

            attempt++;
        }
        throw new ClientException(500, "Async task polling exceeded maximum attempts");
    }

    @Override
    public <T> T exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) throws ClientException {
        AsyncRestTask asyncRequest = wrappedInstance.exchange(
                buildRequestUrl(url),
                method,
                requestEntity,
                AsyncRestTask.class,
                uriVariables
        );

        AsyncRestTask finalResult = pollResult(
                url,
                requestEntity.getHeaders(),
                asyncRequest
        );
        try {
            return objectMapper.readValue(finalResult.getResponseBody(), responseType);
        } catch (IOException e) {
            throw new ClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public File download(String url, List<HttpHeader> httpHeaders, Object resource, File storeLocation) throws ClientException {
        AsyncRestTask asyncRequest = wrappedInstance.exchange(
                buildRequestUrl(url),
                HttpMethod.POST,
                new HttpEntity<>(resource, httpHeaders),
                AsyncRestTask.class,
                new HashMap<>()
        );

        AsyncRestTask finalResult = pollResult(
                url,
                httpHeaders,
                asyncRequest
        );
        if (finalResult.getResponseBody() == null) {
            throw new ClientException(500, "Error downloading file: missing body from response");
        }
        try (FileOutputStream fos = new FileOutputStream(storeLocation)) {
            StreamUtils.copy(finalResult.getResponseBody(), fos);
            return storeLocation;
        } catch (IOException e) {
            throw new ClientResourceMappingException("Error downloading file: " + e.getMessage(), e);
        }
    }

    private String buildRequestUrl(String url) {
        // Find the first occurrence of '/' after the protocol
        int protocolEnd = url.indexOf("://");
        if (protocolEnd == -1) {
            throw new ClientException(400, "Invalid URL format: missing protocol");
        }

        // Find the first '/' after the protocol and host
        int pathStart = url.indexOf('/', protocolEnd + 3);
        if (pathStart == -1) {
            // If no path, just append the async endpoint
            return url + asyncEndpointRequest;
        }

        // Split into base URL and path
        String baseUrl = url.substring(0, pathStart);
        String path = url.substring(pathStart);

        // If the path already contains the async endpoint, return as is
        if (path.startsWith(asyncEndpointRequest)) {
            return url;
        }

        // Otherwise, prepend the async endpoint to the path
        return baseUrl + asyncEndpointRequest + path;
    }

    private String buildPollUrl(String url) {
        // Find the first occurrence of '/' after the protocol
        int protocolEnd = url.indexOf("://");
        if (protocolEnd == -1) {
            throw new ClientException(400, "Invalid URL format: missing protocol");
        }

        // Find the first '/' after the protocol and host
        int pathStart = url.indexOf('/', protocolEnd + 3);
        if (pathStart == -1) {
            // If no path, just append the async endpoint
            return url + asyncEndpointPoll;
        }

        // Return base URL + async endpoint
        return url.substring(0, pathStart) + asyncEndpointPoll;
    }
}
