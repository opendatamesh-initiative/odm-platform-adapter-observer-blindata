package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicyImplementationRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicyImplementationsSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BDGovernancePolicyImplementationClient {

    Page<BDPolicyImplementationRes> getPolicyImplementations(BDPolicyImplementationsSearchOptions filters, Pageable pageable);

    BDPolicyImplementationRes createPolicyImplementation(BDPolicyImplementationRes extractedPolicyImplementation);

    BDPolicyImplementationRes putPolicyImplementation(BDPolicyImplementationRes policyImplementation);

    void deletePolicyImplementation(String implementationUuid);
}
