package org.opendatamesh.platform.up.metaservice.blindata.validator.controllers;

import com.google.common.io.Resources;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.up.metaservice.blindata.ObserverBlindataAppIT;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdProbesClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdQualityClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdSemanticLinkingClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdStewardshipClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdSystemClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdUserClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.jackson.PageUtility;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDShortUserRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDStewardshipRoleRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDSystemRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.notificationevents.BlindataProperties;
import org.opendatamesh.platform.up.metaservice.blindata.validator.resources.OdmValidatorPolicyEvaluationRequestRes;
import org.opendatamesh.platform.up.metaservice.blindata.validator.resources.OdmValidatorPolicyEvaluationResultRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * PROBES_UPLOAD is optional: when no event handler activates it, the validator must not evaluate probe requirements,
 * otherwise it would block the publish of data products for a use case the deployment never opted into.
 */
@TestPropertySource(properties = {
        "blindata.event-handlers[0].event-type=DATA_PRODUCT_VERSION_CREATED",
        "blindata.event-handlers[0].filter=",
        "blindata.event-handlers[0].active-use-cases[0]=DATA_PRODUCT_UPLOAD",
        "blindata.event-handlers[0].active-use-cases[1]=DATA_PRODUCT_VERSION_UPLOAD",
        "blindata.event-handlers[0].active-use-cases[2]=QUALITY_UPLOAD"
})
class BlindataValidatorProbesUploadDisabledIT extends ObserverBlindataAppIT {

    @MockBean
    private BdDataProductClient bdDataProductClient;
    @MockBean
    private BdStewardshipClient bdStewardshipClient;
    @MockBean
    private BdUserClient bdUserClient;
    @MockBean
    private BdSemanticLinkingClient bdSemanticLinkingClient;
    @MockBean
    private BdSystemClient bdSystemClient;
    @MockBean
    private BdProbesClient bdProbesClient;
    @MockBean
    private BdQualityClient bdQualityClient;

    @Autowired
    private BlindataProperties blindataProperties;

    @Test
    public void testProbesAreNotValidatedWhenProbesUploadIsNotActive() throws IOException {
        Assertions.assertThat(blindataProperties.getEventHandlers())
                .as("PROBES_UPLOAD must not be active in this context")
                .noneSatisfy(eventHandler ->
                        Assertions.assertThat(eventHandler.getActiveUseCases()).contains("PROBES_UPLOAD"));

        OdmValidatorPolicyEvaluationRequestRes request = mapper.readValue(
                Resources.toByteArray(BlindataValidatorControllerIT.class.getResource("valid_data_product_version_with_probes.json")),
                OdmValidatorPolicyEvaluationRequestRes.class
        );

        BDShortUserRes owner = new BDShortUserRes();
        owner.setUsername("owner@default.blindata.io");
        owner.setFullName("owner@default.blindata.io");
        when(bdUserClient.getBlindataUser(any())).thenReturn(Optional.of(owner));

        BDStewardshipRoleRes role = new BDStewardshipRoleRes();
        role.setUuid("test-role-uuid");
        role.setName("test-role-name");
        when(bdStewardshipClient.getRole(any())).thenReturn(role);

        BDDataProductRes existingDataProduct = new BDDataProductRes();
        existingDataProduct.setUuid("dp-uuid");
        existingDataProduct.setName("test1");
        existingDataProduct.setIdentifier("urn:dpds:qualityDemo:dataproducts:test1:1");
        existingDataProduct.setVersion("1.0.0");
        existingDataProduct.setDomain("test");
        when(bdDataProductClient.getDataProduct(any())).thenReturn(Optional.of(existingDataProduct));

        BDSystemRes system = new BDSystemRes();
        system.setName("TestSystem");
        lenient().when(bdSystemClient.getSystem(any())).thenReturn(Optional.of(system));
        lenient().when(bdSemanticLinkingClient.getLogicalNamespaceByPrefix(any())).thenReturn(Optional.empty());
        lenient().when(bdQualityClient.getQualitySuites(any(), any())).thenReturn(new PageUtility<>(Collections.emptyList()));
        lenient().when(bdQualityClient.getQualityChecks(any(), any())).thenReturn(new PageUtility<>(Collections.emptyList()));

        ResponseEntity<OdmValidatorPolicyEvaluationResultRes> response = rest.postForEntity(
                "http://localhost:" + port + "/api/v1/up/validator/evaluate-policy",
                request,
                OdmValidatorPolicyEvaluationResultRes.class
        );

        verify(bdProbesClient, never()).getConnections(any(), any());
        verify(bdProbesClient, never()).getProjects(any(), any());
        verify(bdProbesClient, never()).createDefinition(any());

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getOutputObject().getRawError().toString())
                .doesNotContain("[ProbesUpload]")
                .doesNotContain("[#201]")
                .doesNotContain("[#204]")
                .doesNotContain("[#205]");
        Assertions.assertThat(response.getBody().getEvaluationResult())
                .as("the unknown probe connection must not block the publish")
                .isTrue();
    }
}
