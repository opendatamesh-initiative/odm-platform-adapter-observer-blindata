package org.opendatamesh.platform.up.metaservice.blindata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opendatamesh.platform.up.notification.api.clients.NotificationClient;
import org.opendatamesh.platform.up.notification.api.resources.NotificationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
//@ActiveProfiles("dev")
//@ActiveProfiles("testpostgresql")
//@ActiveProfiles("testmysql")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {MetaserviceApp.class, BDClientTestConfigs.class},
        properties = "spring.main.allow-bean-definition-overriding=true"
)
public class MetaserviceAppIT {

    @LocalServerPort
    protected String port;

    protected NotificationClient notificationClient;

    protected ResourceBuilder resourceBuilder;

    protected final String DB_TABLES_POSTGRESQL = "src/test/resources/db/tables_postgresql.txt";

    protected final String DB_TABLES_MYSQL = "src/test/resources/db/tables_mysql.txt";

    protected final String NOTIFICATION_1 = "src/test/resources/notification1.json";

    protected final String NOTIFICATION_2 = "src/test/resources/notification2.json";

    @PostConstruct
    public final void init() {
        notificationClient = new NotificationClient("http://localhost:" + port);
        resourceBuilder = new ResourceBuilder();
    }

    @BeforeEach
    public void cleanDbState(@Autowired JdbcTemplate jdbcTemplate, @Autowired Environment environment) throws IOException {
        String activeProfile = Arrays.stream(environment.getActiveProfiles()).findFirst().get();
        String[] tableSet;
        if (activeProfile.equals("testpostgresql")) {
            tableSet = Files.readAllLines(new File(DB_TABLES_POSTGRESQL).toPath(), Charset.defaultCharset()).toArray(new String[0]);
            System.out.println(tableSet);
            JdbcTestUtils.deleteFromTables(
                    jdbcTemplate,
                    tableSet
            );
        } else if (activeProfile.equals("testmysql")) {
            tableSet = Files.readAllLines(new File(DB_TABLES_MYSQL).toPath(), Charset.defaultCharset()).toArray(new String[0]);
            JdbcTestUtils.deleteFromTables(
                    jdbcTemplate,
                    tableSet
            );
        }
    }


    // ======================================================================================
    // Create test basic resources
    // ======================================================================================

    protected NotificationResource createNotification1() throws IOException {

        NotificationResource notificationResource = resourceBuilder.readResourceFromFile(
                NOTIFICATION_1,
                NotificationResource.class
        );

        ResponseEntity<NotificationResource> postResponse = notificationClient.createNotification(notificationResource);

        verifyResponseEntity(postResponse, HttpStatus.CREATED, true);

        return postResponse.getBody();

    }

    protected NotificationResource createNotification2() throws IOException {

        NotificationResource notificationResource = resourceBuilder.readResourceFromFile(
                NOTIFICATION_2,
                NotificationResource.class
        );

        ResponseEntity<NotificationResource> postResponse = notificationClient.createNotification(notificationResource);

        verifyResponseEntity(postResponse, HttpStatus.CREATED, true);

        return postResponse.getBody();

    }


    // ======================================================================================
    // Verify test basic resources
    // ======================================================================================

    protected ResponseEntity verifyResponseEntity(
            ResponseEntity responseEntity,
            HttpStatus statusCode,
            boolean checkBody
    ) {
        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(statusCode);
        if (checkBody)
            assertThat(responseEntity.getBody()).isNotNull();
        return responseEntity;
    }

    protected void verifyResponseError(ResponseEntity<Error> errorResponse, HttpStatus errorStatus) {
        assertThat(errorResponse.getStatusCode()).isEqualByComparingTo(errorStatus);
    }
}
