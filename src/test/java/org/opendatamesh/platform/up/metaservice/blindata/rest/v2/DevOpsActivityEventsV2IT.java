package org.opendatamesh.platform.up.metaservice.blindata.rest.v2;

import com.google.common.io.Resources;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.opendatamesh.platform.up.metaservice.blindata.ObserverBlindataAppIT;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdIssueCampaignClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdPolicyEvaluationResultClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdProbesClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdQualityClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdUserClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.odm.OdmEventNotificationClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.odm.OdmPolicyEvaluationResultClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.odm.OdmRegistryClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductPortRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductStagesUploadRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicyEvaluationRecords;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDPolicyResultsUploadResultsRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDProductPortAssetsRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityCheckRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityUploadRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityUploadResultsRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes.BDQualityProbesConnectionRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes.BDQualityProbesDefinitionRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes.BDQualityProbesProjectRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.probes.BDQualityProbesTagRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmEventNotificationResourceV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmEventNotificationStatusV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEvaluationResultResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyEvaluationResultSearchOptions;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.policy.OdmPolicyResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.v2.OdmDataProductVersionResourceV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * DevOps 2.0 activity outcomes ({@code ACTIVITY_SUCCEEDED}, {@code ACTIVITY_FAILED}) received on the V2 consume
 * endpoint, and the events the observer does not handle. Generated from the BDMD-5558 REASONS prompt
 * ({@code spdd/prompt/BDMD-5558-202611041232-[Feat]-service-devops-v2-events-handling.md}).
 * <p>
 * The handlers come from the shared {@code application-test.yml}; every client is a shared Mockito mock.
 */
public class DevOpsActivityEventsV2IT extends ObserverBlindataAppIT {

    private static final String DPV_UUID = "dpv-uuid-1";
    private static final String FQN = "urn:org.opendatamesh:dataproducts:tripExecution";
    private static final String INFO_ID = "2f9b1c3e-0000-4000-8000-000000000001";
    // The output port of the Registry fixture, with one datastoreapi table, one library quality check and a probe connection
    private static final String PORT_FQN = "urn:org.opendatamesh:dataproducts:tripExecution:outputPorts:trips";
    private static final String QUALITY_CHECK = "trips_row_count_positive";
    private static final String PROBE_CONNECTION = "TripsConnection";
    private static final String PROBE_PROJECT_UUID = "probe-project-uuid";

    @Autowired
    private OdmRegistryClient odmRegistryClient;
    @Autowired
    private OdmEventNotificationClient odmEventNotificationClient;
    @Autowired
    private BdDataProductClient bdDataProductClient;
    @Autowired
    private OdmPolicyEvaluationResultClient odmPolicyEvaluationResultClient;
    @Autowired
    private BdPolicyEvaluationResultClient bdPolicyEvaluationResultClient;
    @Autowired
    private BdQualityClient bdQualityClient;
    @Autowired
    private BdProbesClient bdProbesClient;
    @Autowired
    private BdIssueCampaignClient bdIssueCampaignClient;
    @Autowired
    private BdUserClient bdUserClient;

