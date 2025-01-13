package org.opendatamesh.platform.up.metaservice.blindata.client.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class RestUtils {

    private final RestTemplate rest;
    private final ObjectMapper objectMapper;

    public RestUtils(RestTemplate restTemplate) {
        this.rest = restTemplate;
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule simpleModule = new SimpleModule()
                .addAbstractTypeMapping(Page.class, PageUtility.class);
        objectMapper.registerModule(simpleModule);
    }

    public <R, F> Page<R> getPage(String url, HttpHeaders httpHeaders, Pageable pageable, F filters, Class<R> clazz) throws ClientException, ClientResourceMappingException {
        try {
            if (pageable != null) {
                url = appendQueryStringFromPageable(url, pageable);
            }
            if (filters != null) {
                url = appendQueryStringFromFilters(url, filters);
            }

            ResponseEntity<ObjectNode> responseEntity = rest.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(httpHeaders),
                    ObjectNode.class
            );

            JavaType type = objectMapper.getTypeFactory().constructParametricType(Page.class, clazz);
            return objectMapper.treeToValue(responseEntity.getBody(), type);
        } catch (HttpClientErrorException e) {
            throw new ClientException(e.getRawStatusCode(), e.getResponseBodyAsString());
        } catch (JsonProcessingException e) {
            throw new ClientResourceMappingException(e.getMessage(), e);
        }
    }

    public <R, ID> R get(String url, HttpHeaders httpHeaders, ID identifier, Class<R> clazz) throws ClientException, ClientResourceMappingException {
        try {
            ResponseEntity<ObjectNode> responseEntity = rest.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(httpHeaders),
                    ObjectNode.class,
                    identifier
            );

            return objectMapper.treeToValue(responseEntity.getBody(), clazz);
        } catch (HttpClientErrorException e) {
            throw new ClientException(e.getRawStatusCode(), e.getResponseBodyAsString());
        } catch (JsonProcessingException e) {
            throw new ClientResourceMappingException(e.getMessage(), e);
        }
    }

    public <R> R create(String url, HttpHeaders httpHeaders, R resourceToCreate, Class<R> clazz) throws ClientException, ClientResourceMappingException {
        try {
            ResponseEntity<ObjectNode> responseEntity = rest.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(resourceToCreate, httpHeaders),
                    ObjectNode.class
            );
            return objectMapper.treeToValue(responseEntity.getBody(), clazz);
        } catch (HttpClientErrorException e) {
            throw new ClientException(e.getRawStatusCode(), e.getResponseBodyAsString());
        } catch (JsonProcessingException e) {
            throw new ClientResourceMappingException(e.getMessage(), e);
        }
    }

    public <R, ID> R put(String url, HttpHeaders httpHeaders, ID identifier, R resourceToModify, Class<R> clazz) throws ClientException, ClientResourceMappingException {
        try {
            ResponseEntity<ObjectNode> responseEntity = rest.exchange(
                    url,
                    HttpMethod.PUT,
                    new HttpEntity<>(resourceToModify, httpHeaders),
                    ObjectNode.class,
                    identifier
            );

            return objectMapper.treeToValue(responseEntity.getBody(), clazz);
        } catch (HttpClientErrorException e) {
            throw new ClientException(e.getRawStatusCode(), e.getResponseBodyAsString());
        } catch (JsonProcessingException e) {
            throw new ClientResourceMappingException(e.getMessage(), e);
        }
    }

    public <R, ID> R patch(String url, HttpHeaders httpHeaders, ID identifier, R resourceToModify, Class<R> clazz) throws ClientException, ClientResourceMappingException {
        try {
            ResponseEntity<ObjectNode> responseEntity = rest.exchange(
                    url,
                    HttpMethod.PATCH,
                    new HttpEntity<>(resourceToModify, httpHeaders),
                    ObjectNode.class,
                    identifier
            );

            return objectMapper.treeToValue(responseEntity.getBody(), clazz);
        } catch (HttpClientErrorException e) {
            throw new ClientException(e.getRawStatusCode(), e.getResponseBodyAsString());
        } catch (JsonProcessingException e) {
            throw new ClientResourceMappingException(e.getMessage(), e);
        }
    }

    public <ID> void delete(String url, HttpHeaders httpHeaders, ID identifier) throws ClientException {
        try {
            rest.exchange(
                    url,
                    HttpMethod.DELETE,
                    new HttpEntity<>(httpHeaders),
                    ObjectNode.class,
                    identifier
            );
        } catch (HttpClientErrorException e) {
            throw new ClientException(e.getRawStatusCode(), e.getResponseBodyAsString());
        }
    }

    public <I, O> O genericPost(String url, HttpHeaders httpHeaders, I resource, Class<O> clazz) throws ClientException, ClientResourceMappingException {
        try {
            ResponseEntity<ObjectNode> responseEntity = rest.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(resource, httpHeaders),
                    ObjectNode.class
            );

            return objectMapper.treeToValue(responseEntity.getBody(), clazz);
        } catch (HttpClientErrorException e) {
            throw new ClientException(e.getRawStatusCode(), e.getResponseBodyAsString());
        } catch (JsonProcessingException e) {
            throw new ClientResourceMappingException(e.getMessage(), e);
        }
    }

    /**
     * Downloads a file from a REST API endpoint and saves it to the specified location.
     *
     * @param url           The URL of the API endpoint to download the file from.
     * @param httpHeaders   The HTTP headers to include in the request (e.g., authorization headers). Can be {@code null}.
     * @param resource      The request body to send with the download request. Can be {@code null} if no request body is needed (e.g., for simple GET downloads).
     * @param storeLocation The File object representing the location where the downloaded file should be saved.
     * @return The File object representing the saved file.
     * @throws ClientException                If a client-side error occurs during the API call (e.g., HTTP error codes).
     * @throws ClientResourceMappingException If an error occurs during file processing (e.g., I/O exceptions).
     * @throws NullPointerException           if storeLocation is null.
     */
    public File download(String url, HttpHeaders httpHeaders, Object resource, File storeLocation)
            throws ClientException, ClientResourceMappingException {
        try {
            return rest.execute(url, HttpMethod.POST, request -> {
                if (httpHeaders != null) {
                    request.getHeaders().addAll(httpHeaders);
                }
                if (resource != null) {
                    objectMapper.writeValue(request.getBody(), resource);
                }
            }, response -> {
                try (FileOutputStream fos = new FileOutputStream(storeLocation)) {
                    StreamUtils.copy(response.getBody(), fos);
                    return storeLocation;
                }
            });
        } catch (RestClientResponseException e) {
            throw new ClientException(e.getRawStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new ClientResourceMappingException("Error downloading file: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves data from a REST API endpoint in pages and processes each page using the provided consumer.
     * This method handles pagination automatically, retrieving data in batches until all data is processed or the maximum number of elements is reached.
     *
     * @param <T>            The type of the data being retrieved.
     * @param retrieveMethod A function that takes a Pageable object and returns a Page of data. This function should encapsulate the logic for retrieving data from the API.
     * @param processMethod  A consumer that accepts a List of data and performs the desired processing on it. This is called for each retrieved page.
     * @param batchSize      The number of elements to retrieve in each page.
     * @param maxElements   The maximum number of elements to retrieve. If this limit is reached, the process stops even if there are more pages available.
     * @implNote This method uses a do-while loop to iterate through the pages. It is important that the {@code retrieveMethod} function correctly handles the {@code Pageable} object to ensure proper pagination.
     */
    public static <T> void retrieveAndProcessPageable(
            Function<Pageable, Page<T>> retrieveMethod,
            Consumer<List<T>> processMethod,
            int batchSize, int maxElements
    ) {
        processPageable(pageable -> {
            Page<T> page = retrieveMethod.apply(pageable);
            processMethod.accept(page.toList());
            return page;
        }, batchSize, maxElements);
    }

    private static <T> void processPageable(Function<Pageable, Page<T>> operation, int batchSize, int maxElements) {
        Pageable pageRequest = PageRequest.of(0, batchSize);
        Page<T> page;
        do {
            page = operation.apply(pageRequest);
            pageRequest = pageRequest.next();
        } while (page.hasNext() && limitNotReachedYet(maxElements, batchSize, page.getNumber()));
    }

    private static boolean limitNotReachedYet(int maxElements, int batchSize, int pageNumber) {
        return (batchSize * pageNumber) <= maxElements;
    }
    
    private String appendQueryStringFromPageable(String url, Pageable pageable) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize());
        StringBuilder sb = new StringBuilder();
        pageable.getSort().forEach(order -> sb.append(order.getProperty()).append(",").append(order.getDirection()));
        builder.queryParam("sort", sb.toString());
        return builder.build().toUriString();
    }

    private <F> String appendQueryStringFromFilters(String urlString, F filters) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlString);
        try {
            for (Field f : filters.getClass().getDeclaredFields()) {
                boolean isAccessible = f.isAccessible();
                f.setAccessible(true);
                builder.queryParam(f.getName(), f.get(filters));
                f.setAccessible(isAccessible);
            }
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
        return builder.build().toUriString();
    }
}
