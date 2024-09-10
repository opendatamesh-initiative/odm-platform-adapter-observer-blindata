package org.opendatamesh.platform.up.metaservice.blindata.client.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Field;

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
