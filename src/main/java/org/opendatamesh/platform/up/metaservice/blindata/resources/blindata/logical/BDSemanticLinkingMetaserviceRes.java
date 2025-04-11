package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical;

public class BDSemanticLinkingMetaserviceRes {

    BDLogicalFieldSemanticLinkRes semanticLinkRes;

    BDDataCategoryRes rootDataCategory;

    public BDLogicalFieldSemanticLinkRes getSemanticLinkRes() {
        return semanticLinkRes;
    }

    public void setSemanticLinkRes(BDLogicalFieldSemanticLinkRes semanticLinkRes) {
        this.semanticLinkRes = semanticLinkRes;
    }

    public BDDataCategoryRes getRootDataCategory() {
        return rootDataCategory;
    }

    public void setRootDataCategory(BDDataCategoryRes rootDataCategory) {
        this.rootDataCategory = rootDataCategory;
    }
}
