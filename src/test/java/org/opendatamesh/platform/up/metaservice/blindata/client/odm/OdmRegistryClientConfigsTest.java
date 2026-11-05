package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Registry client configuration for the temporary DevOps 2.0 bridge (BDMD-5558).
 */
class OdmRegistryClientConfigsTest {

    private OdmRegistryClientConfigs configs(boolean active, String apiVersion) {
        OdmRegistryClientConfigs configs = new OdmRegistryClientConfigs();
        ReflectionTestUtils.setField(configs, "active", active);
        ReflectionTestUtils.setField(configs, "address", "http://localhost:8001");
        ReflectionTestUtils.setField(configs, "apiVersion", apiVersion);
        ReflectionTestUtils.setField(configs, "restTemplateBuilder", new RestTemplateBuilder());
        return configs;
    }

    /**
     * <pre>
     * Scenario: The Registry client is disabled
     *   Given the Registry client is not active
     *   When a DevOps activity version is read
     *   Then the read fails with a message saying the Registry is disabled
     * </pre>
     */
    @Test
    void whenRegistryInactiveThenV2ReadFailsAsDisabled() {
        OdmRegistryClient client = configs(false, "v2").odmRegistryClient();

        assertThatThrownBy(() -> client.getDataProductVersionV2("dpv-uuid-1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ODM Registry client is disabled")
                .hasMessageContaining("odm.productPlane.registryService.active")
                .hasMessageContaining("dpv-uuid-1");
    }

    /**
     * <pre>
     * Scenario: The Registry client is on API v1
     *   Given the Registry client is active with apiVersion v1
     *   When a DevOps activity version is read
     *   Then the read fails with a message saying Registry V2 is not configured
     * </pre>
     */
    @Test
    void whenApiVersionV1ThenV2ReadFailsAsNotConfigured() {
        OdmRegistryClient client = configs(true, "v1").odmRegistryClient();

        assertThatThrownBy(() -> client.getDataProductVersionV2("dpv-uuid-1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ODM Registry V2 is not configured")
                .hasMessageContaining("odm.productPlane.registryService.apiVersion")
                .hasMessageContaining("dpv-uuid-1");
    }

    /**
     * <pre>
     * Scenario: An invalid Registry apiVersion stops the service
     *   Given odm.productPlane.registryService.apiVersion is "v3"
     *   When the Registry client is created
     *   Then creation fails with a message naming the key and the allowed values
     * </pre>
     */
    @Test
    void whenApiVersionInvalidThenCreationFails() {
        OdmRegistryClientConfigs configs = configs(true, "v3");

        assertThatThrownBy(configs::odmRegistryClient)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("odm.productPlane.registryService.apiVersion")
                .hasMessageContaining("'v3'")
                .hasMessageContaining("v1, v2");
    }

    @Test
    void whenApiVersionIsInvalidAndRegistryInactiveThenCreationStillFails() {
        assertThatThrownBy(() -> configs(false, "v3").odmRegistryClient())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("odm.productPlane.registryService.apiVersion");
    }

    @Test
    void whenApiVersionIsV2InAnyCaseThenItIsAccepted() {
        for (String value : new String[]{"v2", "V2", "v1", "V1"}) {
            OdmRegistryClient client = configs(true, value).odmRegistryClient();

            assertThat(client).isInstanceOf(OdmRegistryClientImpl.class);
            assertThat(ReflectionTestUtils.getField(client, "apiVersion")).isEqualTo(value.toUpperCase());
        }
    }
}
