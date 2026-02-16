package org.opendatamesh.platform.up.metaservice.blindata.rest.v1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.opendatamesh.dpds.model.interfaces.Port;
import org.opendatamesh.platform.up.metaservice.blindata.ObserverBlindataAppIT;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmEventNotificationStatus;
import org.opendatamesh.platform.up.metaservice.blindata.services.DataProductPortAssetAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;


@ContextConfiguration(classes = DataProductActivityCompletedIT.SpyConfig.class)
public class DataProductActivityCompletedIT extends ObserverBlindataAppIT {

    private static final String OUTPUT_PORT_NAME = "testOutput";

    @Autowired
    private DataProductPortAssetAnalyzer dataProductPortAssetAnalyzer;

    @Autowired
    private BdDataProductClient bdDataProductClient;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUpMocks() {
        Mockito.reset(bdDataProductClient, dataProductPortAssetAnalyzer);

        BDDataProductRes existingProduct = new BDDataProductRes();
        existingProduct.setIdentifier("test-identifier");
        existingProduct.setName("testOdm");
        existingProduct.setUuid("test-uuid");
        Mockito.when(bdDataProductClient.getDataProduct(any())).thenReturn(Optional.of(existingProduct));
        Mockito.when(bdDataProductClient.patchDataProduct(any())).thenReturn(existingProduct);
        Mockito.when(bdDataProductClient.createDataProductAssets(any())).thenReturn(null);
    }

    @Test
    void expandOutputPortApiDefinitionWhenSignatureMatchesExactly() throws IOException {
        OdmEventNotificationResource notification = loadNotification("activityCreated_outputPortWithOnlyRawContent_eventNotification.json");

        ResponseEntity<OdmEventNotificationResource> response = rest.postForEntity(
                apiUrl(RoutesV1.CONSUME),
                notification,
                OdmEventNotificationResource.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getStatus()).hasToString(OdmEventNotificationStatus.PROCESSING.toString());

        List<Port> portsPassed = capturePortsPassedToExtractPhysicalResourcesFromPorts();
        Assertions.assertThat(portsPassed).isNotEmpty();
        Port outputPort = findOutputPort(portsPassed);
        Assertions.assertThat(outputPort).isNotNull();
        JsonNode apiNode = objectMapper.valueToTree(outputPort.getPromises().getApi());
        JsonNode definition = apiNode.get("definition");
        JsonNode expectedDefinition = objectMapper.readTree("{\n" +
                "  \"mediaType\" : null,\n" +
                "  \"baseUri\" : null,\n" +
                "  \"$ref\" : null,\n" +
                "  \"schema\" : {\n" +
                "    \"databaseSchemaName\" : \"test\",\n" +
                "    \"tables\" : [ {\n" +
                "      \"version\" : \"1.0.3\",\n" +
                "      \"specification\" : \"json-schema\",\n" +
                "      \"specificationVersion\" : \"1\",\n" +
                "      \"definition\" : {\n" +
                "        \"name\" : \"aaa\",\n" +
                "        \"displayName\" : \"aaa\",\n" +
                "        \"description\" : \"\",\n" +
                "        \"physicalType\" : \"BASE_TABLE\",\n" +
                "        \"properties\" : { }\n" +
                "      }\n" +
                "    } ],\n" +
                "    \"databaseName\" : \"\"\n" +
                "  },\n" +
                "  \"endpoints\" : [ ],\n" +
                "  \"inline\" : true,\n" +
                "  \"resolvedRef\" : false,\n" +
                "  \"info\" : {\n" +
                "    \"title\" : \"\",\n" +
                "    \"version\" : \"\"\n" +
                "  }\n" +
                "}");
        Assertions.assertThat(definition).usingRecursiveComparison()
                .isEqualTo(expectedDefinition);
    }

    private OdmEventNotificationResource loadNotification(String resourceName) throws IOException {
        byte[] bytes = Resources.toByteArray(getClass().getClassLoader().getResource("org/opendatamesh/platform/up/metaservice/blindata/rest/v1/" + resourceName));
        return objectMapper.readValue(bytes, OdmEventNotificationResource.class);
    }


    @SuppressWarnings("unchecked")
    private List<Port> capturePortsPassedToExtractPhysicalResourcesFromPorts() {
        ArgumentCaptor<List<Port>> captor = ArgumentCaptor.forClass(List.class);
        verify(dataProductPortAssetAnalyzer, atLeastOnce()).extractPhysicalResourcesFromPorts(captor.capture());
        List<List<Port>> allInvocations = captor.getAllValues();
        return allInvocations.stream()
                .filter(list -> list.stream().anyMatch(p -> OUTPUT_PORT_NAME.equals(p.getName())))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No invocation of extractPhysicalResourcesFromPorts contained output port " + OUTPUT_PORT_NAME));
    }

    private Port findOutputPort(List<Port> ports) {
        return ports.stream()
                .filter(p -> OUTPUT_PORT_NAME.equals(p.getName()))
                .findFirst()
                .orElse(null);
    }


    @Configuration
    static class SpyConfig {
        @Bean
        @Primary
        public DataProductPortAssetAnalyzer spyDataProductPortAssetAnalyzer(DataProductPortAssetAnalyzer real) {
            return Mockito.spy(real);
        }
    }
}
