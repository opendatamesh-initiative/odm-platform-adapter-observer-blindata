package it.quantyca.odm.api.planes.utility.metaservices.metaservice.blindata;


import org.junit.runner.RunWith;
import org.opendatamesh.platform.up.metaservice.api.MetaserviceApp;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.annotation.PostConstruct;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {MetaserviceApp.class})
public abstract class UtilityForTests {

    @LocalServerPort
    protected String port;
    TestRestTemplate testRestTemplate;

    protected String apiUrlFromString(String routeUrlString) {
        return "http://localhost:" + port + routeUrlString;
    }

    @PostConstruct
    public final void init() {
        testRestTemplate = new TestRestTemplate();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(30000);
        requestFactory.setReadTimeout(30000);
        testRestTemplate.getRestTemplate().setRequestFactory(requestFactory);
        // add uri template handler because '+' of iso date would not be encoded
        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
        defaultUriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.TEMPLATE_AND_VALUES);
        testRestTemplate.setUriTemplateHandler(defaultUriBuilderFactory);
    }
}
