package org.opendatamesh.platform.up.metaservice.blindata;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opendatamesh.platform.core.commons.test.ODMIntegrationTest;
import org.opendatamesh.platform.core.commons.test.ODMResourceBuilder;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.up.observer.api.clients.ConsumeClientImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static org.assertj.core.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
//@ActiveProfiles("test")
//@ActiveProfiles("testpostgresql")
//@ActiveProfiles("testmysql")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {ObserverBlindataApp.class, BDClientTestConfigs.class},
        properties = "spring.main.allow-bean-definition-overriding=true"
)
public class ODMObserverBlindataAppIT extends ODMIntegrationTest {

    @LocalServerPort
    protected String port;

    @Autowired
    protected ObjectMapper objectMapper;

    protected ConsumeClientImpl consumeClient;

    protected final String DB_TABLES_POSTGRESQL = "src/test/resources/db/tables_postgresql.txt";

    protected final String DB_TABLES_MYSQL = "src/test/resources/db/tables_mysql.txt";

    @PostConstruct
    public final void init() {
        consumeClient = new ConsumeClientImpl("http://localhost:" + port);
        resourceBuilder = new ODMResourceBuilder(ObjectMapperFactory.JSON_MAPPER);
    }


    // ======================================================================================
    // Create test basic resources
    // ======================================================================================

    protected EventNotificationResource createNotificationResource(String filePath) throws IOException {

        try {
            return resourceBuilder.readResourceFromFile(filePath, EventNotificationResource.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read event notification from file: " + t.getMessage());
            return null;
        }

    }

}
