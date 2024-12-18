package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.semanticlinking;

public class SemanticLinkingManagerMockFactory {

    public SemanticLinkManager buildSemanticLinkingManagerMock(SemanticLinkingManagerInitialState initialState) {
        return new SemanticLinkManagerImpl(new BDSemanticLinkingClientMock(initialState));
    }
}
