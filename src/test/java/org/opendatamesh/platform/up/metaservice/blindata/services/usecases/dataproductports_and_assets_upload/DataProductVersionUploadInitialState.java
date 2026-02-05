package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload;

import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductPortAssetDetailRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;

import java.util.List;

public class DataProductVersionUploadInitialState {
    private BDDataProductRes existentDataProduct;
    private DataProductVersion dataProductDescriptor;
    private List<BDDataProductPortAssetDetailRes> extractedAssets;

    public DataProductVersionUploadInitialState() {
        //DO NOTHING
    }

    public BDDataProductRes getExistentDataProduct() {
        return this.existentDataProduct;
    }

    public DataProductVersion getDataProductDescriptor() {
        return dataProductDescriptor;
    }

    public void setDataProductDescriptor(DataProductVersion dataProductDescriptor) {
        this.dataProductDescriptor = dataProductDescriptor;
    }

    public List<BDDataProductPortAssetDetailRes> getExtractedAssets() {
        return this.extractedAssets;
    }

    public void setExistentDataProduct(BDDataProductRes existentDataProduct) {
        this.existentDataProduct = existentDataProduct;
    }

    public void setExtractedAssets(List<BDDataProductPortAssetDetailRes> extractedAssets) {
        this.extractedAssets = extractedAssets;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof DataProductVersionUploadInitialState)) return false;
        final DataProductVersionUploadInitialState other = (DataProductVersionUploadInitialState) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$existentDataProduct = this.getExistentDataProduct();
        final Object other$existentDataProduct = other.getExistentDataProduct();
        if (this$existentDataProduct == null ? other$existentDataProduct != null : !this$existentDataProduct.equals(other$existentDataProduct))
            return false;
        final Object this$dataProductDescriptor = this.getDataProductDescriptor();
        final Object other$dataProductDescriptor = other.getDataProductDescriptor();
        if (this$dataProductDescriptor == null ? other$dataProductDescriptor != null : !this$dataProductDescriptor.equals(other$dataProductDescriptor))
            return false;
        final Object this$extractedAssets = this.getExtractedAssets();
        final Object other$extractedAssets = other.getExtractedAssets();
        if (this$extractedAssets == null ? other$extractedAssets != null : !this$extractedAssets.equals(other$extractedAssets))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof DataProductVersionUploadInitialState;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $existentDataProduct = this.getExistentDataProduct();
        result = result * PRIME + ($existentDataProduct == null ? 43 : $existentDataProduct.hashCode());
        final Object $dataProductDescriptor = this.getDataProductDescriptor();
        result = result * PRIME + ($dataProductDescriptor == null ? 43 : $dataProductDescriptor.hashCode());
        final Object $extractedAssets = this.getExtractedAssets();
        result = result * PRIME + ($extractedAssets == null ? 43 : $extractedAssets.hashCode());
        return result;
    }

    public String toString() {
        return "DataProductVersionUploadInitialState(existentDataProduct=" + this.getExistentDataProduct() + ", dataProductDescriptor=" + this.getDataProductDescriptor() + ", extractedAssets=" + this.getExtractedAssets() + ")";
    }
}
