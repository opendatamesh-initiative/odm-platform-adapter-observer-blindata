package org.opendatamesh.platform.up.metaservice.blindata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opendatamesh.platform.core.commons.test.ODMIntegrationTest;
import org.opendatamesh.platform.core.commons.test.ODMResourceBuilder;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.notification.api.clients.NotificationClientImpl;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@ExtendWith(SpringExtension.class)
//@ActiveProfiles("test")
//@ActiveProfiles("testpostgresql")
//@ActiveProfiles("testmysql")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {ObserverBlindataApp.class, BDClientTestConfigs.class},
        properties = "spring.main.allow-bean-definition-overriding=true"
)
public class ObserverBlindataAppIT extends ODMIntegrationTest {

    @LocalServerPort
    protected String port;

    protected NotificationClientImpl notificationClient;

    protected final String DB_TABLES_POSTGRESQL = "src/test/resources/db/tables_postgresql.txt";

    protected final String DB_TABLES_MYSQL = "src/test/resources/db/tables_mysql.txt";

    protected final String NOTIFICATION_1 = "src/test/resources/notification1.json";

    protected final String NOTIFICATION_2 = "src/test/resources/notification2.json";

    @PostConstruct
    public final void init() {
        notificationClient = new NotificationClientImpl("http://localhost:" + port);
        resourceBuilder = new ODMResourceBuilder(ObjectMapperFactory.JSON_MAPPER);
    }

    @BeforeEach
    public void cleanDbState(@Autowired JdbcTemplate jdbcTemplate, @Autowired Environment environment) throws IOException {
        String activeProfile = Arrays.stream(environment.getActiveProfiles()).findFirst().get();
        if (activeProfile.equals("testpostgresql"))
            truncateAllTablesFromDb(jdbcTemplate, new File(DB_TABLES_POSTGRESQL));
        else if (activeProfile.equals("testmysql") || activeProfile.equals("localmysql")) {
            truncateAllTablesFromDb(jdbcTemplate, new File(DB_TABLES_MYSQL));
        }
    }


    // ======================================================================================
    // Create test basic resources
    // ======================================================================================

    /*protected EventNotificationResource createNotification1() throws IOException {

        EventNotificationResource notificationResource = resourceBuilder.readResourceFromFile(
                NOTIFICATION_1,
                EventNotificationResource.class
        );

        ResponseEntity<EventNotificationResource> postResponse = notificationClient.createNotificationResponseEntity(notificationResource);

        verifyResponseEntity(postResponse, HttpStatus.CREATED, true);

        return postResponse.getBody();

    }

    protected EventNotificationResource createNotification2() throws IOException {

        EventNotificationResource notificationResource = resourceBuilder.readResourceFromFile(
                NOTIFICATION_2,
                EventNotificationResource.class
        );

        ResponseEntity<EventNotificationResource> postResponse = notificationClient.createNotificationResponseEntity(notificationResource);

        verifyResponseEntity(postResponse, HttpStatus.CREATED, true);

        return postResponse.getBody();

    }*/

}
