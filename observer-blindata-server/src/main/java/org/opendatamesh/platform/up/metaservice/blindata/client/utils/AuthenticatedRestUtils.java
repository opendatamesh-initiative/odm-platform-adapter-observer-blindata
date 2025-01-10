package org.opendatamesh.platform.up.metaservice.blindata.client.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.function.Function;

public class AuthenticatedRestUtils extends RestUtils {

    private final Function<HttpHeaders, HttpHeaders> getAuthenticatedHeaders;

    public AuthenticatedRestUtils(RestTemplate restTemplate, Function<HttpHeaders, HttpHeaders> getAuthenticatedHeaders) {
        super(restTemplate);
        this.getAuthenticatedHeaders = getAuthenticatedHeaders;
        if (getAuthenticatedHeaders == null){
            throw new IllegalArgumentException("getAuthenticatedHeaders must not be null");
        }
    }

    @Override
    public <R, F> Page<R> getPage(String url, HttpHeaders httpHeaders, Pageable pageable, F filters, Class<R> clazz) throws ClientException, ClientResourceMappingException {
        return super.getPage(url, getAuthenticatedHeaders.apply(httpHeaders), pageable, filters, clazz);
    }

    @Override
    public <R, ID> R get(String url, HttpHeaders httpHeaders, ID identifier, Class<R> clazz) throws ClientException, ClientResourceMappingException {
        return super.get(url, getAuthenticatedHeaders.apply(httpHeaders), identifier, clazz);
    }

    @Override
    public <R> R create(String url, HttpHeaders httpHeaders, R resourceToCreate, Class<R> clazz) throws ClientException, ClientResourceMappingException {
        return super.create(url, getAuthenticatedHeaders.apply(httpHeaders), resourceToCreate, clazz);
    }

    @Override
    public <R, ID> R put(String url, HttpHeaders httpHeaders, ID identifier, R resourceToModify, Class<R> clazz) throws ClientException, ClientResourceMappingException {
        return super.put(url, getAuthenticatedHeaders.apply(httpHeaders), identifier, resourceToModify, clazz);
    }

    @Override
    public <R, ID> R patch(String url, HttpHeaders httpHeaders, ID identifier, R resourceToModify, Class<R> clazz) throws ClientException, ClientResourceMappingException {
        return super.patch(url, getAuthenticatedHeaders.apply(httpHeaders), identifier, resourceToModify, clazz);
    }

    @Override
    public <ID> void delete(String url, HttpHeaders httpHeaders, ID identifier) throws ClientException {
        super.delete(url, getAuthenticatedHeaders.apply(httpHeaders), identifier);
    }

    @Override
    public <I, O> O genericPost(String url, HttpHeaders httpHeaders, I resource, Class<O> clazz) throws ClientException, ClientResourceMappingException {
        return super.genericPost(url, getAuthenticatedHeaders.apply(httpHeaders), resource, clazz);
    }

    @Override
    public File download(String url, HttpHeaders httpHeaders, Object resource, File storeLocation) throws ClientException, ClientResourceMappingException {
        return super.download(url, getAuthenticatedHeaders.apply(httpHeaders), resource, storeLocation);
    }
}
