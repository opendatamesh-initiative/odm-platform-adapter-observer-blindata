package org.opendatamesh.platform.up.metaservice.blindata.configurations;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObserverBlindataOpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("ODM Platform Utility Plane Observer Blindata")
                        .summary(
                                "This page describe tha API exposed by the Observer Adapter for Blindata.io Service Server " +
                                "of the Product Plane of the Open Data Mesh Platform."
                        )
                        .description(
                                "This page describe tha API exposed by the Observer Adapter for Blindata.io Service Server" +
                                "of the Product Plane of the Open Data Mesh Platform." +

                                "\r\n# Overview" +
                                "\r\nThe Observer Adapter for Blindata.io of the Open Data Mesh platform manages the " +
                                "reception of notifications from other ODM microservices,and their handling phase." +
                                "\nEvery notification contains an Event that could lead to object creation, update or " +
                                "deletion on the target system Blindata.io."
                        )
                        .version("0.9.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                        .contact(new Contact()
                                .name("ODM Platform Team")
                                .email("odm.info@quantyca.it"))
                ).externalDocs(new ExternalDocumentation()
                        .description("Open Data Mesh Platform Documentation")
                        .url("https://dpds.opendatamesh.org/")
                );
    }

}
