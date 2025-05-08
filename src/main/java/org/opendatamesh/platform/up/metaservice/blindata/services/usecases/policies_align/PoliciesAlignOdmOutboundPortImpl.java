package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.policies_align;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyResource;

class PoliciesAlignOdmOutboundPortImpl implements PoliciesAlignOdmOutboundPort {
    private final OdmPolicyResource policyResource;

    PoliciesAlignOdmOutboundPortImpl(OdmPolicyResource policyResource) {
        this.policyResource = policyResource;
    }

    @Override
    public OdmPolicyResource getPolicy() {
        return policyResource;
    }
}
