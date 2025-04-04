package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata;

import java.util.List;

public class BDProductPortAssetSystemRes {

    private BDSystemRes system;
    private List<BDPhysicalEntityRes> physicalEntities;

    public BDProductPortAssetSystemRes() {
    }

    public BDSystemRes getSystem() {
        return this.system;
    }

    public List<BDPhysicalEntityRes> getPhysicalEntities() {
        return this.physicalEntities;
    }

    public void setSystem(BDSystemRes system) {
        this.system = system;
    }

    public void setPhysicalEntities(List<BDPhysicalEntityRes> physicalEntities) {
        this.physicalEntities = physicalEntities;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof BDProductPortAssetSystemRes)) return false;
        final BDProductPortAssetSystemRes other = (BDProductPortAssetSystemRes) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$system = this.getSystem();
        final Object other$system = other.getSystem();
        if (this$system == null ? other$system != null : !this$system.equals(other$system)) return false;
        final Object this$physicalEntities = this.getPhysicalEntities();
        final Object other$physicalEntities = other.getPhysicalEntities();
        if (this$physicalEntities == null ? other$physicalEntities != null : !this$physicalEntities.equals(other$physicalEntities))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BDProductPortAssetSystemRes;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $system = this.getSystem();
        result = result * PRIME + ($system == null ? 43 : $system.hashCode());
        final Object $physicalEntities = this.getPhysicalEntities();
        result = result * PRIME + ($physicalEntities == null ? 43 : $physicalEntities.hashCode());
        return result;
    }

    public String toString() {
        return "BDProductPortAssetSystemRes(system=" + this.getSystem() + ", physicalEntities=" + this.getPhysicalEntities() + ")";
    }
}

