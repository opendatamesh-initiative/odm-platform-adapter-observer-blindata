package org.opendatamesh.platform.up.metaservice.blindata;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
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
                ODMObserverBlindataResources.NOTIFICATION_DATA_PRODUCT_CREATED
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
    public void testNotificationCreate_DataProductUpdated() throws IOException {

        EventNotificationResource notificationResource = createNotificationResource(
                ODMObserverBlindataResources.NOTIFICATION_DATA_PRODUCT_UPDATED
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
    public void testNotificationCreate_DataProductDeleted() throws IOException {

        EventNotificationResource notificationResource = createNotificationResource(
                ODMObserverBlindataResources.NOTIFICATION_DATA_PRODUCT_DELETED
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
    public void testNotificationCreate_DataProductActivityCreated() throws IOException {

        EventNotificationResource notificationResource = createNotificationResource(
                ODMObserverBlindataResources.NOTIFICATION_DATA_PRODUCT_ACTIVITY_CREATED
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
    public void testNotificationCreate_DataProductActivityStarted() throws IOException {

        EventNotificationResource notificationResource = createNotificationResource(
                ODMObserverBlindataResources.NOTIFICATION_DATA_PRODUCT_ACTIVITY_STARTED
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
    public void testNotificationCreate_DataProductActivityCompleted() throws IOException {

        EventNotificationResource notificationResource = createNotificationResource(
                ODMObserverBlindataResources.NOTIFICATION_DATA_PRODUCT_ACTIVITY_COMPLETED
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
    public void testNotificationCreate_DataProductTaskCreated() throws IOException {

        EventNotificationResource notificationResource = createNotificationResource(
                ODMObserverBlindataResources.NOTIFICATION_DATA_PRODUCT_TASK_CREATED
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
    public void testNotificationCreate_DataProductTaskStarted() throws IOException {

        EventNotificationResource notificationResource = createNotificationResource(
                ODMObserverBlindataResources.NOTIFICATION_DATA_PRODUCT_TASK_STARTED
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
    public void testNotificationCreate_DataProductTaskCompleted() throws IOException {

        EventNotificationResource notificationResource = createNotificationResource(
                ODMObserverBlindataResources.NOTIFICATION_DATA_PRODUCT_TASK_COMPLETED
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
                ODMObserverBlindataResources.NOTIFICATION_DATA_PRODUCT_VERSION_CREATED
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

        Mockito.when(odmRegistryClient.getApi(Mockito.any(), Mockito.any()))
                .thenReturn(new ArrayList<>());

        Page<PolicyEvaluationResultResource> emptyPolicyEvaluationResults = new PageImpl<>(new ArrayList<>());
        Mockito.when(odmPolicyEvaluationResultsClient.getPolicyEvaluationResults(Mockito.any(), Mockito.any()))
                .thenReturn(emptyPolicyEvaluationResults);

        Mockito.when(notificationClient.updateEventNotification(Mockito.any(), Mockito.any()))
                .thenAnswer(invocation -> invocation.getArgument(1));

        ResponseEntity<ObjectNode> consumeResponse = consumeClient.consumeEventNotificationResponseEntity(notificationResource);
        notificationResource = objectMapper.convertValue(consumeResponse.getBody(), EventNotificationResource.class);

        Mockito.verify(notificationClient, Mockito.times(2)).updateEventNotification(Mockito.any(), Mockito.any());
        assertThat(notificationResource.getStatus()).isEqualTo(EventNotificationStatus.PROCESSED);

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testNotificationCreate_DataProductVersionCreated_WhenAlreadyExisting() throws IOException {

        BDShortUserRes bdUser = new BDShortUserRes();
        BDStewardshipRoleRes bdRole = new BDStewardshipRoleRes();
        bdRole.setUuid("BlindataIT.role.uuid");

        Mockito.when(bdUserClient.getBlindataUser(Mockito.any()))
                .thenReturn(Optional.of(bdUser));
        Mockito.when(bdStewardshipClient.getRole(Mockito.any()))
                .thenReturn(bdRole);

        EventNotificationResource oldNotificationResource = createNotificationResource(
                ODMObserverBlindataResources.NOTIFICATION_DATA_PRODUCT_VERSION_CREATED
        );

        EventNotificationResource notificationResource = createNotificationResource(
                ODMObserverBlindataResources.NOTIFICATION_DATA_PRODUCT_VERSION_CREATED_2
        );

        Page<BDDataProductRes> emptyBdDataProductResPage = new PageImpl<>(new ArrayList<>());
        BDSystemRes bdSystemRes = new BDSystemRes();
        BDPhysicalEntityRes bdPhysicalEntityRes = new BDPhysicalEntityRes();

        DataProductVersionDPDS oldDataProduct = objectMapper.readValue(oldNotificationResource.getEvent().getAfterState(), DataProductVersionDPDS.class);
        DataProductVersionDPDS newDataProduct = objectMapper.readValue(notificationResource.getEvent().getAfterState(), DataProductVersionDPDS.class);

        BDProductPortAssetSystemRes bdProductPortAssetSystemRes = new BDProductPortAssetSystemRes();
        bdProductPortAssetSystemRes.setSystem(bdSystemRes);
        bdProductPortAssetSystemRes.setPhysicalEntities(Lists.newArrayList(bdPhysicalEntityRes));

        BDProductPortAssetsRes bdProductPortAssetsRes = new BDProductPortAssetsRes();

        BDDataProductRes oldBDDataProduct = new BDDataProductRes();
        oldBDDataProduct.setUuid("abc123");
        oldBDDataProduct.setVersion(oldDataProduct.getInfo().getVersionNumber());
        oldBDDataProduct.setDescription(oldDataProduct.getInfo().getDescription());
        oldBDDataProduct.setDomain(oldDataProduct.getInfo().getDomain());
        oldBDDataProduct.setName(oldDataProduct.getInfo().getName());
        oldBDDataProduct.setDisplayName(oldDataProduct.getInfo().getDisplayName());

        BDDataProductRes newBDDataProduct = new BDDataProductRes();
        newBDDataProduct.setUuid("abc123");
        newBDDataProduct.setVersion(newDataProduct.getInfo().getVersionNumber());
        newBDDataProduct.setDescription(newDataProduct.getInfo().getDescription());
        newBDDataProduct.setDomain(newDataProduct.getInfo().getDomain());
        newBDDataProduct.setName(newDataProduct.getInfo().getName());
        newBDDataProduct.setDisplayName(newDataProduct.getInfo().getDisplayName());

        Mockito.when(bdDataProductClient.getDataProduct(Mockito.any()))
                .thenReturn(Optional.of(oldBDDataProduct));
        Mockito.when(bdDataProductClient.getDataProducts(Mockito.any(), Mockito.any()))
                .thenReturn(emptyBdDataProductResPage);
        Mockito.when(bdDataProductClient.createDataProduct(Mockito.any()))
                .thenReturn(new BDDataProductRes());
        Mockito.doNothing().when(bdDataProductClient).deleteDataProduct(Mockito.any());
        Mockito.when(bdDataProductClient.createDataProductAssets(Mockito.any()))
                .thenReturn(bdProductPortAssetsRes);
        Mockito.when(bdDataProductClient.updateDataProduct(Mockito.any()))
                .thenReturn(newBDDataProduct);

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

        Mockito.when(odmRegistryClient.getApi(Mockito.any(), Mockito.any()))
                .thenReturn(new ArrayList<>());

        Page<PolicyEvaluationResultResource> emptyPolicyEvaluationResults = new PageImpl<>(new ArrayList<>());
        Mockito.when(odmPolicyEvaluationResultsClient.getPolicyEvaluationResults(Mockito.any(), Mockito.any()))
                .thenReturn(emptyPolicyEvaluationResults);

        Mockito.when(notificationClient.updateEventNotification(Mockito.any(), Mockito.any()))
                .thenAnswer(invocation -> invocation.getArgument(1));

        ResponseEntity<ObjectNode> consumeResponse = consumeClient.consumeEventNotificationResponseEntity(notificationResource);
        notificationResource = objectMapper.convertValue(consumeResponse.getBody(), EventNotificationResource.class);

        Mockito.verify(notificationClient, Mockito.times(2)).updateEventNotification(Mockito.any(), Mockito.any());
        assertThat(notificationResource.getStatus()).isEqualTo(EventNotificationStatus.PROCESSED);

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testNotificationCreate_DataProductVersionDeleted() throws IOException {

        BDShortUserRes bdUser = new BDShortUserRes();
        BDStewardshipRoleRes bdRole = new BDStewardshipRoleRes();
        bdRole.setUuid("BlindataIT.role.uuid");

        Mockito.when(bdUserClient.getBlindataUser(Mockito.any()))
                .thenReturn(Optional.of(bdUser));
        Mockito.when(bdStewardshipClient.getRole(Mockito.any()))
                .thenReturn(bdRole);

        EventNotificationResource notificationResource = createNotificationResource(
                ODMObserverBlindataResources.NOTIFICATION_DATA_PRODUCT_VERSION_DELETED
        );

        DataProductVersionDPDS oldDataProduct = objectMapper.readValue(notificationResource.getEvent().getBeforeState(), DataProductVersionDPDS.class);

        BDDataProductRes oldBDDataProduct = new BDDataProductRes();
        oldBDDataProduct.setUuid("abc123");
        oldBDDataProduct.setVersion(oldDataProduct.getInfo().getVersionNumber());
        oldBDDataProduct.setDescription(oldDataProduct.getInfo().getDescription());
        oldBDDataProduct.setDomain(oldDataProduct.getInfo().getDomain());
        oldBDDataProduct.setName(oldDataProduct.getInfo().getName());
        oldBDDataProduct.setDisplayName(oldDataProduct.getInfo().getDisplayName());

        Mockito.when(bdDataProductClient.getDataProduct(Mockito.any()))
                .thenReturn(Optional.of(oldBDDataProduct));
        Mockito.doNothing().when(bdDataProductClient).deleteDataProduct(Mockito.any());

        Mockito.when(notificationClient.updateEventNotification(Mockito.any(), Mockito.any()))
                .thenAnswer(invocation -> invocation.getArgument(1));

        ResponseEntity<ObjectNode> consumeResponse = consumeClient.consumeEventNotificationResponseEntity(notificationResource);
        notificationResource = objectMapper.convertValue(consumeResponse.getBody(), EventNotificationResource.class);

        Mockito.verify(notificationClient, Mockito.times(2)).updateEventNotification(Mockito.any(), Mockito.any());
        assertThat(notificationResource.getStatus()).isEqualTo(EventNotificationStatus.PROCESSED);

    }

}