package it.quantyca.odm.api.planes.utility.metaservices.metaservice.blindata;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DataProductVersionResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.InfoResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.OwnerResource;
import org.opendatamesh.platform.up.metaservice.blindata.client.BlindataClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.BlindataCredentials;
import org.opendatamesh.platform.up.metaservice.server.database.entities.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

public class BlindataAPITest extends UtilityForTests{

    protected Logger logger = LoggerFactory.getLogger("t.quantyca.odm.api.planes.utility.metaservices.metaservice.blindata");


    private static final String metaserviceBaseURL = "/api/v1/planes/utility/meta-services/loads/";

    DataProductVersionResource dataProductVersionRes;
    String dataProductId;
    BlindataCredentials credentials;

    BlindataClient blindataClient;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private Environment environment;

    @Before
    public void setUp(){
        logger.debug("----- SETTING UP -----");
        init();
        InfoResource info = new InfoResource();
        info.setFullyQualifiedName("urn:qty:dataproduct:TransportExecution:TripExecution:1");
        info.setName("TripExecution");
        OwnerResource owner = new OwnerResource();
        owner.setName("Mauro Luchetti");
        info.setOwner(owner);
        info.setDomain("TransportExecution");
        info.setVersionNumber("1.0.0");
        info.setDescription("Gestione dei viaggi necessari ad eseguire il trasporto della merce dalla sorgente alla destinazione");
        info.setDisplayName("Trip Execution");
        info.setEntityType("dataproduct");
        dataProductId = UUID.nameUUIDFromBytes(info.getFullyQualifiedName().getBytes()).toString();
        info.setDataProductId(dataProductId);
        dataProductVersionRes = new DataProductVersionResource();
        dataProductVersionRes.setInfo(info);
        blindataClient = new BlindataClient(restTemplate);

        credentials = new BlindataCredentials(environment.getProperty("blindata.url"),
                environment.getProperty("blindata.user"),
                environment.getProperty("blindata.password"),
                environment.getProperty("blindata.tenantUUID"),
                "roleUuid"
        );
    }

    @Test
    public void lifecycleTest(){
        Notification load = null;
        try {
            /* 
            ResponseEntity<Notification> loadResponse = testRestTemplate.postForEntity(apiUrlFromString(metaserviceBaseURL),
                    dataProduct, Notification.class);
            load = loadResponse.getBody();
            assertThat(load.getStatus()).isEqualTo(NotificationStatus.DONE);
            assertThat(load.getMetaServiceId()).isNotNull();

            //Trying to register a second time the same data product
            loadResponse = testRestTemplate.postForEntity(apiUrlFromString(metaserviceBaseURL),
                    dataProduct, Notification.class);
            load = loadResponse.getBody();
            assertThat(load.getStatus()).isEqualTo(NotificationStatus.FAILED);

            //Get loads test
            ResponseEntity<Notification[]> loadListResponse = testRestTemplate.getForEntity(apiUrlFromString(metaserviceBaseURL), Notification[].class);
            assertThat(loadListResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            */
        }finally {
            testRestTemplate.delete(apiUrlFromString(metaserviceBaseURL+"?dataProductId="+dataProductId));
        }

    }

    @Test
    public void putTest() throws Exception {
        Notification load = null;
        try {
            /* 
            ResponseEntity<Notification> loadResponse = testRestTemplate.postForEntity(apiUrlFromString(metaserviceBaseURL),
                    dataProduct, Notification.class);
            load = loadResponse.getBody();
            assertThat(load.getStatus()).isEqualTo(NotificationStatus.DONE);
            assertThat(load.getMetaServiceId()).isNotNull();
            assertThat(load.getDataproductId()).isEqualTo(dataProduct.getId());

            dataProduct.getInfo().setDescription("new description");
            dataProduct.getInfo().getOwner().setName("Robert");
            dataProduct.getInfo().setName("New DataProduct");


            testRestTemplate.put(apiUrlFromString(metaserviceBaseURL), dataProduct);
            BlindataSystem system = systemAPI.getSystem(load.getMetaServiceId(), credentials);
            for(AdditionalProperty additionalProperty: system.getAdditionalProperties()) {
                if(additionalProperty.getName().equals("name"))
                    assertThat(additionalProperty.getValue()).isEqualTo("New DataProduct");
                if(additionalProperty.getName().equals("ownerName"))
                    assertThat(additionalProperty.getValue()).isEqualTo("Robert");
                if(additionalProperty.getName().equals("description"))
                    assertThat(additionalProperty.getValue()).isEqualTo("new description");
            }
            */
        }finally {
            testRestTemplate.delete(apiUrlFromString(metaserviceBaseURL+"?dataProductId="+dataProductId));
        }
    }

}
