package org.opendatamesh.platform.up.metaservice.blindata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


//TODO gestione dipendenze sul POM verso odm-dpexperience-service e metaservice
//le annotazioni seguenti serviranno per far risolvere le dipendenze alle diverse implementazioni del meta service
@SpringBootApplication
@ComponentScan(basePackages = "org.opendatamesh.platform.up.metaservice")
@EntityScan(basePackages = "org.opendatamesh.platform.up.metaservice")
@EnableJpaRepositories(basePackages = "org.opendatamesh.platform.up.metaservice")
public class MetaserviceApp {

	public static void main(String[] args) {
		SpringApplication.run(MetaserviceApp.class, args);
	}

}
