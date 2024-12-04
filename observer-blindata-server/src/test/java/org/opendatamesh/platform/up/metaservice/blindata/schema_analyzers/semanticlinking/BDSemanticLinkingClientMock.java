package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDSemanticLinkingClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.SemanticLinkingMetaserviceRes;

public class BDSemanticLinkingClientMock implements BDSemanticLinkingClient {

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public SemanticLinkingMetaserviceRes getSemanticLinkElements(String pathString, String defaultNamespaceIdentifier) {
        try {
            return mapper.readValue(Resources.toByteArray(
                            getClass().getResource("clientResponse.json")
                    ),
                    SemanticLinkingMetaserviceRes.class);
        } catch (Exception e) {
            return null;
        }
    }
}
