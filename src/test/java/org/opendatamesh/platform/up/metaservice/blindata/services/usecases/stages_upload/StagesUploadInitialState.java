package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.stages_upload;

import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductStageRes;

import java.util.List;

public class StagesUploadInitialState {
    private DataProductVersion dataProductDescriptor;
    private BDDataProductRes existentDataProduct;
    private List<BDDataProductStageRes> dataProductVersionStages;

    public StagesUploadInitialState() {
    }

    public DataProductVersion getDataProductDescriptor() {
        return dataProductDescriptor;
    }

    public void setDataProductDescriptor(DataProductVersion dataProductDescriptor) {
        this.dataProductDescriptor = dataProductDescriptor;
    }

    public BDDataProductRes getExistentDataProduct() {
        return this.existentDataProduct;
    }

    public List<BDDataProductStageRes> getDataProductVersionStages() {
        return this.dataProductVersionStages;
    }

    public void setExistentDataProduct(BDDataProductRes existentDataProduct) {
        this.existentDataProduct = existentDataProduct;
    }

    public void setDataProductVersionStages(List<BDDataProductStageRes> dataProductVersionStages) {
        this.dataProductVersionStages = dataProductVersionStages;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof StagesUploadInitialState)) return false;
        final StagesUploadInitialState other = (StagesUploadInitialState) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$dataProductDescriptor = this.getDataProductDescriptor();
        final Object other$dataProductDescriptor = other.getDataProductDescriptor();
        if (this$dataProductDescriptor == null ? other$dataProductDescriptor != null : !this$dataProductDescriptor.equals(other$dataProductDescriptor))
            return false;
        final Object this$existentDataProduct = this.getExistentDataProduct();
        final Object other$existentDataProduct = other.getExistentDataProduct();
        if (this$existentDataProduct == null ? other$existentDataProduct != null : !this$existentDataProduct.equals(other$existentDataProduct))
            return false;
        final Object this$dataProductVersionStages = this.getDataProductVersionStages();
        final Object other$dataProductVersionStages = other.getDataProductVersionStages();
        if (this$dataProductVersionStages == null ? other$dataProductVersionStages != null : !this$dataProductVersionStages.equals(other$dataProductVersionStages))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof StagesUploadInitialState;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $dataProductDescriptor = this.getDataProductDescriptor();
        result = result * PRIME + ($dataProductDescriptor == null ? 43 : $dataProductDescriptor.hashCode());
        final Object $existentDataProduct = this.getExistentDataProduct();
        result = result * PRIME + ($existentDataProduct == null ? 43 : $existentDataProduct.hashCode());
        final Object $dataProductVersionStages = this.getDataProductVersionStages();
        result = result * PRIME + ($dataProductVersionStages == null ? 43 : $dataProductVersionStages.hashCode());
        return result;
    }

    public String toString() {
        return "StagesUploadInitialState(dataProductDescriptor=" + this.getDataProductDescriptor() + ", existentDataProduct=" + this.getExistentDataProduct() + ", dataProductVersionStages=" + this.getDataProductVersionStages() + ")";
    }
}
