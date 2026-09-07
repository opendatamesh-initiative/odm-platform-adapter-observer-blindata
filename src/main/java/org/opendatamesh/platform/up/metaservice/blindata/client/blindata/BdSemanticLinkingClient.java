package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical.BDDataCategoryRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical.BDLogicalNamespaceRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical.BDSemanticLinkingResolveFieldsRequestRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical.BDSemanticLinkingResolveFieldsResultRes;

import java.util.Optional;

public interface BdSemanticLinkingClient {

    BDSemanticLinkingResolveFieldsResultRes resolveSemanticFields(BDSemanticLinkingResolveFieldsRequestRes request);

    Optional<BDDataCategoryRes> getDataCategoryByNameAndNamespaceUuid(String dataCategoryName, String namespaceUuid);

    Optional<BDLogicalNamespaceRes> getLogicalNamespaceByIdentifier(String identifier);

    Optional<BDLogicalNamespaceRes> getLogicalNamespaceByPrefix(String prefix);
}
