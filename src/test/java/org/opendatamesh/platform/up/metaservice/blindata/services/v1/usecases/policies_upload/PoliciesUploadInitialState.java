package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.policies_upload;

import org.opendatamesh.dpds.model.info.Info;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEvaluationResultResource;

import java.util.List;

public class PoliciesUploadInitialState {
    private BDDataProductRes existentDataProduct;
    private Info dataProductInfo;
    private List<OdmPolicyEvaluationResultResource> policiesResults;

    public PoliciesUploadInitialState() {
    }

    public BDDataProductRes getExistentDataProduct() {
        return this.existentDataProduct;
    }

    public Info getDataProductInfo() {
        return this.dataProductInfo;
    }

    public List<OdmPolicyEvaluationResultResource> getPoliciesResults() {
        return this.policiesResults;
    }

    public void setExistentDataProduct(BDDataProductRes existentDataProduct) {
        this.existentDataProduct = existentDataProduct;
    }

    public void setDataProductInfo(Info dataProductInfo) {
        this.dataProductInfo = dataProductInfo;
    }

    public void setPoliciesResults(List<OdmPolicyEvaluationResultResource> policiesResults) {
        this.policiesResults = policiesResults;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof PoliciesUploadInitialState)) return false;
        final PoliciesUploadInitialState other = (PoliciesUploadInitialState) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$existentDataProduct = this.getExistentDataProduct();
        final Object other$existentDataProduct = other.getExistentDataProduct();
        if (this$existentDataProduct == null ? other$existentDataProduct != null : !this$existentDataProduct.equals(other$existentDataProduct))
            return false;
        final Object this$dataProductInfo = this.getDataProductInfo();
        final Object other$dataProductInfo = other.getDataProductInfo();
        if (this$dataProductInfo == null ? other$dataProductInfo != null : !this$dataProductInfo.equals(other$dataProductInfo))
            return false;
        final Object this$policiesResults = this.getPoliciesResults();
        final Object other$policiesResults = other.getPoliciesResults();
        if (this$policiesResults == null ? other$policiesResults != null : !this$policiesResults.equals(other$policiesResults))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof PoliciesUploadInitialState;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $existentDataProduct = this.getExistentDataProduct();
        result = result * PRIME + ($existentDataProduct == null ? 43 : $existentDataProduct.hashCode());
        final Object $dataProductInfo = this.getDataProductInfo();
        result = result * PRIME + ($dataProductInfo == null ? 43 : $dataProductInfo.hashCode());
        final Object $policiesResults = this.getPoliciesResults();
        result = result * PRIME + ($policiesResults == null ? 43 : $policiesResults.hashCode());
        return result;
    }

    public String toString() {
        return "PoliciesUploadInitialState(existentDataProduct=" + this.getExistentDataProduct() + ", dataProductInfo=" + this.getDataProductInfo() + ", policiesResults=" + this.getPoliciesResults() + ")";
    }
}
