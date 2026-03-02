package org.opendatamesh.platform.up.metaservice.blindata.client.utils;

import org.opendatamesh.platform.up.metaservice.blindata.client.utils.exceptions.ClientException;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.exceptions.ClientResourceMappingException;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.http.HttpHeader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.List;

public interface RestUtils {
    <R, F> Page<R> getPage(String url, List<HttpHeader> httpHeaders, Pageable pageable, F filters, Class<R> clazz) throws ClientException, ClientResourceMappingException;

    <R, F> R genericGet(String url, List<HttpHeader> httpHeaders, F filters, Class<R> clazz) throws ClientException, ClientResourceMappingException;

    <R, ID> R get(String url, List<HttpHeader> httpHeaders, ID identifier, Class<R> clazz) throws ClientException, ClientResourceMappingException;

    <R> R create(String url, List<HttpHeader> httpHeaders, R resourceToCreate, Class<R> clazz) throws ClientException, ClientResourceMappingException;

    <R, ID> R put(String url, List<HttpHeader> httpHeaders, ID identifier, R resourceToModify, Class<R> clazz) throws ClientException, ClientResourceMappingException;

    <R, ID> R patch(String url, List<HttpHeader> httpHeaders, ID identifier, R resourceToModify, Class<R> clazz) throws ClientException, ClientResourceMappingException;

    <I, O> O genericPatch(String url, List<HttpHeader> httpHeaders, I resourceToModify, Class<O> clazz) throws ClientException, ClientResourceMappingException;

    <ID> void delete(String url, List<HttpHeader> httpHeaders, ID identifier) throws ClientException;

    <I, O> O genericPost(String url, List<HttpHeader> httpHeaders, I resource, Class<O> clazz) throws ClientException, ClientResourceMappingException;

    File download(String url, List<HttpHeader> httpHeaders, Object resource, File storeLocation)
            throws ClientException, ClientResourceMappingException;

}
