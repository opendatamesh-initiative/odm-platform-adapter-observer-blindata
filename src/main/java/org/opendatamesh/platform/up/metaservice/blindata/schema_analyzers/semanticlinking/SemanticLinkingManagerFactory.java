package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDSemanticLinkingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SemanticLinkingManagerFactory {

    @Autowired
    @Qualifier("bdSemanticLinkingClient")
    private BDSemanticLinkingClient client;

    @Bean
    public SemanticLinkManager semanticLinkManager() {
        return new SemanticLinkManagerImpl(client);
    }
}
