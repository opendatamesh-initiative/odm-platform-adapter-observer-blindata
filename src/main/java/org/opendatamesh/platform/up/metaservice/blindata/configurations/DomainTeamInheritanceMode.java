package org.opendatamesh.platform.up.metaservice.blindata.configurations;

import java.util.Locale;

public enum DomainTeamInheritanceMode {
    OFF,
    IF_MISSING,
    ALWAYS;

    public static DomainTeamInheritanceMode parse(String value) {
        if (value == null || value.trim().isEmpty()) {
            return OFF;
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT).replace('-', '_');
        if ("FALSE".equals(normalized)) {
            return OFF;
        }
        for (DomainTeamInheritanceMode mode : values()) {
            if (mode.name().equals(normalized)) {
                return mode;
            }
        }
        throw new IllegalArgumentException(String.format(
                "Invalid value '%s' for blindata.dataProducts.domainTeamInheritance. Accepted values: OFF, IF_MISSING, ALWAYS", value));
    }
}
