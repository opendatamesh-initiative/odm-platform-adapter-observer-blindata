package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.policies_align;

import com.google.common.collect.Lists;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdGovernancePolicyClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdGovernancePolicyImplementationClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdGovernancePolicySuiteClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.*;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

class PoliciesAlignBlindataOutboundPortImpl implements PoliciesAlignBlindataOutboundPort {

    private final BdGovernancePolicySuiteClient suiteClient;
    private final BdGovernancePolicyClient policyClient;
    private final BdGovernancePolicyImplementationClient policyImplementationClient;

    PoliciesAlignBlindataOutboundPortImpl(BdGovernancePolicySuiteClient suiteClient, BdGovernancePolicyClient policyClient, BdGovernancePolicyImplementationClient policyImplementationClient) {
        this.suiteClient = suiteClient;
        this.policyClient = policyClient;
        this.policyImplementationClient = policyImplementationClient;
    }

    @Override
    public Optional<BDPolicySuiteRes> findPolicySuite(String suiteName) {
        BDPolicySuiteSearchOptions filters = new BDPolicySuiteSearchOptions();
        filters.setName(suiteName);
        return suiteClient.getPolicySuites(filters, Pageable.ofSize(1)).stream().findFirst();
    }

    @Override
    public BDPolicySuiteRes createPolicySuite(BDPolicySuiteRes extractedSuite) {
        extractedSuite.setUuid(null);
        return suiteClient.createPolicySuite(extractedSuite);
    }

    @Override
    public Optional<BDPolicyRes> findPolicy(String suiteUuid, String policyName) {
        BDPoliciesSearchOptions filters = new BDPoliciesSearchOptions();
        filters.setName(policyName);
        filters.setPolicySuiteUuids(Lists.newArrayList(suiteUuid));
        return policyClient.getPolicies(filters, Pageable.ofSize(1)).stream().findFirst();
    }

    @Override
    public BDPolicyRes createPolicy(BDPolicySuiteRes suite, BDPolicyRes extractedPolicy) {
        extractedPolicy.setGovernancePolicySuite(suite);
        extractedPolicy.setUuid(null);
        return policyClient.createPolicy(extractedPolicy);
    }

    @Override
    public Optional<BDPolicyImplementationRes> findPolicyImplementation(String implementationName) {
        BDPolicyImplementationsSearchOptions filters = new BDPolicyImplementationsSearchOptions();
        filters.setImplementationName(implementationName);
        return policyImplementationClient.getPolicyImplementations(filters, Pageable.ofSize(1)).stream().findFirst();
    }

    @Override
    public BDPolicyImplementationRes createPolicyImplementation(BDPolicyRes policy, BDPolicyImplementationRes extractedPolicyImplementation) {
        extractedPolicyImplementation.setGovernancePolicy(policy);
        return policyImplementationClient.createPolicyImplementation(extractedPolicyImplementation);
    }

    @Override
    public BDPolicySuiteRes updatePolicySuite(String uuid, BDPolicySuiteRes extractedSuite) {
        extractedSuite.setUuid(uuid);
        return suiteClient.putPolicySuite(extractedSuite);
    }

    @Override
    public BDPolicyRes updatePolicy(String uuid, BDPolicyRes extractedPolicy) {
        extractedPolicy.setUuid(uuid);
        return policyClient.putPolicyRes(extractedPolicy);
    }

    @Override
    public BDPolicyImplementationRes updatePolicyImplementation(String uuid, BDPolicyRes policy, BDPolicyImplementationRes extractedPolicyImplementation) {
        extractedPolicyImplementation.setGovernancePolicy(policy);
        extractedPolicyImplementation.setUuid(uuid);
        return policyImplementationClient.putPolicyImplementation(extractedPolicyImplementation);
    }

    @Override
    public void deletePolicyImplementation(String uuid) {
        policyImplementationClient.deletePolicyImplementation(uuid);
    }

}
