package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.policies_align;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyResource;

interface PoliciesAlignOdmOutboundPort {

    OdmPolicyResource getPolicy();
}
