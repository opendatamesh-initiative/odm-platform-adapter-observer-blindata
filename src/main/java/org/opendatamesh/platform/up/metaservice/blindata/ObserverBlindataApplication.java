package org.opendatamesh.platform.up.metaservice.blindata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ObserverBlindataApplication {
    public static void main(String[] args) {
        SpringApplication.run(ObserverBlindataApplication.class, args);
    }
}
