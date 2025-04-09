package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata;

import java.util.List;

public class BDProductPortAssetsRes {

    private List<BDDataProductPortAssetDetailRes> ports;

    public BDProductPortAssetsRes() {
    }

    public List<BDDataProductPortAssetDetailRes> getPorts() {
        return this.ports;
    }

    public void setPorts(List<BDDataProductPortAssetDetailRes> ports) {
        this.ports = ports;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof BDProductPortAssetsRes)) return false;
        final BDProductPortAssetsRes other = (BDProductPortAssetsRes) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$ports = this.getPorts();
        final Object other$ports = other.getPorts();
        if (this$ports == null ? other$ports != null : !this$ports.equals(other$ports)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BDProductPortAssetsRes;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $ports = this.getPorts();
        result = result * PRIME + ($ports == null ? 43 : $ports.hashCode());
        return result;
    }

    public String toString() {
        return "BDProductPortAssetsRes(ports=" + this.getPorts() + ")";
    }
}
