package org.opendatamesh.platform.up.metaservice.blindata.utils.client;

import org.opendatamesh.platform.up.metaservice.blindata.utils.client.http.HttpHeader;
import org.opendatamesh.platform.up.metaservice.blindata.utils.client.http.Oauth2;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public abstract class RestUtilsFactory {

    private RestUtilsFactory() {
    }

    public static RestUtils getRestUtils(RestTemplate restTemplate) {
        return RestTemplateWrapper.wrap(restTemplate).build();
    }

    public static RestUtils getAuthenticatedRestUtils(
            RestTemplate restTemplate,
            List<HttpHeader> authenticatedHeaders,
            Oauth2 oauth2
    ) {
        return RestTemplateWrapper.wrap(restTemplate)
                .authenticate(authenticatedHeaders, oauth2)
                .build();
    }

    public static RestUtils getAsyncRestUtils(
            RestTemplate restTemplate,
            String asyncEndpointRequest,
            String asyncEndpointPoll
    ) {
        return RestTemplateWrapper.wrap(restTemplate)
                .async(asyncEndpointRequest, asyncEndpointPoll)
                .build();
    }

    public static RestUtils getAuthenticatedAsyncRestUtils(
            RestTemplate restTemplate,
            List<HttpHeader> authenticatedHeaders,
            Oauth2 oauth2,
            String asyncEndpointRequest,
            String asyncEndpointPoll
    ) {
        return RestTemplateWrapper.wrap(restTemplate)
                .authenticate(authenticatedHeaders, oauth2)
                .async(asyncEndpointRequest, asyncEndpointPoll)
                .build();
    }
}
