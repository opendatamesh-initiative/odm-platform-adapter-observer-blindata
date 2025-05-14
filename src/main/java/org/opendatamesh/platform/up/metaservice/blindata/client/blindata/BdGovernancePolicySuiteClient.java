package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicySuiteRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicySuiteSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BdGovernancePolicySuiteClient {

    Page<BDPolicySuiteRes> getPolicySuites(BDPolicySuiteSearchOptions filters, Pageable pageable);

    BDPolicySuiteRes createPolicySuite(BDPolicySuiteRes policySuite);

    BDPolicySuiteRes putPolicySuite(BDPolicySuiteRes policySuite);

    void deletePolicySuite(String policySuiteUuid);
}
