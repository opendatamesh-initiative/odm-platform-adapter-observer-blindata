package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.quality_upload;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.io.Resources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opendatamesh.platform.up.metaservice.blindata.ObserverBlindataAppIT;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdIssueCampaignClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdQualityClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdUserClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityCheckRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualitySuiteRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityUploadResultsRes;
import org.opendatamesh.platform.up.metaservice.blindata.validator.resources.OdmValidatorPolicyEvaluationRequestRes;
import org.opendatamesh.platform.up.metaservice.blindata.validator.resources.OdmValidatorPolicyEvaluationResultRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Feature: Quality check name/code conflict detection on QUALITY_UPLOAD
 * In order to avoid opaque Blindata create failures when renaming quality.name without aligning display identity
 * As the Blindata observer
 * I want to detect same-name / different-code collisions within a Quality Suite and emit clear warnings
 * So that the observer validator fails policy evaluation with remediable messages before publish
 */
public class QualityUploadNameCodeConflictIT extends ObserverBlindataAppIT {

    @Autowired
    private BdQualityClient bdQualityClient;
    @Autowired
    private BdIssueCampaignClient bdIssueCampaignClient;
    @Autowired
    private BdUserClient bdUserClient;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void resetMocks() {
        Mockito.reset(bdQualityClient, bdIssueCampaignClient, bdUserClient);
        when(bdIssueCampaignClient.getIssueCampaign(any())).thenReturn(java.util.Optional.empty());
        when(bdIssueCampaignClient.createCampaign(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(bdUserClient.getBlindataUser(any())).thenReturn(java.util.Optional.empty());
        when(bdQualityClient.uploadQuality(any())).thenReturn(new BDQualityUploadResultsRes());
    }

    /**
     * Scenario: AC6 — observer validator dry-run surfaces conflict and fails policy evaluation
     * Given the Blindata validator policy evaluation runs QUALITY_UPLOAD in dry-run
     * And Blindata already has Quality Suite "testDomain - test" with an existing same-name / different-code check
     * And the descriptor produces a conflicting incoming check
     * When BlindataValidatorService validates the data product
     * Then QualityUploadBlindataOutboundPortDryRunImpl still performs suite/check read lookups against Blindata
     * And uploadQuality is not persisted (dry-run stub)
     * And ValidatorUseCaseLogger collects the conflict warning(s)
     * And evaluationResult is false
     * And rawError lists every collected warning message
     */
    @Test
    public void testValidatorFailsWhenQualityNameCodeConflictDetected() throws IOException {
        BDQualitySuiteRes existingSuite = new BDQualitySuiteRes();
        existingSuite.setUuid("suite-uuid");
        existingSuite.setCode("testDomain - test");
        existingSuite.setName("testDomain - test");

        BDQualityCheckRes existingCheck = new BDQualityCheckRes();
        existingCheck.setCode("testDomain - test - Macrozona ExpectColumnValuesToBeInSet OLD");
        existingCheck.setName("Macrozona ExpectColumnValuesToBeInSet");

        when(bdQualityClient.getQualitySuites(any(Pageable.class), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(existingSuite)));
        when(bdQualityClient.getQualityChecks(any(Pageable.class), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(existingCheck)));

        OdmValidatorPolicyEvaluationRequestRes request = buildValidatorRequestFromQualityUploadFixture();

        ResponseEntity<OdmValidatorPolicyEvaluationResultRes> response = rest.postForEntity(
                "http://localhost:" + port + "/api/v1/up/validator/evaluate-policy",
                request,
                OdmValidatorPolicyEvaluationResultRes.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEvaluationResult()).isFalse();
        assertThat(response.getBody().getOutputObject().getRawError().toString())
                .contains("[#121]")
                .contains("Macrozona ExpectColumnValuesToBeInSet")
                .contains("customProperties.displayName")
                .contains("quality.id");

        verify(bdQualityClient, atLeastOnce()).getQualitySuites(any(Pageable.class), any());
        verify(bdQualityClient, atLeastOnce()).getQualityChecks(any(Pageable.class), any());
        verify(bdQualityClient, never()).uploadQuality(any());
    }

    /**
     * Scenario: AC5 — first upload / suite absent is a no-op for the guard (validator path)
     * Given Blindata does not contain Quality Suite "testDomain - test"
     * When the Blindata validator evaluates the data product version with quality checks
     * Then findQualitySuiteByCode returns empty
     * And no name/code conflict warning [#121] is emitted for this reason
     */
    @Test
    public void testValidatorDoesNotEmitConflictWhenSuiteAbsent() throws IOException {
        when(bdQualityClient.getQualitySuites(any(Pageable.class), any()))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        OdmValidatorPolicyEvaluationRequestRes request = buildValidatorRequestFromQualityUploadFixture();

        ResponseEntity<OdmValidatorPolicyEvaluationResultRes> response = rest.postForEntity(
                "http://localhost:" + port + "/api/v1/up/validator/evaluate-policy",
                request,
                OdmValidatorPolicyEvaluationResultRes.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        if (Boolean.FALSE.equals(response.getBody().getEvaluationResult())) {
            assertThat(response.getBody().getOutputObject().getRawError().toString()).doesNotContain("[#121]");
        }
        verify(bdQualityClient, atLeastOnce()).getQualitySuites(any(Pageable.class), any());
        verify(bdQualityClient, never()).getQualityChecks(any(Pageable.class), any());
        verify(bdQualityClient, never()).uploadQuality(any());
    }

    private OdmValidatorPolicyEvaluationRequestRes buildValidatorRequestFromQualityUploadFixture() throws IOException {
        JsonNode dataProductVersion = objectMapper.readTree(
                Resources.toByteArray(Objects.requireNonNull(
                        getClass().getResource("/org/opendatamesh/platform/up/metaservice/blindata/services/usecases/quality_upload/quality_upload_data_product_version.json")))
        );
        ObjectNode afterState = objectMapper.createObjectNode();
        afterState.set("dataProductVersion", dataProductVersion);

        ObjectNode objectToEvaluate = objectMapper.createObjectNode();
        objectToEvaluate.set("afterState", afterState);
        objectToEvaluate.putNull("currentState");

        OdmValidatorPolicyEvaluationRequestRes request = new OdmValidatorPolicyEvaluationRequestRes();
        request.setPolicyEvaluationId(5357L);
        request.setObjectToEvaluate(objectToEvaluate);
        return request;
    }
}
