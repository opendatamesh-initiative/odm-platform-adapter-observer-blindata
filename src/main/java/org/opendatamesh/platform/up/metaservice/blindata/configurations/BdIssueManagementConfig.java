package org.opendatamesh.platform.up.metaservice.blindata.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BdIssueManagementConfig {
    @Value("${blindata.issueManagement.policies.active:true}")
    private boolean issuePoliciesActive;

    public boolean isIssuePoliciesActive() {
        return issuePoliciesActive;
    }
}

