package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical;

import java.util.ArrayList;
import java.util.List;

public class BDSemanticLinkingResolveFieldsResultRes {

    private List<BDSemanticLinkingResolveFieldPathResultRes> paths = new ArrayList<>();

    public List<BDSemanticLinkingResolveFieldPathResultRes> getPaths() {
        return paths;
    }

    public void setPaths(List<BDSemanticLinkingResolveFieldPathResultRes> paths) {
        this.paths = paths;
    }
}
