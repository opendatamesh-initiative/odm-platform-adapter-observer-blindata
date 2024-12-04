package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

public class SemanticLinkingMetaserviceRes {

    LogicalFieldSemanticLinkRes semanticLinkRes;

    BDDataCategoryRes rootDataCategory;

    public BDDataCategoryRes getRootDataCategory() {
        return rootDataCategory;
    }

    public void setRootDataCategory(BDDataCategoryRes rootDataCategory) {
        this.rootDataCategory = rootDataCategory;
    }

    public LogicalFieldSemanticLinkRes getSemanticLinkElementRes() {
        return semanticLinkRes;
    }

    public void setSemanticLinkElementResList(LogicalFieldSemanticLinkRes semanticLinkElementRes) {
        this.semanticLinkRes = semanticLinkElementRes;
    }
}
