package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPoliciesSearchOptions;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicyRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BDGovernancePolicyClient {

    Page<BDPolicyRes> getPolicies(BDPoliciesSearchOptions filters, Pageable pageable);

    BDPolicyRes createPolicy(BDPolicyRes policy);

    BDPolicyRes putPolicyRes(BDPolicyRes policy);

    void deletePolicy(String policyUuid);
}
