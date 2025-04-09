package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata;

import java.util.List;

public class BDDataProductPortAssetDetailRes {

    private String portIdentifier;
    private List<BDProductPortAssetSystemRes> assets;

    public BDDataProductPortAssetDetailRes() {
    }

    public String getPortIdentifier() {
        return this.portIdentifier;
    }

    public List<BDProductPortAssetSystemRes> getAssets() {
        return this.assets;
    }

    public void setPortIdentifier(String portIdentifier) {
        this.portIdentifier = portIdentifier;
    }

    public void setAssets(List<BDProductPortAssetSystemRes> assets) {
        this.assets = assets;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof BDDataProductPortAssetDetailRes)) return false;
        final BDDataProductPortAssetDetailRes other = (BDDataProductPortAssetDetailRes) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$portIdentifier = this.getPortIdentifier();
        final Object other$portIdentifier = other.getPortIdentifier();
        if (this$portIdentifier == null ? other$portIdentifier != null : !this$portIdentifier.equals(other$portIdentifier))
            return false;
        final Object this$assets = this.getAssets();
        final Object other$assets = other.getAssets();
        if (this$assets == null ? other$assets != null : !this$assets.equals(other$assets)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BDDataProductPortAssetDetailRes;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $portIdentifier = this.getPortIdentifier();
        result = result * PRIME + ($portIdentifier == null ? 43 : $portIdentifier.hashCode());
        final Object $assets = this.getAssets();
        result = result * PRIME + ($assets == null ? 43 : $assets.hashCode());
        return result;
    }

    public String toString() {
        return "BDDataProductPortAssetDetailRes(portIdentifier=" + this.getPortIdentifier() + ", assets=" + this.getAssets() + ")";
    }
}

