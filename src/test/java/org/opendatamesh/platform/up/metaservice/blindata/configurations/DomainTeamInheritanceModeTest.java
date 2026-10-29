package org.opendatamesh.platform.up.metaservice.blindata.configurations;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DomainTeamInheritanceModeTest {

    @Test
    public void testOffValues() {
        for (String value : new String[]{null, "", "  ", "off", "OFF", "false", "FALSE"}) {
            assertThat(DomainTeamInheritanceMode.parse(value)).as(String.valueOf(value)).isEqualTo(DomainTeamInheritanceMode.OFF);
        }
    }

    @Test
    public void testIfMissingValues() {
        assertThat(DomainTeamInheritanceMode.parse("if-missing")).isEqualTo(DomainTeamInheritanceMode.IF_MISSING);
        assertThat(DomainTeamInheritanceMode.parse("IF_MISSING")).isEqualTo(DomainTeamInheritanceMode.IF_MISSING);
    }

    @Test
    public void testAlwaysValue() {
        assertThat(DomainTeamInheritanceMode.parse(" always ")).isEqualTo(DomainTeamInheritanceMode.ALWAYS);
    }

    @Test
    public void testInvalidValue() {
        assertThatThrownBy(() -> DomainTeamInheritanceMode.parse("sometimes"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("OFF, IF_MISSING, ALWAYS");
    }
}
