package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.policies_align;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicyImplementationRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicyRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicySuiteRes;

import java.util.Optional;

interface PoliciesAlignBlindataOutboundPort {

    Optional<BDPolicySuiteRes> findPolicySuite(String suiteName);

    BDPolicySuiteRes createPolicySuite(BDPolicySuiteRes extractedSuite);

    Optional<BDPolicyRes> findPolicy(String suiteUuid, String policyName);

    BDPolicyRes createPolicy(BDPolicySuiteRes suite, BDPolicyRes extractedPolicy);

    Optional<BDPolicyImplementationRes> findPolicyImplementation(String implementationName);

    BDPolicyImplementationRes createPolicyImplementation(BDPolicyRes policy, BDPolicyImplementationRes extractedPolicyImplementation);

    BDPolicySuiteRes updatePolicySuite(String uuid, BDPolicySuiteRes extractedSuite);

    BDPolicyRes updatePolicy(String uuid, BDPolicyRes extractedPolicy);

    BDPolicyImplementationRes updatePolicyImplementation(String uuid, BDPolicyRes policy, BDPolicyImplementationRes extractedPolicyImplementation);

    void deletePolicyImplementation(String uuid);
}
