package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataCategoryRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.LogicalFieldSemanticLinkRes;

import java.util.Optional;

public interface BDSemanticLinkingClient {

    LogicalFieldSemanticLinkRes getSemanticLinkElements(String pathString, String defaultNamespaceIdentifier);

    Optional<BDDataCategoryRes> getDataCategoryByNameAndNamespace(String dataCategoryName, String defaultNamespaceIdentifier);
}
