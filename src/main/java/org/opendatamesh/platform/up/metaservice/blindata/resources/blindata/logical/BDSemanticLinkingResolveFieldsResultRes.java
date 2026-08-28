package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical;

import java.util.ArrayList;
import java.util.List;

public class BDSemanticLinkingResolveFieldsResultRes {

    private List<BDSemanticLinkingResolveFieldItemResultRes> items = new ArrayList<>();

    public List<BDSemanticLinkingResolveFieldItemResultRes> getItems() {
        return items;
    }

    public void setItems(List<BDSemanticLinkingResolveFieldItemResultRes> items) {
        this.items = items;
    }
}
