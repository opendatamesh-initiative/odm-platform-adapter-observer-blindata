package org.opendatamesh.platform.up.metaservice.blindata;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opendatamesh.platform.pp.notification.api.clients.EventNotificationClient;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.enums.EventNotificationStatus;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyEvaluationResultClient;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDPolicyEvaluationResultClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDStewardshipClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDUserClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.odm.OdmRegistryClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class ConsumeIT extends ODMObserverBlindataAppIT {

    @MockBean
    private EventNotificationClient notificationClient;

    @MockBean
    private BDDataProductClient bdDataProductClient;

    @MockBean
    private BDUserClient bdUserClient;

    @MockBean
    private BDStewardshipClient bdStewardshipClient;

    @MockBean
    private BDPolicyEvaluationResultClient bdPolicyEvaluationResultClient;

    @MockBean
    private PolicyEvaluationResultClient odmPolicyEvaluationResultsClient;

    @MockBean
    private OdmRegistryClient odmRegistryClient;

    // ----------------------------------------
    // POST Notification - Consume
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testNotificationCreate_DataProductCreated() throws IOException {

        EventNotificationResource notificationResource = createNotificationResource(
                ODMObserverBlindataResources.NOTIFICATION_1
        );

        Mockito.when(notificationClient.updateEventNotification(Mockito.any(), Mockito.any()))
                .thenAnswer(invocation -> invocation.getArgument(1));

        ResponseEntity<ObjectNode> consumeResponse = consumeClient.consumeEventNotificationResponseEntity(notificationResource);
        notificationResource = objectMapper.convertValue(consumeResponse.getBody(), EventNotificationResource.class);

        Mockito.verify(notificationClient, Mockito.times(2)).updateEventNotification(Mockito.any(), Mockito.any());
        assertThat(notificationResource.getStatus()).isEqualTo(EventNotificationStatus.UNPROCESSABLE);

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testNotificationCreate_DataProductVersionCreated() throws IOException {

        BDShortUserRes bdUser = new BDShortUserRes();
        BDStewardshipRoleRes bdRole = new BDStewardshipRoleRes();
        bdRole.setUuid("BlindataIT.role.uuid");

        Mockito.when(bdUserClient.getBlindataUser(Mockito.any()))
                .thenReturn(Optional.of(bdUser));
        Mockito.when(bdStewardshipClient.getRole(Mockito.any()))
                .thenReturn(bdRole);

        EventNotificationResource notificationResource = createNotificationResource(
                ODMObserverBlindataResources.NOTIFICATION_2
        );

        Page<BDDataProductRes> emptyBdDataProductResPage = new PageImpl<>(new ArrayList<>());
        BDSystemRes bdSystemRes = new BDSystemRes();
        BDPhysicalEntityRes bdPhysicalEntityRes = new BDPhysicalEntityRes();

        BDProductPortAssetSystemRes bdProductPortAssetSystemRes = new BDProductPortAssetSystemRes();
        bdProductPortAssetSystemRes.setSystem(bdSystemRes);
        bdProductPortAssetSystemRes.setPhysicalEntities(Lists.newArrayList(bdPhysicalEntityRes));

        BDProductPortAssetsRes bdProductPortAssetsRes = new BDProductPortAssetsRes();

        Mockito.when(bdDataProductClient.getDataProduct(Mockito.any()))
                .thenReturn(Optional.empty());
        Mockito.when(bdDataProductClient.getDataProducts(Mockito.any(), Mockito.any()))
                .thenReturn(emptyBdDataProductResPage);
        Mockito.when(bdDataProductClient.createDataProduct(Mockito.any()))
                .thenReturn(new BDDataProductRes());
        Mockito.doNothing().when(bdDataProductClient).deleteDataProduct(Mockito.any());
        Mockito.when(bdDataProductClient.createDataProductAssets(Mockito.any()))
                .thenReturn(bdProductPortAssetsRes);

        BDStewardshipResponsibilityRes bdResponsibility = new BDStewardshipResponsibilityRes();
        bdResponsibility.setUser(bdUser);
        bdResponsibility.setResourceIdentifier("abc123");
        bdResponsibility.setResourceType(BDResourceType.DATA_PRODUCT);
        bdResponsibility.setStewardshipRole(bdRole);
        bdResponsibility.setResourceName("abc");

        Mockito.when(bdStewardshipClient.createResponsibility(Mockito.any()))
                .thenReturn(bdResponsibility);
        Mockito.when(bdStewardshipClient.getResponsibility(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(bdResponsibility));

        BDUploadResultsMessage uploadResultsMessage = new BDUploadResultsMessage();
        uploadResultsMessage.setRowCreated(1);

        Mockito.when(bdPolicyEvaluationResultClient.createPolicyEvaluationRecords(Mockito.any()))
                .thenReturn(uploadResultsMessage);

        Mockito.when(odmRegistryClient.getSchemasId(Mockito.any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(odmRegistryClient.getSchemaContent(Mockito.any()))
                .thenReturn(null);

        Page<PolicyEvaluationResultResource> emptyPolicyEvaluationResults = new PageImpl<>(new ArrayList<>());
        Mockito.when(odmPolicyEvaluationResultsClient.getPolicyEvaluationResults(Mockito.any(), Mockito.any()))
                .thenReturn(emptyPolicyEvaluationResults);

        Mockito.when(notificationClient.updateEventNotification(Mockito.any(), Mockito.any()))
                .thenAnswer(invocation -> invocation.getArgument(1));

        Mockito.verify(notificationClient, Mockito.times(2));

        ResponseEntity<ObjectNode> consumeResponse = consumeClient.consumeEventNotificationResponseEntity(notificationResource);
        notificationResource = objectMapper.convertValue(consumeResponse.getBody(), EventNotificationResource.class);

        Mockito.verify(notificationClient, Mockito.times(2)).updateEventNotification(Mockito.any(), Mockito.any());
        assertThat(notificationResource.getStatus()).isEqualTo(EventNotificationStatus.PROCESSED);

    }

}