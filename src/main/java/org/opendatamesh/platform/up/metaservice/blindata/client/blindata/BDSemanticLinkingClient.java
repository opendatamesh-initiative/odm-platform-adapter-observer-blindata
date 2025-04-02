package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDDataCategoryRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDLogicalNamespaceRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDLogicalFieldSemanticLinkRes;

import java.util.Optional;

public interface BDSemanticLinkingClient {

    BDLogicalFieldSemanticLinkRes getSemanticLinkElements(String pathString, String defaultNamespaceIdentifier);

    Optional<BDDataCategoryRes> getDataCategoryByNameAndNamespaceUuid(String dataCategoryName, String namespaceUuid);

    Optional<BDLogicalNamespaceRes> getLogicalNamespaceByIdentifier(String identifier);
}
