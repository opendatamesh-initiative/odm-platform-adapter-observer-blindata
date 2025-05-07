package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload;

import org.opendatamesh.dpds.model.info.Info;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDShortUserRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDStewardshipResponsibilityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDStewardshipRoleRes;

public class DataProductUploadInitialState {
    private Info dataProductInfo;
    private BDDataProductRes existentDataProduct;
    private BDShortUserRes user;
    private String defaultRoleUuid;
    private BDStewardshipRoleRes dataProductRole;
    private BDStewardshipResponsibilityRes dataProductResponsibility;

    public DataProductUploadInitialState() {
    }

    public Info getDataProductInfo() {
        return dataProductInfo;
    }

    public BDDataProductRes getExistentDataProduct() {
        return this.existentDataProduct;
    }

    public BDShortUserRes getUser() {
        return this.user;
    }

    public String getDefaultRoleUuid() {
        return this.defaultRoleUuid;
    }

    public BDStewardshipRoleRes getDataProductRole() {
        return this.dataProductRole;
    }

    public BDStewardshipResponsibilityRes getDataProductResponsibility() {
        return this.dataProductResponsibility;
    }

    public void setDataProductInfo(Info dataProductInfo) {
        this.dataProductInfo = dataProductInfo;
    }

    public void setExistentDataProduct(BDDataProductRes existentDataProduct) {
        this.existentDataProduct = existentDataProduct;
    }

    public void setUser(BDShortUserRes user) {
        this.user = user;
    }

    public void setDefaultRoleUuid(String defaultRoleUuid) {
        this.defaultRoleUuid = defaultRoleUuid;
    }

    public void setDataProductRole(BDStewardshipRoleRes dataProductRole) {
        this.dataProductRole = dataProductRole;
    }

    public void setDataProductResponsibility(BDStewardshipResponsibilityRes dataProductResponsibility) {
        this.dataProductResponsibility = dataProductResponsibility;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof DataProductUploadInitialState)) return false;
        final DataProductUploadInitialState other = (DataProductUploadInitialState) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$dataProductInfo = this.getDataProductInfo();
        final Object other$dataProductInfo = other.getDataProductInfo();
        if (this$dataProductInfo == null ? other$dataProductInfo != null : !this$dataProductInfo.equals(other$dataProductInfo))
            return false;
        final Object this$existentDataProduct = this.getExistentDataProduct();
        final Object other$existentDataProduct = other.getExistentDataProduct();
        if (this$existentDataProduct == null ? other$existentDataProduct != null : !this$existentDataProduct.equals(other$existentDataProduct))
            return false;
        final Object this$user = this.getUser();
        final Object other$user = other.getUser();
        if (this$user == null ? other$user != null : !this$user.equals(other$user)) return false;
        final Object this$defaultRoleUuid = this.getDefaultRoleUuid();
        final Object other$defaultRoleUuid = other.getDefaultRoleUuid();
        if (this$defaultRoleUuid == null ? other$defaultRoleUuid != null : !this$defaultRoleUuid.equals(other$defaultRoleUuid))
            return false;
        final Object this$dataProductRole = this.getDataProductRole();
        final Object other$dataProductRole = other.getDataProductRole();
        if (this$dataProductRole == null ? other$dataProductRole != null : !this$dataProductRole.equals(other$dataProductRole))
            return false;
        final Object this$dataProductResponsibility = this.getDataProductResponsibility();
        final Object other$dataProductResponsibility = other.getDataProductResponsibility();
        if (this$dataProductResponsibility == null ? other$dataProductResponsibility != null : !this$dataProductResponsibility.equals(other$dataProductResponsibility))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof DataProductUploadInitialState;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $dataProductInfo = this.getDataProductInfo();
        result = result * PRIME + ($dataProductInfo == null ? 43 : $dataProductInfo.hashCode());
        final Object $existentDataProduct = this.getExistentDataProduct();
        result = result * PRIME + ($existentDataProduct == null ? 43 : $existentDataProduct.hashCode());
        final Object $user = this.getUser();
        result = result * PRIME + ($user == null ? 43 : $user.hashCode());
        final Object $defaultRoleUuid = this.getDefaultRoleUuid();
        result = result * PRIME + ($defaultRoleUuid == null ? 43 : $defaultRoleUuid.hashCode());
        final Object $dataProductRole = this.getDataProductRole();
        result = result * PRIME + ($dataProductRole == null ? 43 : $dataProductRole.hashCode());
        final Object $dataProductResponsibility = this.getDataProductResponsibility();
        result = result * PRIME + ($dataProductResponsibility == null ? 43 : $dataProductResponsibility.hashCode());
        return result;
    }

    public String toString() {
        return "DataProductUploadInitialState(dataProductInfo=" + this.getDataProductInfo() + ", existentDataProduct=" + this.getExistentDataProduct() + ", user=" + this.getUser() + ", defaultRoleUuid=" + this.getDefaultRoleUuid() + ", dataProductRole=" + this.getDataProductRole() + ", dataProductResponsibility=" + this.getDataProductResponsibility() + ")";
    }
}
