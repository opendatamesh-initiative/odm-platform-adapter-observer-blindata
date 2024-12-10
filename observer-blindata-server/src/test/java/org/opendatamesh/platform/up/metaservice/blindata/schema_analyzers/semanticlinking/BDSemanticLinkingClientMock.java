package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDSemanticLinkingClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataCategoryRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDLogicalNamespaceRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.LogicalFieldSemanticLinkRes;

import java.util.Optional;

public class BDSemanticLinkingClientMock implements BDSemanticLinkingClient {

    private final SemanticLinkingManagerInitialState initialState;

    public BDSemanticLinkingClientMock(SemanticLinkingManagerInitialState initialState) {
        this.initialState = initialState;
    }

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public LogicalFieldSemanticLinkRes getSemanticLinkElements(String pathString, String defaultNamespaceIdentifier) {
        return initialState.getSemanticLinkRes();
    }

    @Override
    public Optional<BDDataCategoryRes> getDataCategoryByNameAndNamespaceUuid(String dataCategoryName, String namespaceUuid) {
        return Optional.empty();
    }

    @Override
    public Optional<BDLogicalNamespaceRes> getLogicalNamespaceByIdentifier(String identifier) {
        return Optional.empty();
    }
}
