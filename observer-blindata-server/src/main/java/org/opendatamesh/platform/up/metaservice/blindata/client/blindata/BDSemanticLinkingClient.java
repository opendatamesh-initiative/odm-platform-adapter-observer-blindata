package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.SemanticLinkingMetaserviceRes;

public interface BDSemanticLinkingClient {

    SemanticLinkingMetaserviceRes getSemanticLinkElements(String pathString, String defaultNamespaceIdentifier);
}
