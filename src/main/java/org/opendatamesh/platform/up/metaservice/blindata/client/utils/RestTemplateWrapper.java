package org.opendatamesh.platform.up.metaservice.blindata.client.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.exceptions.ClientException;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.http.HttpEntity;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.http.HttpHeader;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.http.HttpMethod;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.http.Oauth2;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

class RestTemplateWrapper implements RestUtilsTemplate {

    private final RestTemplate restTemplate;
    private List<HttpHeader> authenticatedHeaders;
    private Oauth2 oauth2;
    private String asyncEndpointRequest;
    private String asyncEndpointPoll;

    private RestTemplateWrapper(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public static RestTemplateWrapper wrap(RestTemplate restTemplate) {
        return new RestTemplateWrapper(restTemplate);
    }

    public RestTemplateWrapper authenticate(List<HttpHeader> authenticatedHeaders, Oauth2 oauth2) {
        this.authenticatedHeaders = authenticatedHeaders;
        this.oauth2 = oauth2;
        return this;
    }

    public RestTemplateWrapper async(String asyncEndpointRequest, String asyncEndpointPoll) {
        this.asyncEndpointRequest = asyncEndpointRequest;
        this.asyncEndpointPoll = asyncEndpointPoll;
        return this;
    }

    public RestUtils build() {
        RestUtilsTemplate template = this;

        if (authenticatedHeaders != null || oauth2 != null) {
            template = new AuthenticatedRestUtilsTemplate(template, authenticatedHeaders, oauth2);
        }

        if (asyncEndpointRequest != null && asyncEndpointPoll != null) {
            template = new AsyncRestUtilsTemplate(template, asyncEndpointRequest, asyncEndpointPoll);
        }

        return new BaseRestUtils(template);
    }

    @Override
    public <T> T exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) throws ClientException {
        try {
            LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            requestEntity.getRawHeaders().forEach(headers::add);
            return restTemplate.exchange(
                    url,
                    org.springframework.http.HttpMethod.resolve(method.name()),
                    new org.springframework.http.HttpEntity<>(requestEntity.getBody(), headers),
                    responseType,
                    uriVariables
            ).getBody();
        } catch (RestClientResponseException e) {
            throw new ClientException(e.getRawStatusCode(), e.getResponseBodyAsString());
        } catch (RestClientException e) {
            throw new ClientException(500, e.getMessage());
        }
    }

    @Override
    public File download(String url, List<HttpHeader> httpHeaders, Object resource, File storeLocation) throws ClientException {
        try {
            return restTemplate.execute(url, org.springframework.http.HttpMethod.POST, request -> {
                if (httpHeaders != null) {
                    LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
                    httpHeaders.forEach(h -> headers.put(h.getName(), Lists.newArrayList(h.getValue())));
                    request.getHeaders().addAll(headers);
                }
                if (resource != null) {
                    new ObjectMapper().writeValue(request.getBody(), resource);
                }
            }, response -> {
                try (FileOutputStream fos = new FileOutputStream(storeLocation)) {
                    StreamUtils.copy(response.getBody(), fos);
                    return storeLocation;
                }
            });
        } catch (RestClientResponseException e) {
            throw new ClientException(e.getRawStatusCode(), e.getResponseBodyAsString());
        } catch (RestClientException e) {
            throw new ClientException(500, e.getMessage());
        }
    }
}
