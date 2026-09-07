package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical;

import java.util.List;

public class BDSemanticLinkingResolveFieldsRequestRes {

    private List<BDSemanticLinkingResolveFieldPathRes> paths;

    public List<BDSemanticLinkingResolveFieldPathRes> getPaths() {
        return paths;
    }

    public void setPaths(List<BDSemanticLinkingResolveFieldPathRes> paths) {
        this.paths = paths;
    }
}
