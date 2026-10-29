package org.opendatamesh.platform.up.metaservice.blindata.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class BdDataProductConfig {
    @Value("${blindata.dataProducts.assetsCleanup:true}")
    private boolean assetsCleanup;

    @Value("${blindata.dataProducts.additionalPropertiesRegex:}")
    private String additionalPropertiesRegex;

    @Value("${blindata.dataProducts.domainTeamInheritance:OFF}")
    private String domainTeamInheritance;

    private DomainTeamInheritanceMode domainTeamInheritanceMode = DomainTeamInheritanceMode.OFF;

    @Value("${blindata.dataProducts.missingDomainBlocksPublication:false}")
    private boolean missingDomainBlocksPublication;

    @PostConstruct
    void parseDomainTeamInheritance() {
        try {
            domainTeamInheritanceMode = DomainTeamInheritanceMode.parse(domainTeamInheritance);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public DomainTeamInheritanceMode getDomainTeamInheritanceMode() {
        return domainTeamInheritanceMode;
    }

    public boolean isMissingDomainBlocksPublication() {
        return missingDomainBlocksPublication;
    }

    public boolean isAssetsCleanup() {
        return assetsCleanup;
    }

    public String getAdditionalPropertiesRegex() {
        return additionalPropertiesRegex;
    }
}