    @BeforeEach
    public void setUpMocks() throws IOException {
        resetMocks();

        // a fresh instance per read, so each captured patch keeps the state it was sent with
        when(bdDataProductClient.getDataProduct(any())).thenAnswer(invocation -> Optional.of(existingProduct()));
        when(bdDataProductClient.patchDataProduct(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(bdDataProductClient.createDataProductAssets(any())).thenAnswer(invocation -> invocation.getArgument(0));

        OdmDataProductVersionResourceV2 registryVersion = mapper.readValue(
                Resources.toByteArray(getClass().getResource("registry_dataProductVersion_dpv-uuid-1.json")),
                OdmDataProductVersionResourceV2.class);
        when(odmRegistryClient.getDataProductVersionV2(DPV_UUID)).thenReturn(Optional.of(registryVersion));

        when(odmPolicyEvaluationResultClient.getPolicyEvaluationResults(any(), any())).thenReturn(new PageImpl<>(List.of()));
        when(bdPolicyEvaluationResultClient.createPolicyEvaluationRecords(any())).thenReturn(new BDPolicyResultsUploadResultsRes());

        stubQualityAndProbesClients();
    }

    @AfterEach
    public void resetMocksAfterTest() {
        resetMocks();
    }

    private void resetMocks() {
        Mockito.reset(odmRegistryClient, odmEventNotificationClient, bdDataProductClient, odmPolicyEvaluationResultClient,
                bdPolicyEvaluationResultClient, bdQualityClient, bdProbesClient, bdIssueCampaignClient, bdUserClient);
    }

    private BDDataProductRes existingProduct() {
        BDDataProductRes existingProduct = new BDDataProductRes();
        existingProduct.setIdentifier(FQN);
        existingProduct.setName("tripExecution");
        existingProduct.setUuid("bd-product-uuid");
        return existingProduct;
    }

    private void stubQualityAndProbesClients() {
        when(bdQualityClient.getQualitySuites(any(), any())).thenReturn(new PageImpl<>(List.of()));
        when(bdQualityClient.uploadQuality(any())).thenReturn(new BDQualityUploadResultsRes());
        when(bdIssueCampaignClient.getIssueCampaign(any())).thenReturn(Optional.empty());
        when(bdIssueCampaignClient.createCampaign(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(bdUserClient.getBlindataUser(any())).thenReturn(Optional.empty());

        BDQualityProbesConnectionRes connection = new BDQualityProbesConnectionRes();
        connection.setName(PROBE_CONNECTION);
        connection.setType("postgresql");
        when(bdProbesClient.getConnections(any(), any())).thenReturn(new PageImpl<>(List.of(connection)));
        when(bdProbesClient.getProjects(any(), any())).thenReturn(new PageImpl<>(List.of()));
        when(bdProbesClient.createProject(any())).thenAnswer(invocation -> {
            BDQualityProbesProjectRes project = invocation.getArgument(0);
            project.setUuid(PROBE_PROJECT_UUID);
            return project;
        });
        when(bdProbesClient.getDefinitions(any(), any())).thenReturn(new PageImpl<>(List.of()));
        when(bdProbesClient.createDefinition(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(bdProbesClient.getTags(any(), any())).thenReturn(new PageImpl<>(List.of()));
        when(bdProbesClient.createTag(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    /**
     * <pre>
     * Scenario: A successful activity uploads its stage and the policy results
     *   Given the data product exists on Blindata
     *   And Registry V2 holds the data product version with version number "1.0.0" and git tag "v1.0.0-release"
     *   When the observer receives ACTIVITY_SUCCEEDED for the activity "dev" of that version
     *   Then the stage "dev" with version "1.0.0" is uploaded to Blindata
     *   And the policy evaluation results are searched by the descriptor id and version "1.0.0"
     *   And the notification is closed as PROCESSED
     * </pre>
     */
    @Test
    public void whenActivitySucceededThenStageAndPoliciesAreUploaded() throws IOException {
        stubOnePolicyResult();

        postAndExpectProcessing("activitySucceeded_dev_eventNotification.json");

        assertStageAndPoliciesUploaded();
        assertFinalStatus(OdmEventNotificationStatusV2.PROCESSED);
    }

    /**
     * <pre>
     * Scenario: The version is read from Registry once per event, whatever the number of use cases
     *   Given the data product exists on Blindata
     *   And the handlers for the stage "prod" run six use cases on ACTIVITY_SUCCEEDED
     *   When the observer receives ACTIVITY_SUCCEEDED for the activity "prod"
     *   Then the data product, its ports, quality, probes, stage and policies are uploaded
     *   And Registry V2 is called exactly once for that event
     *   And the notification is closed as PROCESSED
     * </pre>
     */
    @Test
    public void whenProdActivitySucceededThenFullUploadWithSingleRegistryRead() throws IOException {
        stubOnePolicyResult();

        postAndExpectProcessing("activitySucceeded_prod_eventNotification.json");

        // DATA_PRODUCT_UPLOAD (update) + DATA_PRODUCT_VERSION_UPLOAD (ports and version number)
        verify(bdDataProductClient, Mockito.atLeast(2)).patchDataProduct(any());
        assertPortsQualityAndProbesUploaded();
        ArgumentCaptor<BDDataProductStagesUploadRes> stages = ArgumentCaptor.forClass(BDDataProductStagesUploadRes.class);
        verify(bdDataProductClient, times(1)).uploadStages(stages.capture());
        assertThat(stages.getValue().getStages()).hasSize(1);
        assertThat(stages.getValue().getStages().get(0).getName()).isEqualTo("prod");
        assertThat(stages.getValue().getStages().get(0).getVersion()).isEqualTo("1.0.0");
        verify(bdPolicyEvaluationResultClient, times(1)).createPolicyEvaluationRecords(any());
        verify(odmRegistryClient, times(1)).getDataProductVersionV2(DPV_UUID);
        assertFinalStatus(OdmEventNotificationStatusV2.PROCESSED);
    }

    /**
     * <pre>
     * Scenario: The execution mode does not change what the observer does
     *   Given the data product exists on Blindata
     *   When the observer receives ACTIVITY_SUCCEEDED for an activity whose tasks are all instrumented
     *   Then the stage and the policy results are uploaded as for a full-control activity
     * </pre>
     */
    @Test
    public void whenInstrumentedActivitySucceededThenSameOutcomeAsFullControl() throws IOException {
        stubOnePolicyResult();

        postAndExpectProcessing("activitySucceeded_instrumented_eventNotification.json");

        assertStageAndPoliciesUploaded();
        assertFinalStatus(OdmEventNotificationStatusV2.PROCESSED);
    }

    /**
     * <pre>
     * Scenario: A failed activity uploads the policy results and no stage
     *   Given the data product exists on Blindata
     *   When the observer receives ACTIVITY_FAILED for the activity "dev"
     *   Then the policy evaluation results are uploaded to Blindata
     *   And no stage is uploaded
     *   And the notification is closed as PROCESSED
     * </pre>
     */
    @Test
    public void whenActivityFailedThenPoliciesUploadedAndNoStage() throws IOException {
        stubOnePolicyResult();

        postAndExpectProcessing("activityFailed_dev_eventNotification.json");

        verify(bdPolicyEvaluationResultClient, times(1)).createPolicyEvaluationRecords(any());
        verify(bdDataProductClient, never()).uploadStages(any());
        assertFinalStatus(OdmEventNotificationStatusV2.PROCESSED);
    }

    /**
     * <pre>
     * Scenario: A failed production activity runs the full upload except stages
     *   Given the data product exists on Blindata
     *   When the observer receives ACTIVITY_FAILED for the activity "prod"
     *   Then the data product, its ports, quality, probes and policies are uploaded
     *   And no stage is uploaded
     * </pre>
     */
    @Test
    public void whenProdActivityFailedThenFullUploadWithoutStages() throws IOException {
        stubOnePolicyResult();

        postAndExpectProcessing("activityFailed_prod_eventNotification.json");

        verify(bdDataProductClient, Mockito.atLeast(2)).patchDataProduct(any());
        assertPortsQualityAndProbesUploaded();
        verify(bdPolicyEvaluationResultClient, times(1)).createPolicyEvaluationRecords(any());
        verify(bdDataProductClient, never()).uploadStages(any());
        verify(odmRegistryClient, times(1)).getDataProductVersionV2(DPV_UUID);
        assertFinalStatus(OdmEventNotificationStatusV2.PROCESSED);
    }

    /**
     * <pre>
     * Scenario: The version is missing from Registry V2
     *   Given Registry V2 does not know the data product version of the activity
     *   When the observer receives ACTIVITY_SUCCEEDED for that activity
     *   Then nothing is uploaded to Blindata
     *   And the notification is closed as FAILED_TO_PROCESS with a message naming Registry V2 and the version uuid
     * </pre>
     */
    @Test
    public void whenVersionMissingInRegistryThenFailedToProcess() throws IOException {
        when(odmRegistryClient.getDataProductVersionV2(DPV_UUID)).thenReturn(Optional.empty());

        postAndExpectProcessing("activitySucceeded_dev_eventNotification.json");

        verifyNothingUploadedToBlindata();
        OdmEventNotificationResourceV2 finalNotification = assertFinalStatus(OdmEventNotificationStatusV2.FAILED_TO_PROCESS);
        assertThat(finalNotification.getErrorMessage())
                .contains("ODM Registry V2")
                .contains(DPV_UUID);
    }

    /**
     * <pre>
     * Scenario: The data product is not on Blindata yet
     *   Given the data product does not exist on Blindata
     *   When the observer receives ACTIVITY_SUCCEEDED
     *   Then nothing is uploaded to Blindata
     *   And the notification is closed as PROCESSED
     * </pre>
     */
    @Test
    public void whenDataProductNotOnBlindataThenProcessedWithoutUpload() throws IOException {
        when(bdDataProductClient.getDataProduct(any())).thenReturn(Optional.empty());

        postAndExpectProcessing("activitySucceeded_dev_eventNotification.json");

        verifyNothingUploadedToBlindata();
        // the event was dispatched: the version was resolved and the use cases looked the product up on Blindata
        verify(odmRegistryClient, times(1)).getDataProductVersionV2(DPV_UUID);
        verify(bdDataProductClient, atLeastOnce()).getDataProduct(FQN);
        assertFinalStatus(OdmEventNotificationStatusV2.PROCESSED);
    }

    /**
     * <pre>
     * Scenario: A canceled activity is ignored
     *   When the observer receives ACTIVITY_CANCELED
     *   Then the response is 200 PROCESSING
     *   And neither Registry V2 nor Blindata is called
     *   And the notification is closed as PROCESSED
     * </pre>
     */
    @Test
    public void whenActivityCanceledThenIgnoredAndProcessed() throws IOException {
        postAndExpectProcessing("activityCanceled_eventNotification.json");

        verifyNoInteractions(odmRegistryClient, bdDataProductClient, bdPolicyEvaluationResultClient,
                odmPolicyEvaluationResultClient, bdQualityClient, bdProbesClient);
        assertFinalStatus(OdmEventNotificationStatusV2.PROCESSED);
    }

    /**
     * <pre>
     * Scenario: An unknown event type is ignored
     *   When the observer receives an event of type SOMETHING_NEW
     *   Then the response is 200 PROCESSING
     *   And neither Registry V2 nor Blindata is called
     *   And the notification is closed as PROCESSED
     * </pre>
     */
    @Test
    public void whenUnknownEventTypeThenIgnoredAndProcessed() throws IOException {
        postAndExpectProcessing("unknownType_eventNotification.json");

        verifyNoInteractions(odmRegistryClient, bdDataProductClient, bdPolicyEvaluationResultClient,
                odmPolicyEvaluationResultClient, bdQualityClient, bdProbesClient);
        assertFinalStatus(OdmEventNotificationStatusV2.PROCESSED);
    }

    /**
     * <pre>
     * Scenario: A published version is still processed from the event content
     *   When the observer receives DATA_PRODUCT_VERSION_PUBLISHED
     *   Then its use cases run from the descriptor in the event
     *   And Registry V2 is not called
     * </pre>
     */
    @Test
    public void whenVersionPublishedThenRegistryNotCalled() throws IOException {
        postAndExpectProcessing("dataProductVersionPublished_eventNotification.json");

        verify(bdDataProductClient, Mockito.atLeastOnce()).patchDataProduct(any());
        verify(odmRegistryClient, never()).getDataProductVersionV2(any());
        assertFinalStatus(OdmEventNotificationStatusV2.PROCESSED);
    }

    private void stubOnePolicyResult() {
        OdmPolicyResource policy = new OdmPolicyResource();
        policy.setName("policy-1");
        OdmPolicyEvaluationResultResource result = new OdmPolicyEvaluationResultResource();
        result.setPolicy(policy);
        result.setResult(true);
        when(odmPolicyEvaluationResultClient.getPolicyEvaluationResults(any(), any())).thenReturn(new PageImpl<>(List.of(result)));
    }

    private void postAndExpectProcessing(String fixture) throws IOException {
        OdmEventNotificationResourceV2 notification = mapper.readValue(
                Resources.toByteArray(getClass().getResource(fixture)),
                OdmEventNotificationResourceV2.class);

        ResponseEntity<OdmEventNotificationResourceV2> response = rest.postForEntity(
                apiUrlFromString(RoutesV2.CONSUME.getPath()),
                notification,
                OdmEventNotificationResourceV2.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo(OdmEventNotificationStatusV2.PROCESSING);
    }

    private OdmEventNotificationResourceV2 assertFinalStatus(OdmEventNotificationStatusV2 expected) {
        ArgumentCaptor<OdmEventNotificationResourceV2> captor = ArgumentCaptor.forClass(OdmEventNotificationResourceV2.class);
        verify(odmEventNotificationClient, times(1)).updateEventNotificationV2(eq(1L), captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(expected);
        return captor.getValue();
    }

    private void assertStageAndPoliciesUploaded() {
        ArgumentCaptor<BDDataProductStagesUploadRes> stages = ArgumentCaptor.forClass(BDDataProductStagesUploadRes.class);
        verify(bdDataProductClient, times(1)).uploadStages(stages.capture());
        assertThat(stages.getValue().getDataProductUuid()).isEqualTo("bd-product-uuid");
        assertThat(stages.getValue().getStages()).hasSize(1);
        assertThat(stages.getValue().getStages().get(0).getName()).isEqualTo("dev");
        // the descriptor info.version (Registry versionNumber), never the Registry git tag "v1.0.0-release"
        assertThat(stages.getValue().getStages().get(0).getVersion()).isEqualTo("1.0.0");

        ArgumentCaptor<OdmPolicyEvaluationResultSearchOptions> options = ArgumentCaptor.forClass(OdmPolicyEvaluationResultSearchOptions.class);
        verify(odmPolicyEvaluationResultClient, times(1)).getPolicyEvaluationResults(any(), options.capture());
        assertThat(options.getValue().getDataProductId()).isEqualTo(INFO_ID);
        assertThat(options.getValue().getDataProductVersion()).isEqualTo("1.0.0");

        ArgumentCaptor<BDPolicyEvaluationRecords> records = ArgumentCaptor.forClass(BDPolicyEvaluationRecords.class);
        verify(bdPolicyEvaluationResultClient, times(1)).createPolicyEvaluationRecords(records.capture());
        assertThat(records.getValue().getRecords()).hasSize(1);
    }

    /**
     * Proves that DATA_PRODUCT_VERSION_UPLOAD, QUALITY_UPLOAD and PROBES_UPLOAD received the descriptor resolved from
     * Registry V2: each one sends the fixture's output port, quality check or probe to Blindata.
     */
    private void assertPortsQualityAndProbesUploaded() {
        // DATA_PRODUCT_VERSION_UPLOAD: the port is patched on the product, and its datastoreapi table is uploaded as asset
        ArgumentCaptor<BDDataProductRes> patches = ArgumentCaptor.forClass(BDDataProductRes.class);
        verify(bdDataProductClient, Mockito.atLeast(2)).patchDataProduct(patches.capture());
        assertThat(patches.getAllValues())
                .filteredOn(patch -> patch.getPorts() != null && !patch.getPorts().isEmpty())
                .isNotEmpty()
                .allSatisfy(patch -> assertThat(patch.getPorts()).extracting(BDDataProductPortRes::getIdentifier).containsExactly(PORT_FQN));

        ArgumentCaptor<BDProductPortAssetsRes> assets = ArgumentCaptor.forClass(BDProductPortAssetsRes.class);
        verify(bdDataProductClient, times(1)).createDataProductAssets(assets.capture());
        assertThat(assets.getValue().getPorts()).hasSize(1);
        assertThat(assets.getValue().getPorts().get(0).getPortIdentifier()).isEqualTo(PORT_FQN);
        assertThat(assets.getValue().getPorts().get(0).getAssets())
                .flatExtracting(asset -> asset.getPhysicalEntities())
                .extracting(entity -> entity.getName())
                .contains("trips");

        // QUALITY_UPLOAD: the table quality check is uploaded
        ArgumentCaptor<BDQualityUploadRes> quality = ArgumentCaptor.forClass(BDQualityUploadRes.class);
        verify(bdQualityClient, times(1)).uploadQuality(quality.capture());
        assertThat(quality.getValue().getQualityChecks())
                .extracting(BDQualityCheckRes::getCode)
                .anySatisfy(code -> assertThat(code).contains(QUALITY_CHECK));

        // PROBES_UPLOAD: a probe project is created, with one probe definition on the port connection, and the version tag
        verify(bdProbesClient, times(1)).createProject(any());
        ArgumentCaptor<BDQualityProbesDefinitionRes> definitions = ArgumentCaptor.forClass(BDQualityProbesDefinitionRes.class);
        verify(bdProbesClient, times(1)).createDefinition(definitions.capture());
        assertThat(definitions.getValue().getName()).contains(QUALITY_CHECK);
        assertThat(definitions.getValue().getProject().getUuid()).isEqualTo(PROBE_PROJECT_UUID);
        assertThat(definitions.getValue().getQueries())
                .extracting(query -> query.getConnectionName())
                .containsExactly(PROBE_CONNECTION);
        ArgumentCaptor<BDQualityProbesTagRes> tags = ArgumentCaptor.forClass(BDQualityProbesTagRes.class);
        verify(bdProbesClient, times(1)).createTag(tags.capture());
        assertThat(tags.getValue().getName()).isEqualTo("1.0.0");
    }

    private void verifyNothingUploadedToBlindata() {
        verify(bdDataProductClient, never()).uploadStages(any());
        verify(bdDataProductClient, never()).patchDataProduct(any());
        verify(bdDataProductClient, never()).createDataProduct(any());
        verify(bdPolicyEvaluationResultClient, never()).createPolicyEvaluationRecords(any());
    }
}
