package org.opendatamesh.platform.up.metaservice.blindata.client.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.exceptions.OdmPlatformInternalServerException;
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
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility class for performing REST operations and mapping JSON responses to Java objects.
 * <p>
 * This class encapsulates various methods to execute HTTP requests (GET, POST, PUT, PATCH, DELETE)
 * using a {@link RestTemplate} and convert the JSON responses into Java objects using an {@link ObjectMapper}.
 * It also provides helper methods to support pagination and file download operations.
 * </p>
 */
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

    /**
     * Retrieves a paginated response from a REST API endpoint and maps it to a {@link Page} of the specified type.
     *
     * @param url         the base URL of the REST endpoint.
     * @param httpHeaders the HTTP headers to include in the request.
     * @param pageable    the pagination information to include in the request.
     * @param filters     the filters to apply to the request.
     * @param clazz       the target class to which the response should be mapped.
     * @param <R>         the type of elements in the returned page.
     * @param <F>         the type of the filters.
     * @return a {@link Page} of mapped objects of type {@code R}.
     * @throws ClientException                if an HTTP client error occurs.
     * @throws ClientResourceMappingException if JSON processing fails.
     */
    public <R, F> Page<R> getPage(String url, HttpHeaders httpHeaders, Pageable pageable, F filters, Class<R> clazz) throws ClientException, ClientResourceMappingException {
        try {
            UriComponents uriComponents = new UriComponents(url);
            if (filters != null) {
                buildUriComponentFromFilters(uriComponents, filters);
            }
            if (pageable != null) {
                buildUriComponentFromPageable(uriComponents, pageable);
            }

            ResponseEntity<ObjectNode> responseEntity = rest.exchange(
                    uriComponents.getBuilder().build().toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(httpHeaders),
                    ObjectNode.class,
                    uriComponents.getUriVariables()
            );

            JavaType type = objectMapper.getTypeFactory().constructParametricType(Page.class, clazz);
            return objectMapper.treeToValue(responseEntity.getBody(), type);
        } catch (HttpClientErrorException e) {
            throw new ClientException(e.getRawStatusCode(), e.getResponseBodyAsString());
        } catch (JsonProcessingException e) {
            throw new ClientResourceMappingException(e.getMessage(), e);
        }
    }

    /**
     * Executes a generic HTTP GET request with optional filters and maps the response to an object of the specified type.
     *
     * @param url         the base URL of the REST endpoint.
     * @param httpHeaders the HTTP headers to include in the request.
     * @param filters     the filters to apply to the request.
     * @param clazz       the target class to which the response should be mapped.
     * @param <R>         the type of the returned object.
     * @param <F>         the type of the filters.
     * @return an object of type {@code R} representing the response.
     * @throws ClientException                if an HTTP client error occurs.
     * @throws ClientResourceMappingException if JSON processing fails.
     */
    public <R, F> R genericGet(String url, HttpHeaders httpHeaders, F filters, Class<R> clazz) throws ClientException, ClientResourceMappingException {
        try {
            UriComponents uriComponents = new UriComponents(url);
            if (filters != null) {
                buildUriComponentFromFilters(uriComponents, filters);
            }

            ResponseEntity<ObjectNode> responseEntity = rest.exchange(
                    uriComponents.getBuilder().build().toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(httpHeaders),
                    ObjectNode.class,
                    uriComponents.getUriVariables()
            );

            return objectMapper.treeToValue(responseEntity.getBody(), clazz);
        } catch (HttpClientErrorException e) {
            throw new ClientException(e.getRawStatusCode(), e.getResponseBodyAsString());
        } catch (JsonProcessingException e) {
            throw new ClientResourceMappingException(e.getMessage(), e);
        }
    }

    /**
     * Retrieves a resource identified by a given identifier from the specified URL.
     *
     * @param url         the base URL of the REST endpoint.
     * @param httpHeaders the HTTP headers to include in the request.
     * @param identifier  the identifier of the resource.
     * @param clazz       the target class to which the response should be mapped.
     * @param <R>         the type of the returned object.
     * @param <ID>        the type of the identifier.
     * @return an object of type {@code R} representing the retrieved resource.
     * @throws ClientException                if an HTTP client error occurs.
     * @throws ClientResourceMappingException if JSON processing fails.
     */
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

    /**
     * Creates a new resource by sending a POST request to the specified URL.
     *
     * @param url              the base URL of the REST endpoint.
     * @param httpHeaders      the HTTP headers to include in the request.
     * @param resourceToCreate the resource object to be created.
     * @param clazz            the target class to which the response should be mapped.
     * @param <R>              the type of the resource.
     * @return the created resource mapped to an object of type {@code R}.
     * @throws ClientException                if an HTTP client error occurs.
     * @throws ClientResourceMappingException if JSON processing fails.
     */
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

    /**
     * Updates an existing resource by sending a PUT request to the specified URL.
     *
     * @param url              the base URL of the REST endpoint.
     * @param httpHeaders      the HTTP headers to include in the request.
     * @param identifier       the identifier of the resource to update.
     * @param resourceToModify the resource object containing updated data.
     * @param clazz            the target class to which the response should be mapped.
     * @param <R>              the type of the resource.
     * @param <ID>             the type of the identifier.
     * @return the updated resource mapped to an object of type {@code R}.
     * @throws ClientException                if an HTTP client error occurs.
     * @throws ClientResourceMappingException if JSON processing fails.
     */
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

    /**
     * Partially updates a resource by sending a PATCH request to the specified URL.
     *
     * @param url              the base URL of the REST endpoint.
     * @param httpHeaders      the HTTP headers to include in the request.
     * @param identifier       the identifier of the resource to update.
     * @param resourceToModify the resource object containing the partial updates.
     * @param clazz            the target class to which the response should be mapped.
     * @param <R>              the type of the resource.
     * @param <ID>             the type of the identifier.
     * @return the updated resource mapped to an object of type {@code R}.
     * @throws ClientException                if an HTTP client error occurs.
     * @throws ClientResourceMappingException if JSON processing fails.
     */
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

    /**
     * Deletes a resource identified by the given identifier by sending a DELETE request to the specified URL.
     *
     * @param url         the base URL of the REST endpoint.
     * @param httpHeaders the HTTP headers to include in the request.
     * @param identifier  the identifier of the resource to delete.
     * @param <ID>        the type of the identifier.
     * @throws ClientException if an HTTP client error occurs.
     */
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

    /**
     * Executes a generic HTTP POST request with the given resource and maps the response to an object of the specified type.
     *
     * @param url         the base URL of the REST endpoint.
     * @param httpHeaders the HTTP headers to include in the request.
     * @param resource    the resource object to send in the POST request.
     * @param clazz       the target class to which the response should be mapped.
     * @param <I>         the type of the resource sent in the request.
     * @param <O>         the type of the object returned.
     * @return an object of type {@code O} representing the response.
     * @throws ClientException                if an HTTP client error occurs.
     * @throws ClientResourceMappingException if JSON processing fails.
     */
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
     * @param maxElements    The maximum number of elements to retrieve. If this limit is reached, the process stops even if there are more pages available.
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

    /**
     * Adds pagination query parameters to the provided {@link UriComponents} based on the {@link Pageable} object.
     *
     * @param uriComponents the URI components object to which pagination parameters will be added.
     * @param pageable      the pageable object containing page number, size, and sort information.
     */
    private void buildUriComponentFromPageable(UriComponents uriComponents, Pageable pageable) {
        uriComponents.getBuilder().queryParam("page", "{page}");
        uriComponents.getUriVariables().put("page", pageable.getPageNumber());

        uriComponents.getBuilder().queryParam("size", "{size}");
        uriComponents.getUriVariables().put("size", pageable.getPageSize());

        StringBuilder sortBuilder = new StringBuilder();
        pageable.getSort().forEach(order -> {
            if (sortBuilder.length() > 0) {
                sortBuilder.append(",");
            }
            sortBuilder.append(order.getProperty()).append(",").append(order.getDirection());
        });
        uriComponents.getBuilder().queryParam("sort", "{sort}");
        uriComponents.getUriVariables().put("sort", sortBuilder.toString());
    }

    /**
     * Adds query parameters for filters to the provided {@link UriComponents} based on the fields of the filter object.
     * <p>
     * This method iterates over the declared fields of the filter object, making them accessible to extract their values,
     * and adds them as query parameters. For collections or arrays, it creates placeholders for each element.
     * </p>
     *
     * @param uriComponents the URI components object to which filter parameters will be added.
     * @param filters       the filter object containing the fields to be used as query parameters.
     * @param <F>           the type of the filter object.
     * @throws InternalServerException if reflection fails or an error occurs during parameter construction.
     */
    private <F> void buildUriComponentFromFilters(UriComponents uriComponents, F filters) {
        try {
            for (Field f : filters.getClass().getDeclaredFields()) {
                boolean accessible = f.isAccessible();
                f.setAccessible(true);
                Object value = f.get(filters);
                String paramName = f.getName();

                if (value instanceof Collection) {
                    Collection<?> collection = (Collection<?>) value;
                    List<String> placeholders = new ArrayList<>();
                    int index = 0;
                    for (Object item : collection) {
                        String placeholder = paramName + index;
                        placeholders.add("{" + placeholder + "}");
                        uriComponents.getUriVariables().put(placeholder, item);
                        index++;
                    }
                    uriComponents.getBuilder().queryParam(paramName, placeholders.toArray());
                } else if (value != null && value.getClass().isArray()) {
                    int length = Array.getLength(value);
                    List<String> placeholders = new ArrayList<>();
                    for (int i = 0; i < length; i++) {
                        Object item = Array.get(value, i);
                        String placeholder = paramName + i;
                        placeholders.add("{" + placeholder + "}");
                        uriComponents.getUriVariables().put(placeholder, item);
                    }
                    uriComponents.getBuilder().queryParam(paramName, placeholders.toArray());
                } else {
                    uriComponents.getBuilder().queryParam(paramName, "{" + paramName + "}");
                    uriComponents.getUriVariables().put(paramName, value);
                }
                f.setAccessible(accessible);
            }
        } catch (Exception e) {
            throw new OdmPlatformInternalServerException(e);
        }
    }

    /**
     * Utility class for building URI components with query parameters and URI variables.
     */
    private static class UriComponents {
        private final UriComponentsBuilder builder;
        private final Map<String, Object> uriVariables;

        public UriComponents(String baseUrl) {
            this.uriVariables = new HashMap<>();
            this.builder = UriComponentsBuilder.fromHttpUrl(baseUrl);
        }

        public UriComponentsBuilder getBuilder() {
            return builder;
        }

        public Map<String, Object> getUriVariables() {
            return uriVariables;
        }
    }

}
