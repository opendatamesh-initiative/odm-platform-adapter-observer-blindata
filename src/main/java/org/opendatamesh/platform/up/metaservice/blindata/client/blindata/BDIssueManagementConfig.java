package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BDIssueManagementConfig {
    @Value("${blindata.issueManagement.policies.active:true}")
    private boolean issuePoliciesActive;

    public boolean isIssuePoliciesActive() {
        return issuePoliciesActive;
    }
}

