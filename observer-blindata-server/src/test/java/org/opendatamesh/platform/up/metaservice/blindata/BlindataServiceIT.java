package org.opendatamesh.platform.up.metaservice.blindata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyEvaluationResultClient;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDPolicyEvaluationResultClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDStewardshipClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDUserClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.odm.OdmRegistryClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.*;
import org.opendatamesh.platform.up.metaservice.blindata.services.NotificationEventHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Optional;

@TestPropertySource(properties = {"blindata.roleUuid=BlindataIT.role.uuid"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class BlindataServiceIT extends ODMObserverBlindataAppIT {
    @Autowired
    private NotificationEventHandlerService blindataService;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BDDataProductClient bdDataProductClient;
    @MockBean
    private BDPolicyEvaluationResultClient bdPolicyEvaluationResultClient;
    @MockBean
    private BDUserClient bdUserClient;
    @MockBean
    private BDStewardshipClient bdStewardshipClient;
    @MockBean
    private PolicyEvaluationResultClient odmPolicyEvaluationResultsClient;
    @MockBean
    private OdmRegistryClient odmRegistryClient;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDataProductCreation() throws JsonProcessingException {
        BDShortUserRes bdUser = new BDShortUserRes();
        BDStewardshipRoleRes bdRole = new BDStewardshipRoleRes();
        bdRole.setUuid("BlindataIT.role.uuid");

        Mockito.when(bdUserClient.getBlindataUser(Mockito.any())).thenReturn(Optional.of(bdUser));
        Mockito.when(bdStewardshipClient.getRole(Mockito.any())).thenReturn(bdRole);


        BDDataProductRes bdDataProductRes = objectMapper.readValue("{\"uuid\":\"ca8802b6-bc59-3ad8-8436-fdfe79c9c512\",\"name\":\"tripExecution\",\"displayName\":\"Trip Execution\",\"identifier\":\"urn:org.opendatamesh:dataproducts:tripExecution\",\"description\":\"Gestione dei viaggi necessari ad eseguire il trasporto della merce dalla sorgente alla destinazione\",\"version\":\"1.0.0\",\"domain\":\"sampleDomain\",\"additionalProperties\":null,\"ports\":[{\"uuid\":null,\"name\":\"tmsTripCDC\",\"displayName\":\"tms Trip CDC\",\"identifier\":\"urn:org.opendatamesh:dataproducts:tripExecution:1.0.0:inputports:tmsTripCDC:1.0.0\",\"description\":\"input port 1 of data product\",\"version\":\"1.0.0\",\"servicesType\":\"rest-services\",\"entityType\":\"inputPorts\",\"dependsOnIdentifier\":null,\"additionalProperties\":[{\"name\":\"sloDescription\",\"value\":\"slo for input port 1\"},{\"name\":\"deprecationPolicy\",\"value\":\"slo for input port 1\"}]},{\"uuid\":null,\"name\":\"input-port2\",\"displayName\":\"input port 2\",\"identifier\":\"urn:org.opendatamesh:dataproducts:tripExecution:1.0.0:inputports:input-port2:1.0.0\",\"description\":\"input port 2 of data product\",\"version\":\"1.0.0\",\"servicesType\":\"streaming-services\",\"entityType\":\"inputPorts\",\"dependsOnIdentifier\":null,\"additionalProperties\":[{\"name\":\"sloDescription\",\"value\":\"slo for input port 2\"},{\"name\":\"deprecationPolicy\",\"value\":\"slo for input port 2\"}]},{\"uuid\":null,\"name\":\"output-port1\",\"displayName\":\"output port 1\",\"identifier\":\"urn:org.opendatamesh:dataproducts:tripExecution:1.0.0:outputports:output-port1:1.0.0\",\"description\":\"output port 1 of data product\",\"version\":\"1.0.0\",\"servicesType\":\"rest-services\",\"entityType\":\"outputPorts\",\"dependsOnIdentifier\":null,\"additionalProperties\":[{\"name\":\"sloDescription\",\"value\":\"slo for output port 1\"},{\"name\":\"deprecationPolicy\",\"value\":\"slo for output port 1\"}]}]}", BDDataProductRes.class);

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
                .thenReturn(bdDataProductRes);
        Mockito.doNothing().when(bdDataProductClient).deleteDataProduct(Mockito.any());
        Mockito.when(bdDataProductClient.createDataProductAssets(Mockito.any())).thenReturn(bdProductPortAssetsRes);

        BDStewardshipResponsibilityRes bdResponsibility = new BDStewardshipResponsibilityRes();
        bdResponsibility.setUser(bdUser);
        bdResponsibility.setResourceIdentifier(bdDataProductRes.getUuid());
        bdResponsibility.setResourceType("DATA_PRODUCT");
        bdResponsibility.setStewardshipRole(bdRole);
        bdResponsibility.setResourceName(bdDataProductRes.getName());

        Mockito.when(bdStewardshipClient.createResponsibility(Mockito.any())).thenReturn(bdResponsibility);
        Mockito.when(bdStewardshipClient.getResponsibility(Mockito.any(), Mockito.any())).thenReturn(Optional.empty());

        BDUploadResultsMessage uploadResultsMessage = new BDUploadResultsMessage();
        uploadResultsMessage.setRowCreated(1);

        Mockito.when(bdPolicyEvaluationResultClient.createPolicyEvaluationRecords(Mockito.any()))
                .thenReturn(uploadResultsMessage);

        Mockito.when(odmRegistryClient.getApi(Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>());

        PolicyResource policyResource = new PolicyResource();
        policyResource.setId(1L);
        policyResource.setLastVersion(true);
        policyResource.setRootId(1L);
        policyResource.setName("BlindataIT.policy.name");
        PolicyEngineResource policyEngineResource = new PolicyEngineResource();
        policyEngineResource.setId(1L);
        PolicyEvaluationResultResource policyEvaluationResultResource = new PolicyEvaluationResultResource();
        policyEvaluationResultResource.setDataProductId(bdDataProductRes.getIdentifier());
        policyEvaluationResultResource.setDataProductVersion(bdDataProductRes.getVersion());
        policyEvaluationResultResource.setResult(true);
        policyEvaluationResultResource.setId(1L);
        policyEvaluationResultResource.setInputObject(null);
        policyEvaluationResultResource.setOutputObject(null);
        policyEvaluationResultResource.setPolicy(policyResource);
        BDPolicyEvaluationRecord bdPolicyEvaluationRecord = new BDPolicyEvaluationRecord();
        bdPolicyEvaluationRecord.setEvaluationDate(policyEvaluationResultResource.getCreatedAt());
        bdPolicyEvaluationRecord.setImplementationName("BlindataIT.policy.name");
        bdPolicyEvaluationRecord.setPolicyName(policyResource.getName());
        bdPolicyEvaluationRecord.setEvaluationResult(BDPolicyEvaluationRecord.BDPolicyEvaluationResult.VERIFIED);
        bdPolicyEvaluationRecord.setResolverKey("uuid");
        bdPolicyEvaluationRecord.setResolverValue(bdDataProductRes.getUuid());
        bdPolicyEvaluationRecord.setResourceType("DATA_PRODUCT");
        BDPolicyEvaluationRecords bdPolicyEvaluationRecords = new BDPolicyEvaluationRecords();
        bdPolicyEvaluationRecords.setRecords(Lists.newArrayList(bdPolicyEvaluationRecord));

        Page<PolicyEvaluationResultResource> policyEvaluationResults = new PageImpl<>(Lists.newArrayList(policyEvaluationResultResource));
        Mockito.when(odmPolicyEvaluationResultsClient.getPolicyEvaluationResults(Mockito.any(), Mockito.any())).thenReturn(policyEvaluationResults);

        String notificationJson = "{\"id\":null,\"event\":{\"id\":null,\"type\":\"DATA_PRODUCT_VERSION_CREATED\",\"entityId\":\"ca8802b6-bc59-3ad8-8436-fdfe79c9c512\",\"beforeState\":null,\"afterState\":\"{\\\"dataProductDescriptor\\\":\\\"1.0.0-DRAFT\\\",\\\"info\\\":{\\\"id\\\":\\\"ca8802b6-bc59-3ad8-8436-fdfe79c9c512\\\",\\\"fullyQualifiedName\\\":\\\"urn:org.opendatamesh:dataproducts:tripExecution\\\",\\\"entityType\\\":\\\"dataproduct\\\",\\\"name\\\":\\\"tripExecution\\\",\\\"version\\\":\\\"1.0.0\\\",\\\"displayName\\\":\\\"Trip Execution\\\",\\\"description\\\":\\\"Gestione dei viaggi necessari ad eseguire il trasporto della merce dalla sorgente alla destinazione\\\",\\\"domain\\\":\\\"sampleDomain\\\",\\\"owner\\\":{\\\"id\\\":\\\"john.doe@company-xyz.com\\\",\\\"name\\\":\\\"John Doe\\\"},\\\"contactPoints\\\":[{\\\"name\\\":\\\"Support Team Mail\\\",\\\"description\\\":\\\"The mail address of to the team that give support on this product\\\",\\\"channel\\\":\\\"email\\\",\\\"address\\\":\\\"trip-execution-support@company-xyz.com\\\"},{\\\"name\\\":\\\"Issue Tracker\\\",\\\"description\\\":\\\"The address of the issue tracker associated to this product\\\",\\\"channel\\\":\\\"web\\\",\\\"address\\\":\\\"https://readmine.company-xyz.com/trip-execution\\\"}]},\\\"interfaceComponents\\\":{\\\"inputPorts\\\":[{\\\"externalReference\\\":false,\\\"id\\\":\\\"0e50e42b-c60d-3a9a-a4f5-d33e24848914\\\",\\\"fullyQualifiedName\\\":\\\"urn:org.opendatamesh:dataproducts:tripExecution:1.0.0:inputports:tmsTripCDC:1.0.0\\\",\\\"entityType\\\":\\\"inputport\\\",\\\"name\\\":\\\"tmsTripCDC\\\",\\\"version\\\":\\\"1.0.0\\\",\\\"displayName\\\":\\\"tms Trip CDC\\\",\\\"description\\\":\\\"input port 1 of data product\\\",\\\"componentGroup\\\":\\\"gruppoA\\\",\\\"tags\\\":[\\\"tag1\\\",\\\"tag2\\\",\\\"tag3\\\"],\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for input port 1\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"},\\\"promises\\\":{\\\"platform\\\":\\\"onprem:milan-1\\\",\\\"servicesType\\\":\\\"rest-services\\\",\\\"api\\\":{\\\"externalReference\\\":false,\\\"id\\\":\\\"96856bc1-761d-3d54-a703-d65c9466ad4f\\\",\\\"fullyQualifiedName\\\":\\\"urn:org.opendatamesh:apis:89a5d6c2-9115-3a54-bf06-6e54a82b2412:1.3.2\\\",\\\"entityType\\\":\\\"api\\\",\\\"name\\\":\\\"89a5d6c2-9115-3a54-bf06-6e54a82b2412\\\",\\\"version\\\":\\\"1.3.2\\\",\\\"tags\\\":[],\\\"specification\\\":\\\"custom-api-spec\\\",\\\"definition\\\":{\\\"inline\\\":false,\\\"resolvedRef\\\":false,\\\"$ref\\\":\\\"http://localhost:8001/api/v1/pp/registry/apis/96856bc1-761d-3d54-a703-d65c9466ad4f\\\"}},\\\"deprecationPolicy\\\":{\\\"description\\\":\\\"deprecationPolicy for input port 1\\\",\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for deprecationPolicy of input port 1\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"}},\\\"slo\\\":{\\\"description\\\":\\\"slo for input port 1\\\",\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for slo of input port 1\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"}}},\\\"expectations\\\":{\\\"audience\\\":{\\\"description\\\":\\\"audience for input port 1\\\",\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for audience of input port 1\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"}},\\\"usage\\\":{\\\"description\\\":\\\"usage for input port 1\\\",\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for audience of input port 1\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"}}},\\\"contracts\\\":{\\\"termsAndConditions\\\":{\\\"description\\\":\\\"termsAndConditions for input port 1\\\",\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for termsAndConditions of input port 1\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"}},\\\"billingPolicy\\\":{\\\"description\\\":\\\"billingPolicy for input port 1\\\",\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for billingPolicy of input port 1\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"}},\\\"sla\\\":{\\\"description\\\":\\\"sla for input port 1\\\",\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for sla of input port 1\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"}}}},{\\\"externalReference\\\":false,\\\"id\\\":\\\"8005ffdc-c163-3cfa-ba42-6346de9b9581\\\",\\\"fullyQualifiedName\\\":\\\"urn:org.opendatamesh:dataproducts:tripExecution:1.0.0:inputports:input-port2:1.0.0\\\",\\\"entityType\\\":\\\"inputport\\\",\\\"name\\\":\\\"input-port2\\\",\\\"version\\\":\\\"1.0.0\\\",\\\"displayName\\\":\\\"input port 2\\\",\\\"description\\\":\\\"input port 2 of data product\\\",\\\"componentGroup\\\":\\\"gruppoB\\\",\\\"tags\\\":[\\\"tag2\\\",\\\"tag4\\\"],\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for input port 2\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"},\\\"promises\\\":{\\\"platform\\\":\\\"aws:eu-south-1\\\",\\\"servicesType\\\":\\\"streaming-services\\\",\\\"api\\\":{\\\"externalReference\\\":false,\\\"id\\\":\\\"4495e3ca-ce7b-3ae3-9784-0ced080b7c62\\\",\\\"fullyQualifiedName\\\":\\\"urn:org.opendatamesh:apis:org.odm.api-1:1.3.0\\\",\\\"entityType\\\":\\\"api\\\",\\\"name\\\":\\\"org.odm.api-1\\\",\\\"version\\\":\\\"1.3.0\\\",\\\"description\\\":\\\"Let's stream!\\\",\\\"tags\\\":[],\\\"specification\\\":\\\"custom-api-spec\\\",\\\"specificationVersion\\\":\\\"1.2.0\\\",\\\"definition\\\":{\\\"inline\\\":false,\\\"resolvedRef\\\":false,\\\"$ref\\\":\\\"http://localhost:8001/api/v1/pp/registry/apis/4495e3ca-ce7b-3ae3-9784-0ced080b7c62\\\"}},\\\"deprecationPolicy\\\":{\\\"description\\\":\\\"deprecationPolicy for input port 2\\\",\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for deprecationPolicy of input port 2\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"}},\\\"slo\\\":{\\\"description\\\":\\\"slo for input port 2\\\",\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for slo of input port 2\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"}}},\\\"expectations\\\":{\\\"audience\\\":{\\\"description\\\":\\\"audience for input port 2\\\",\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for audience of input port 2\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"}},\\\"usage\\\":{\\\"description\\\":\\\"usage for input port 2\\\",\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for usage of input port 2\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"}}},\\\"contracts\\\":{\\\"termsAndConditions\\\":{\\\"description\\\":\\\"termsAndConditions for input port 2\\\",\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for termsAndConditions of input port 2\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"}},\\\"billingPolicy\\\":{\\\"description\\\":\\\"billingPolicy for input port 2\\\",\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for billingPolicy of input port 2\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"}},\\\"sla\\\":{\\\"description\\\":\\\"sla for input port 2\\\",\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for sla of input port 2\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"}}}}],\\\"outputPorts\\\":[{\\\"externalReference\\\":false,\\\"id\\\":\\\"60fb6090-2393-34f6-8765-f6aaaff379f5\\\",\\\"fullyQualifiedName\\\":\\\"urn:org.opendatamesh:dataproducts:tripExecution:1.0.0:outputports:output-port1:1.0.0\\\",\\\"entityType\\\":\\\"outputport\\\",\\\"name\\\":\\\"output-port1\\\",\\\"version\\\":\\\"1.0.0\\\",\\\"displayName\\\":\\\"output port 1\\\",\\\"description\\\":\\\"output port 1 of data product\\\",\\\"componentGroup\\\":\\\"gruppoA\\\",\\\"tags\\\":[\\\"tag1\\\",\\\"tag2\\\",\\\"tag3\\\"],\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for output port 1\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"},\\\"promises\\\":{\\\"platform\\\":\\\"aws:eu-south-1\\\",\\\"servicesType\\\":\\\"rest-services\\\",\\\"api\\\":{\\\"externalReference\\\":false,\\\"id\\\":\\\"2912a0a4-4366-3308-82d1-8e64e662371f\\\",\\\"fullyQualifiedName\\\":\\\"urn:org.opendatamesh:apis:rest-api-1:1.0.0\\\",\\\"entityType\\\":\\\"api\\\",\\\"name\\\":\\\"rest-api-1\\\",\\\"version\\\":\\\"1.0.0\\\",\\\"description\\\":\\\"Rest what else?\\\",\\\"tags\\\":[],\\\"specification\\\":\\\"custom-api-spec\\\",\\\"specificationVersion\\\":\\\"1.3.2\\\",\\\"definition\\\":{\\\"inline\\\":false,\\\"resolvedRef\\\":false,\\\"$ref\\\":\\\"http://localhost:8001/api/v1/pp/registry/apis/2912a0a4-4366-3308-82d1-8e64e662371f\\\"}},\\\"deprecationPolicy\\\":{\\\"description\\\":\\\"deprecationPolicy for output port 1\\\",\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for deprecationPolicy of output port 1\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"}},\\\"slo\\\":{\\\"description\\\":\\\"slo for output port 1\\\",\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for slo of output port 1\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"}}},\\\"expectations\\\":{\\\"audience\\\":{\\\"description\\\":\\\"audience for output port 1\\\",\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for audience of input port 1\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"}},\\\"usage\\\":{\\\"description\\\":\\\"usage for output port 1\\\",\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for audience of input port 1\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"}}},\\\"contracts\\\":{\\\"termsAndConditions\\\":{\\\"description\\\":\\\"termsAndConditions for output port 1\\\",\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for termsAndConditions of output port 1\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"}},\\\"billingPolicy\\\":{\\\"description\\\":\\\"billingPolicy for output port 1\\\",\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for billingPolicy of output port 1\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"}},\\\"sla\\\":{\\\"description\\\":\\\"sla for output port 1\\\",\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for sla of output port 1\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"}}}}]},\\\"internalComponents\\\":{\\\"applicationComponents\\\":[{\\\"externalReference\\\":false,\\\"id\\\":\\\"68ed55fc-fabf-3af5-bcff-7b606e1f380f\\\",\\\"fullyQualifiedName\\\":\\\"urn:org.opendatamesh:dataproducts:tripExecution:1.0.0:applications:modelNormalizationJob:1.1.0\\\",\\\"entityType\\\":\\\"application\\\",\\\"name\\\":\\\"modelNormalizationJob\\\",\\\"version\\\":\\\"1.1.0\\\",\\\"displayName\\\":\\\"model Normalization Job\\\",\\\"description\\\":\\\"internal app 1 of data product\\\",\\\"componentGroup\\\":\\\"gruppoB\\\",\\\"tags\\\":[\\\"tag1\\\",\\\"tag5\\\"],\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for internal-app-1\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"},\\\"consumesFrom\\\":[],\\\"providesTo\\\":[],\\\"dependsOn\\\":[\\\"urn:dpds:it.quantyca:dataproducts:SampleDP:1:infrastructure:stagingArea\\\"]}],\\\"infrastructuralComponents\\\":[{\\\"externalReference\\\":false,\\\"id\\\":\\\"3bc07517-e909-3646-b029-dcc87609ea07\\\",\\\"fullyQualifiedName\\\":\\\"urn:org.opendatamesh:dataproducts:tripExecution:1.0.0:infrastructures:stagingArea:1.1.0\\\",\\\"entityType\\\":\\\"infrastructure\\\",\\\"name\\\":\\\"stagingArea\\\",\\\"version\\\":\\\"1.1.0\\\",\\\"displayName\\\":\\\"staging area\\\",\\\"description\\\":\\\"infrastructure component 1 of data product\\\",\\\"componentGroup\\\":\\\"gruppoC\\\",\\\"tags\\\":[\\\"tag2\\\",\\\"tag4\\\"],\\\"externalDocs\\\":{\\\"description\\\":\\\"external doc description for infra-component-1\\\",\\\"mediaType\\\":\\\"text/html\\\",\\\"$href\\\":\\\"http://fakeurl1\\\"},\\\"platform\\\":\\\"TBD\\\",\\\"infrastructureType\\\":\\\"storage-resource\\\",\\\"dependsOn\\\":[]}],\\\"lifecycleInfo\\\":{\\\"tasksInfo\\\":[{\\\"name\\\":\\\"dev:task:1\\\",\\\"order\\\":1,\\\"service\\\":{\\\"$href\\\":\\\"azure-devops\\\"},\\\"template\\\":{\\\"externalReference\\\":false,\\\"id\\\":\\\"cbba92ae-e9d6-3029-bd5d-73d1e80202ee\\\",\\\"fullyQualifiedName\\\":\\\"urn:org.opendatamesh:templates:e99500d7-fa68-3ea4-ba3b-6e77b1284c73:1.0.0\\\",\\\"entityType\\\":\\\"template\\\",\\\"name\\\":\\\"e99500d7-fa68-3ea4-ba3b-6e77b1284c73\\\",\\\"version\\\":\\\"1.0.0\\\",\\\"tags\\\":[],\\\"specification\\\":\\\"spec\\\",\\\"specificationVersion\\\":\\\"2.0\\\",\\\"definition\\\":{\\\"inline\\\":false,\\\"resolvedRef\\\":false,\\\"$ref\\\":\\\"http://localhost:8001/api/v1/pp/registry/templates/cbba92ae-e9d6-3029-bd5d-73d1e80202ee\\\"}},\\\"configurations\\\":{\\\"stagesToSkip\\\":[\\\"Test\\\",\\\"Deploy\\\"],\\\"params\\\":{\\\"paramOne\\\":\\\"value1\\\",\\\"paramTwo\\\":\\\"value2\\\",\\\"callbackRef\\\":\\\"http://localhost:8002/api/v1/pp/devops\\\"}}},{\\\"name\\\":\\\"prod:task:1\\\",\\\"order\\\":1,\\\"service\\\":{\\\"$href\\\":\\\"azure-devops\\\"},\\\"template\\\":{\\\"externalReference\\\":false,\\\"id\\\":\\\"cbba92ae-e9d6-3029-bd5d-73d1e80202ee\\\",\\\"fullyQualifiedName\\\":\\\"urn:org.opendatamesh:templates:e99500d7-fa68-3ea4-ba3b-6e77b1284c73:1.0.0\\\",\\\"entityType\\\":\\\"template\\\",\\\"name\\\":\\\"e99500d7-fa68-3ea4-ba3b-6e77b1284c73\\\",\\\"version\\\":\\\"1.0.0\\\",\\\"tags\\\":[],\\\"specification\\\":\\\"spec\\\",\\\"specificationVersion\\\":\\\"2.0\\\",\\\"definition\\\":{\\\"inline\\\":false,\\\"resolvedRef\\\":false,\\\"$ref\\\":\\\"http://localhost:8001/api/v1/pp/registry/templates/cbba92ae-e9d6-3029-bd5d-73d1e80202ee\\\"}},\\\"configurations\\\":{\\\"stagesToSkip\\\":[\\\"Test\\\",\\\"Deploy\\\"],\\\"params\\\":{\\\"paramOne\\\":\\\"value1.1\\\",\\\"paramTwo\\\":\\\"${dev.results.task1.ciao}\\\",\\\"callbackRef\\\":\\\"http://localhost:8002/api/v1/pp/devops\\\"}}}]}},\\\"tags\\\":[]}\",\"time\":\"2024-04-04T12:34:50.096+00:00\"},\"status\":null,\"processingOutput\":null,\"receivedAt\":null,\"processedAt\":null}";
        EventNotificationResource notificationResource = objectMapper.readValue(notificationJson, EventNotificationResource.class);
        Assertions.assertDoesNotThrow(
                () -> blindataService.handleDataProductCreated(notificationResource)
        );

        Mockito.verify(bdDataProductClient, Mockito.times(1)).getDataProduct("urn:org.opendatamesh:dataproducts:tripExecution");
        Mockito.verify(bdDataProductClient, Mockito.times(1)).createDataProduct(bdDataProductRes);

        //Input ports, no assets created on Blindata
        Mockito.verify(odmRegistryClient, Mockito.times(3)).getApi(Mockito.any(), Mockito.any());
        Mockito.verify(bdDataProductClient, Mockito.times(0)).createDataProductAssets(Mockito.any());

        ArgumentCaptor<BDStewardshipResponsibilityRes> argument = ArgumentCaptor.forClass(BDStewardshipResponsibilityRes.class);
        Mockito.verify(bdStewardshipClient, Mockito.times(1)).createResponsibility(argument.capture());
        Assertions.assertTrue(
                bdResponsibility.getResourceIdentifier().equalsIgnoreCase(argument.getValue().getResourceIdentifier()) &&
                        bdResponsibility.getResourceName().equalsIgnoreCase(argument.getValue().getResourceName()) &&
                        bdResponsibility.getResourceType().equals(argument.getValue().getResourceType()) &&
                        bdResponsibility.getStewardshipRole().equals(argument.getValue().getStewardshipRole())
        );

        Mockito.verify(odmPolicyEvaluationResultsClient, Mockito.times(1)).getPolicyEvaluationResults(Mockito.any(), Mockito.any());
        Mockito.verify(bdPolicyEvaluationResultClient, Mockito.times(1)).createPolicyEvaluationRecords(bdPolicyEvaluationRecords);

    }

}
