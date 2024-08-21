package org.opendatamesh.platform.up.metaservice.blindata;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.opendatamesh.platform.up.metaservice.blindata.rest.v1.RoutesV1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.annotation.PostConstruct;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {ObserverBlindataApp.class, TestConfig.class},
        properties = {"spring.main.allow-bean-definition-overriding=true"}
)
public abstract class ObserverBlindataAppIT {
    @LocalServerPort
    protected String port;
    protected TestRestTemplate rest;
    @Autowired
    protected ObjectMapper mapper;

    @PostConstruct
    public final void init() {
        rest = new TestRestTemplate();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(30000);
        requestFactory.setReadTimeout(30000);
        rest.getRestTemplate().setRequestFactory(requestFactory);
        // add uri template handler because '+' of iso date would not be encoded
        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
        defaultUriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.TEMPLATE_AND_VALUES);
        rest.setUriTemplateHandler(defaultUriBuilderFactory);
    }

    protected String apiUrl(RoutesV1 route) {
        return apiUrl(route, "");
    }

    protected String apiUrl(RoutesV1 route, String extension) {
        return apiUrlFromString(route.getPath() + extension);
    }

    protected String apiUrlFromString(String routeUrlString) {
        return "http://localhost:" + port + routeUrlString;
    }

    protected String apiUrlOfItem(RoutesV1 route) {
        return apiUrl(route, "/{uuid}");
    }
}
