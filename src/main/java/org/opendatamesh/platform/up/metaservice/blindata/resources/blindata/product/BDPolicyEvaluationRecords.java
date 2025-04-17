package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product;

import java.util.ArrayList;
import java.util.List;

public class BDPolicyEvaluationRecords {
    List<BDPolicyEvaluationRecord> records = new ArrayList<>();

    public List<BDPolicyEvaluationRecord> getRecords() {
        return records;
    }

    public void setRecords(List<BDPolicyEvaluationRecord> records) {
        this.records = records;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof BDPolicyEvaluationRecords)) return false;
        final BDPolicyEvaluationRecords other = (BDPolicyEvaluationRecords) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$records = this.getRecords();
        final Object other$records = other.getRecords();
        if (this$records == null ? other$records != null : !this$records.equals(other$records)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BDPolicyEvaluationRecords;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $records = this.getRecords();
        result = result * PRIME + ($records == null ? 43 : $records.hashCode());
        return result;
    }
}
