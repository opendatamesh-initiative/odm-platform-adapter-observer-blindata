package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
public class BDPolicyEvaluationRecords {
    List<BDPolicyEvaluationRecord> records = new ArrayList<>();

    public List<BDPolicyEvaluationRecord> getRecords() {
        return records;
    }

    public void setRecords(List<BDPolicyEvaluationRecord> records) {
        this.records = records;
    }

}
