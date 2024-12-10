package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

public class SemanticLinkingMetaserviceRes {

    LogicalFieldSemanticLinkRes semanticLinkRes;

    BDDataCategoryRes rootDataCategory;

    public LogicalFieldSemanticLinkRes getSemanticLinkRes() {
        return semanticLinkRes;
    }

    public void setSemanticLinkRes(LogicalFieldSemanticLinkRes semanticLinkRes) {
        this.semanticLinkRes = semanticLinkRes;
    }

    public BDDataCategoryRes getRootDataCategory() {
        return rootDataCategory;
    }

    public void setRootDataCategory(BDDataCategoryRes rootDataCategory) {
        this.rootDataCategory = rootDataCategory;
    }
}
