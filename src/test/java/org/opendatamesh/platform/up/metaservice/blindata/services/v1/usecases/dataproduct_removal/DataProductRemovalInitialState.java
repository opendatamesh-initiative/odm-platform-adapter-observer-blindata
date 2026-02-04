package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.dataproduct_removal;

public class DataProductRemovalInitialState {
    private String fqn;

    public DataProductRemovalInitialState() {
    }

    public String getFqn() {
        return this.fqn;
    }

    public void setFqn(String fqn) {
        this.fqn = fqn;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof DataProductRemovalInitialState)) return false;
        final DataProductRemovalInitialState other = (DataProductRemovalInitialState) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$fqn = this.getFqn();
        final Object other$fqn = other.getFqn();
        if (this$fqn == null ? other$fqn != null : !this$fqn.equals(other$fqn)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof DataProductRemovalInitialState;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $fqn = this.getFqn();
        result = result * PRIME + ($fqn == null ? 43 : $fqn.hashCode());
        return result;
    }

    public String toString() {
        return "DataProductRemovalInitialState(fqn=" + this.getFqn() + ")";
    }
}
