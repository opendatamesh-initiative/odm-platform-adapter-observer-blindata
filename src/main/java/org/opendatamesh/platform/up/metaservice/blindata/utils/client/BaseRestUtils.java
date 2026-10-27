package org.opendatamesh.platform.up.metaservice.blindata.utils.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.opendatamesh.platform.up.metaservice.blindata.exceptions.InternalException;
import org.opendatamesh.platform.up.metaservice.blindata.exceptions.client.ClientException;
import org.opendatamesh.platform.up.metaservice.blindata.exceptions.client.ClientResourceMappingException;
import org.opendatamesh.platform.up.metaservice.blindata.utils.client.http.HttpEntity;
import org.opendatamesh.platform.up.metaservice.blindata.utils.client.http.HttpHeader;
import org.opendatamesh.platform.up.metaservice.blindata.utils.client.http.HttpMethod;
import org.opendatamesh.platform.up.metaservice.blindata.utils.client.jackson.PageUtility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class BaseRestUtils implements RestUtils {

    protected final RestUtilsTemplate rest;
    protected final ObjectMapper objectMapper;

    public BaseRestUtils(RestUtilsTemplate restUtilsTemplate) {
        this.rest = restUtilsTemplate;
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule simpleModule = new SimpleModule()
                .addAbstractTypeMapping(Page.class, PageUtility.class);
        objectMapper.registerModule(simpleModule);
    }

    @Override
    public <R, F> Page<R> getPage(String url, List<HttpHeader> httpHeaders, Pageable pageable, F filters, Class<R> clazz)
            throws ClientException, ClientResourceMappingException {
        try {
            UriComponents uriComponents = new UriComponents(url);
            if (filters != null) {
                buildUriComponentFromFilters(uriComponents, filters);
            }
            if (pageable != null) {
                buildUriComponentFromPageable(uriComponents, pageable);
            }

            JsonNode response = rest.exchange(
                    uriComponents.getBuilder().build().toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(httpHeaders),
                    JsonNode.class,
                    uriComponents.getUriVariables()
            );

            JavaType type = objectMapper.getTypeFactory().constructParametricType(Page.class, clazz);
            return objectMapper.treeToValue(response, type);
        } catch (JsonProcessingException e) {
            throw new ClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public <R, F> R genericGet(String url, List<HttpHeader> httpHeaders, F filters, Class<R> clazz)
            throws ClientException, ClientResourceMappingException {
        try {
            UriComponents uriComponents = new UriComponents(url);
            if (filters != null) {
                buildUriComponentFromFilters(uriComponents, filters);
            }

            JsonNode response = rest.exchange(
                    uriComponents.getBuilder().build().toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(httpHeaders),
                    JsonNode.class,
                    uriComponents.getUriVariables()
            );

            return objectMapper.treeToValue(response, clazz);
        } catch (JsonProcessingException e) {
            throw new ClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public <R, ID> R get(String url, List<HttpHeader> httpHeaders, ID identifier, Class<R> clazz)
            throws ClientException, ClientResourceMappingException {
        try {
            JsonNode response = rest.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(httpHeaders),
                    JsonNode.class,
                    idVariables(identifier)
            );
            return objectMapper.treeToValue(response, clazz);
        } catch (JsonProcessingException e) {
            throw new ClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public <R> R create(String url, List<HttpHeader> httpHeaders, R resourceToCreate, Class<R> clazz)
            throws ClientException, ClientResourceMappingException {
        try {
            JsonNode response = rest.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(resourceToCreate, httpHeaders),
                    JsonNode.class,
                    new HashMap<>()
            );
            return objectMapper.treeToValue(response, clazz);
        } catch (JsonProcessingException e) {
            throw new ClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public <R, ID> R put(String url, List<HttpHeader> httpHeaders, ID identifier, R resourceToModify, Class<R> clazz)
            throws ClientException, ClientResourceMappingException {
        try {
            JsonNode response = rest.exchange(
                    url,
                    HttpMethod.PUT,
                    new HttpEntity<>(resourceToModify, httpHeaders),
                    JsonNode.class,
                    idVariables(identifier)
            );
            return objectMapper.treeToValue(response, clazz);
        } catch (JsonProcessingException e) {
            throw new ClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public <R, ID> R patch(String url, List<HttpHeader> httpHeaders, ID identifier, R resourceToModify, Class<R> clazz)
            throws ClientException, ClientResourceMappingException {
        try {
            JsonNode response = rest.exchange(
                    url,
                    HttpMethod.PATCH,
                    new HttpEntity<>(resourceToModify, httpHeaders),
                    JsonNode.class,
                    idVariables(identifier)
            );
            return objectMapper.treeToValue(response, clazz);
        } catch (JsonProcessingException e) {
            throw new ClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public <I, O> O genericPatch(String url, List<HttpHeader> httpHeaders, I resourceToModify, Class<O> clazz)
            throws ClientException, ClientResourceMappingException {
        try {
            JsonNode response = rest.exchange(
                    url,
                    HttpMethod.PATCH,
                    new HttpEntity<>(resourceToModify, httpHeaders),
                    JsonNode.class,
                    new HashMap<>()
            );
            return objectMapper.treeToValue(response, clazz);
        } catch (JsonProcessingException e) {
            throw new ClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public <ID> void delete(String url, List<HttpHeader> httpHeaders, ID identifier) throws ClientException {
        rest.exchange(
                url,
                HttpMethod.DELETE,
                new HttpEntity<>(httpHeaders),
                JsonNode.class,
                idVariables(identifier)
        );
    }

    @Override
    public <I, O> O genericPost(String url, List<HttpHeader> httpHeaders, I resource, Class<O> clazz)
            throws ClientException, ClientResourceMappingException {
        try {
            JsonNode response = rest.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(resource, httpHeaders),
                    JsonNode.class,
                    new HashMap<>()
            );
            return objectMapper.treeToValue(response, clazz);
        } catch (JsonProcessingException e) {
            throw new ClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public File download(String url, List<HttpHeader> httpHeaders, Object resource, File storeLocation)
            throws ClientException, ClientResourceMappingException {
        return rest.download(url, httpHeaders, resource, storeLocation);
    }

    private Map<String, Object> idVariables(Object identifier) {
        Map<String, Object> uriVariables = new HashMap<>();
        if (identifier != null) {
            uriVariables.put("id", identifier);
        }
        return uriVariables;
    }

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

    private <F> void buildUriComponentFromFilters(UriComponents uriComponents, F filters) {
        try {
            for (Field field : filters.getClass().getDeclaredFields()) {
                boolean accessible = field.canAccess(filters);
                field.setAccessible(true);
                Object value = field.get(filters);
                String paramName = field.getName();

                if (value instanceof Collection<?> collection) {
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
                field.setAccessible(accessible);
            }
        } catch (Exception e) {
            throw new InternalException(e.getMessage(), e);
        }
    }

    private static class UriComponents {
        private final UriComponentsBuilder builder;
        private final Map<String, Object> uriVariables;

        public UriComponents(String baseUrl) {
            this.uriVariables = new HashMap<>();
            this.builder = UriComponentsBuilder.fromUriString(baseUrl);
        }

        public UriComponentsBuilder getBuilder() {
            return builder;
        }

        public Map<String, Object> getUriVariables() {
            return uriVariables;
        }
    }
}
