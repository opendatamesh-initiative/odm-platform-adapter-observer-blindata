package org.opendatamesh.platform.up.metaservice.blindata.validator.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.io.Resources;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opendatamesh.platform.up.metaservice.blindata.ObserverBlindataAppIT;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdSemanticLinkingClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdStewardshipClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdUserClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.odm.OdmRegistryClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDShortUserRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDStewardshipRoleRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical.BDDataCategoryRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical.BDLogicalNamespaceRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.validator.resources.OdmValidatorPolicyEvaluationRequestRes;
import org.opendatamesh.platform.up.metaservice.blindata.validator.resources.OdmValidatorPolicyEvaluationResultRes;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BlindataValidatorControllerIT extends ObserverBlindataAppIT {

    @MockBean
    private BdDataProductClient bdDataProductClient;
    @MockBean
    private BdStewardshipClient bdStewardshipClient;
    @MockBean
    private BdUserClient bdUserClient;
    @MockBean
    private BdSemanticLinkingClient bdSemanticLinkingClient;
    @MockBean
    private OdmRegistryClient odmRegistryClient;

    @BeforeEach
    public void resetMocks() {
        Mockito.reset(
                bdDataProductClient,
                bdStewardshipClient,
                bdUserClient,
                bdSemanticLinkingClient,
                odmRegistryClient
        );
    }

    @Test
    public void testValidDataProduct() throws IOException {
        // Load test data from JSON file
        OdmValidatorPolicyEvaluationRequestRes request = mapper.readValue(
                Resources.toByteArray(getClass().getResource("valid_data_product.json")),
                OdmValidatorPolicyEvaluationRequestRes.class
        );

        // Call the validator endpoint
        ResponseEntity<OdmValidatorPolicyEvaluationResultRes> response = rest.postForEntity(
                "http://localhost:" + port + "/api/v1/up/validator/evaluate-policy",
                request,
                OdmValidatorPolicyEvaluationResultRes.class
        );

        // Verify the response
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getPolicyEvaluationId()).isEqualTo(1L);
        Assertions.assertThat(response.getBody().getEvaluationResult()).isTrue();
        Assertions.assertThat(response.getBody().getOutputObject().getMessage()).isNull();
        Assertions.assertThat(response.getBody().getOutputObject().getRawError().isNull()).isTrue();
    }

    @Test
    public void testValidDataProductVersion() throws IOException {
        // Load test data from JSON file
        OdmValidatorPolicyEvaluationRequestRes request = mapper.readValue(
                Resources.toByteArray(getClass().getResource("valid_data_product_version.json")),
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

        when(odmRegistryClient.getApi(any()))
                .thenAnswer(invocation -> {
                    String identifier = invocation.getArgument(0);
                    return Optional.ofNullable(findObjectByFullyQualifiedName(
                            request.getObjectToEvaluate(), identifier));
                });

        BDDataProductRes existingDataProduct = new BDDataProductRes();
        existingDataProduct.setUuid("dp-uuid");
        existingDataProduct.setName("test1");
        existingDataProduct.setIdentifier("urn:dpds:qualityDemo:dataproducts:test1:1");
        existingDataProduct.setVersion("1.0.0");
        existingDataProduct.setDomain("test");
        when(bdDataProductClient.getDataProduct(any())).thenReturn(Optional.of(existingDataProduct));

        // Call the validator endpoint
        ResponseEntity<OdmValidatorPolicyEvaluationResultRes> response = rest.postForEntity(
                "http://localhost:" + port + "/api/v1/up/validator/evaluate-policy",
                request,
                OdmValidatorPolicyEvaluationResultRes.class
        );


        verify(bdDataProductClient, never()).createDataProduct(any());
        verify(bdDataProductClient, never()).putDataProduct(any());

        // Verify the response
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getPolicyEvaluationId()).isEqualTo(2L);
        Assertions.assertThat(response.getBody().getEvaluationResult()).isTrue();
        Assertions.assertThat(response.getBody().getOutputObject().getMessage()).isNull();
        Assertions.assertThat(response.getBody().getOutputObject().getRawError().isNull()).isTrue();
    }

    @Test
    public void testValidateDataProductWithInvalidData() throws IOException {
        // Load test data from JSON file
        OdmValidatorPolicyEvaluationRequestRes request = mapper.readValue(
                Resources.toByteArray(getClass().getResource("invalid_data_product.json")),
                OdmValidatorPolicyEvaluationRequestRes.class
        );

        // Call the validator endpoint
        ResponseEntity<JsonNode> response = rest.postForEntity(
                "http://localhost:" + port + "/api/v1/up/validator/evaluate-policy",
                request,
                JsonNode.class
        );

        // Verify the response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().toString()).contains("Malformed Policy Evaluation Object");
    }

    @Test
    public void testValidateDataProductWithMissingRequiredFields() throws IOException {
        // Load test data from JSON file
        OdmValidatorPolicyEvaluationRequestRes request = mapper.readValue(
                Resources.toByteArray(getClass().getResource("invalid_data_product_missing_required.json")),
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

        when(odmRegistryClient.getApi(any()))
                .thenAnswer(invocation -> {
                    String identifier = invocation.getArgument(0);
                    return Optional.ofNullable(findObjectByFullyQualifiedName(
                            request.getObjectToEvaluate(), identifier));
                });

        // Mock bdDataProductClient.getDataProduct to return empty since this is a new product
        when(bdDataProductClient.getDataProduct(any())).thenReturn(Optional.empty());

        // Call the validator endpoint
        ResponseEntity<OdmValidatorPolicyEvaluationResultRes> response = rest.postForEntity(
                "http://localhost:" + port + "/api/v1/up/validator/evaluate-policy",
                request,
                OdmValidatorPolicyEvaluationResultRes.class
        );

        // Verify that neither create nor update was called since validation failed
        verify(bdDataProductClient, never()).createDataProduct(any());
        verify(bdDataProductClient, never()).putDataProduct(any());

        // Verify the response
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getPolicyEvaluationId()).isEqualTo(6L);
        Assertions.assertThat(response.getBody().getEvaluationResult()).isFalse();
        Assertions.assertThat(response.getBody().getOutputObject().getMessage()).contains("Blindata policy failed to validate data product");
        Assertions.assertThat(response.getBody().getOutputObject().getRawError().toString())
                .contains("[DataProductUpload] Missing odm data product info fully qualified name")
                .contains("[DataProductVersionUpload] Missing interface components on data product")
                .contains("[QualityUpload] Missing info fields on data product");
    }

    @Test
    public void testValidateDataProductWithMissingBlindataNamespaceOnSemanticLinking() throws IOException {
        OdmValidatorPolicyEvaluationRequestRes request = mapper.readValue(
                Resources.toByteArray(getClass().getResource("valid_data_product_with_semantic_linking.json")),
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

        when(odmRegistryClient.getApi(any()))
                .thenAnswer(invocation -> {
                    String identifier = invocation.getArgument(0);
                    return Optional.ofNullable(findObjectByFullyQualifiedName(
                            request.getObjectToEvaluate(), identifier));
                });

        // Return a data product like in other tests
        BDDataProductRes existingDataProduct = new BDDataProductRes();
        existingDataProduct.setUuid("dp-uuid");
        existingDataProduct.setName("test1");
        existingDataProduct.setIdentifier("urn:dpds:qualityDemo:dataproducts:test1:1");
        existingDataProduct.setVersion("1.0.0");
        existingDataProduct.setDomain("test");
        when(bdDataProductClient.getDataProduct(any())).thenReturn(Optional.of(existingDataProduct));

        // Make getLogicalNamespaceByIdentifier return empty
        when(bdSemanticLinkingClient.getLogicalNamespaceByIdentifier(any())).thenReturn(Optional.empty());

        // Call the validator endpoint
        ResponseEntity<OdmValidatorPolicyEvaluationResultRes> response = rest.postForEntity(
                "http://localhost:" + port + "/api/v1/up/validator/evaluate-policy",
                request,
                OdmValidatorPolicyEvaluationResultRes.class
        );

        // Verify bdDataProduct was called
        verify(bdDataProductClient, atLeastOnce()).getDataProduct(any());

        // Verify getLogicalNamespaceByIdentifier was called and returned empty
        verify(bdSemanticLinkingClient, atLeastOnce()).getLogicalNamespaceByIdentifier(any());

        // Verify other semantic linking calls were never made
        verify(bdSemanticLinkingClient, never()).getDataCategoryByNameAndNamespaceUuid(any(), any());
        verify(bdSemanticLinkingClient, never()).getSemanticLinkElements(any(), any());

        // Verify the response
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getPolicyEvaluationId()).isEqualTo(7L);
        Assertions.assertThat(response.getBody().getEvaluationResult()).isFalse();
        Assertions.assertThat(response.getBody().getOutputObject().getMessage()).contains("Blindata policy failed to validate data product");
        Assertions.assertThat(response.getBody().getOutputObject().getRawError().toString())
                .contains("Namespace not found for identifier: https://demo.blindata.io/logical/namespaces/name/filmRentalInc#");
    }

    @Test
    public void testValidateDataProductWithMissingBlindataConceptsOnSemanticLinking() throws IOException {
        OdmValidatorPolicyEvaluationRequestRes request = mapper.readValue(
                Resources.toByteArray(getClass().getResource("valid_data_product_with_semantic_linking.json")),
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

        when(odmRegistryClient.getApi(any()))
                .thenAnswer(invocation -> {
                    String identifier = invocation.getArgument(0);
                    return Optional.ofNullable(findObjectByFullyQualifiedName(
                            request.getObjectToEvaluate(), identifier));
                });

        // Return a data product like in other tests
        BDDataProductRes existingDataProduct = new BDDataProductRes();
        existingDataProduct.setUuid("dp-uuid");
        existingDataProduct.setName("test1");
        existingDataProduct.setIdentifier("urn:dpds:qualityDemo:dataproducts:test1:1");
        existingDataProduct.setVersion("1.0.0");
        existingDataProduct.setDomain("test");
        when(bdDataProductClient.getDataProduct(any())).thenReturn(Optional.of(existingDataProduct));

        // Make getLogicalNamespaceByIdentifier return a namespace (success)
        when(bdSemanticLinkingClient.getLogicalNamespaceByIdentifier(any()))
                .thenAnswer(invocation -> {
                    BDLogicalNamespaceRes namespace = new BDLogicalNamespaceRes();
                    namespace.setIdentifier(invocation.getArgument(0));
                    namespace.setUuid("test-namespace-uuid");
                    return Optional.of(namespace);
                });

        // Make getDataCategoryByNameAndNamespaceUuid return empty (concept not found)
        when(bdSemanticLinkingClient.getDataCategoryByNameAndNamespaceUuid(any(), any()))
                .thenReturn(Optional.empty());

        // Call the validator endpoint
        ResponseEntity<OdmValidatorPolicyEvaluationResultRes> response = rest.postForEntity(
                "http://localhost:" + port + "/api/v1/up/validator/evaluate-policy",
                request,
                OdmValidatorPolicyEvaluationResultRes.class
        );

        // Verify bdDataProduct was called and returned a product
        verify(bdDataProductClient, atLeastOnce()).getDataProduct(any());

        // Verify getLogicalNamespaceByIdentifier was called and returned a namespace
        verify(bdSemanticLinkingClient, atLeastOnce()).getLogicalNamespaceByIdentifier(any());

        // Verify getDataCategoryByNameAndNamespaceUuid was called and returned empty
        verify(bdSemanticLinkingClient, atLeastOnce()).getDataCategoryByNameAndNamespaceUuid(any(), any());

        // Verify getSemanticLinkElements was never called since data category was not found
        verify(bdSemanticLinkingClient, never()).getSemanticLinkElements(any(), any());

        // Verify the response
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getPolicyEvaluationId()).isEqualTo(7L);
        Assertions.assertThat(response.getBody().getEvaluationResult()).isFalse();
        Assertions.assertThat(response.getBody().getOutputObject().getMessage()).contains("Blindata policy failed to validate data product");
        Assertions.assertThat(response.getBody().getOutputObject().getRawError().toString())
                .contains("Data category: Customer not found in namespace https://demo.blindata.io/logical/namespaces/name/filmRentalInc#");
    }

    @Test
    public void testValidateDataProductWithMissingBlindataSemanticLinkElement() throws IOException {
        OdmValidatorPolicyEvaluationRequestRes request = mapper.readValue(
                Resources.toByteArray(getClass().getResource("valid_data_product_with_semantic_linking.json")),
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

        when(odmRegistryClient.getApi(any()))
                .thenAnswer(invocation -> {
                    String identifier = invocation.getArgument(0);
                    return Optional.ofNullable(findObjectByFullyQualifiedName(
                            request.getObjectToEvaluate(), identifier));
                });

        // Return a data product like in other tests
        BDDataProductRes existingDataProduct = new BDDataProductRes();
        existingDataProduct.setUuid("dp-uuid");
        existingDataProduct.setName("test1");
        existingDataProduct.setIdentifier("urn:dpds:qualityDemo:dataproducts:test1:1");
        existingDataProduct.setVersion("1.0.0");
        existingDataProduct.setDomain("test");
        when(bdDataProductClient.getDataProduct(any())).thenReturn(Optional.of(existingDataProduct));

        // Make getLogicalNamespaceByIdentifier return a namespace (success)
        when(bdSemanticLinkingClient.getLogicalNamespaceByIdentifier(any()))
                .thenAnswer(invocation -> {
                    BDLogicalNamespaceRes namespace = new BDLogicalNamespaceRes();
                    namespace.setIdentifier(invocation.getArgument(0));
                    namespace.setUuid("test-namespace-uuid");
                    return Optional.of(namespace);
                });

        // Make getDataCategoryByNameAndNamespaceUuid return a data category (success)
        when(bdSemanticLinkingClient.getDataCategoryByNameAndNamespaceUuid(any(), any()))
                .thenAnswer(invocation -> {
                    BDDataCategoryRes category = new BDDataCategoryRes();
                    category.setUuid("test-category-uuid");
                    category.setName("Customer");
                    return Optional.of(category);
                });

        // Make getSemanticLinkElements return null (not found)
        when(bdSemanticLinkingClient.getSemanticLinkElements(any(), any()))
                .thenReturn(null);

        // Call the validator endpoint
        ResponseEntity<OdmValidatorPolicyEvaluationResultRes> response = rest.postForEntity(
                "http://localhost:" + port + "/api/v1/up/validator/evaluate-policy",
                request,
                OdmValidatorPolicyEvaluationResultRes.class
        );

        // Verify bdDataProduct was called
        verify(bdDataProductClient, atLeastOnce()).getDataProduct(any());

        // Verify getLogicalNamespaceByIdentifier was called and returned a namespace
        verify(bdSemanticLinkingClient, atLeastOnce()).getLogicalNamespaceByIdentifier(any());

        // Verify getDataCategoryByNameAndNamespaceUuid was called and returned a category
        verify(bdSemanticLinkingClient, atLeastOnce()).getDataCategoryByNameAndNamespaceUuid(any(), any());

        // Verify getSemanticLinkElements was called and returned null
        verify(bdSemanticLinkingClient, atLeastOnce()).getSemanticLinkElements(any(), any());

        // Verify the response
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getPolicyEvaluationId()).isEqualTo(7L);
        Assertions.assertThat(response.getBody().getEvaluationResult()).isFalse();
        Assertions.assertThat(response.getBody().getOutputObject().getMessage()).contains("Blindata policy failed to validate data product");
        Assertions.assertThat(response.getBody().getOutputObject().getRawError().toString())
                .contains("Unable to resolve semantic elements for semantic link path: [Customer]");
    }

    @Test
    public void testValidateDataProductNotFoundInBlindata() throws IOException {
        OdmValidatorPolicyEvaluationRequestRes request = mapper.readValue(
                Resources.toByteArray(getClass().getResource("valid_data_product_with_semantic_linking.json")),
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

        when(odmRegistryClient.getApi(any()))
                .thenAnswer(invocation -> {
                    String identifier = invocation.getArgument(0);
                    return Optional.ofNullable(findObjectByFullyQualifiedName(
                            request.getObjectToEvaluate(), identifier));
                });

        // Make bdDataProductClient return empty (data product not found)
        when(bdDataProductClient.getDataProduct(any())).thenReturn(Optional.empty());

        // Call the validator endpoint
        ResponseEntity<OdmValidatorPolicyEvaluationResultRes> response = rest.postForEntity(
                "http://localhost:" + port + "/api/v1/up/validator/evaluate-policy",
                request,
                OdmValidatorPolicyEvaluationResultRes.class
        );

        // Verify bdDataProduct was called and returned empty
        verify(bdDataProductClient, atLeastOnce()).getDataProduct(any());

        // Verify semantic linking calls were never made since data product was not found
        verify(bdSemanticLinkingClient, atLeastOnce()).getLogicalNamespaceByIdentifier(any());
        verify(bdSemanticLinkingClient, never()).getDataCategoryByNameAndNamespaceUuid(any(), any());
        verify(bdSemanticLinkingClient, never()).getSemanticLinkElements(any(), any());

        // Verify the response
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getPolicyEvaluationId()).isEqualTo(7L);
        Assertions.assertThat(response.getBody().getEvaluationResult()).isFalse();
        Assertions.assertThat(response.getBody().getOutputObject().getMessage()).contains("Blindata policy failed to validate data product");
        Assertions.assertThat(response.getBody().getOutputObject().getRawError().toString())
                .contains("Data product: urn:dpds:qualityDemo:dataproducts:test1:1 has not been created yet on Blindata");
    }

    @Test
    public void testValidateDataProductWithMissingBlindataUser() throws IOException {
        OdmValidatorPolicyEvaluationRequestRes request = mapper.readValue(
                Resources.toByteArray(getClass().getResource("valid_data_product_with_semantic_linking.json")),
                OdmValidatorPolicyEvaluationRequestRes.class
        );

        // Make getBlindataUser return empty (user not found)
        when(bdUserClient.getBlindataUser(any())).thenReturn(Optional.empty());

        // Call the validator endpoint
        ResponseEntity<OdmValidatorPolicyEvaluationResultRes> response = rest.postForEntity(
                "http://localhost:" + port + "/api/v1/up/validator/evaluate-policy",
                request,
                OdmValidatorPolicyEvaluationResultRes.class
        );

        // Verify bdUserClient was called and returned empty
        verify(bdUserClient, atLeastOnce()).getBlindataUser(any());

        // Verify semantic linking calls were never made since user was not found
        verify(bdSemanticLinkingClient, never()).getLogicalNamespaceByIdentifier(any());
        verify(bdSemanticLinkingClient, never()).getDataCategoryByNameAndNamespaceUuid(any(), any());
        verify(bdSemanticLinkingClient, never()).getSemanticLinkElements(any(), any());

        // Verify the response
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getPolicyEvaluationId()).isEqualTo(7L);
        Assertions.assertThat(response.getBody().getEvaluationResult()).isFalse();
        Assertions.assertThat(response.getBody().getOutputObject().getMessage()).contains("Blindata policy failed to validate data product");
        Assertions.assertThat(response.getBody().getOutputObject().getRawError().toString())
                .contains("user: owner@default.blindata.io not found on Blindata");
    }

    @Test
    public void testValidateDataProductWithMissingBlindataRole() throws IOException {
        OdmValidatorPolicyEvaluationRequestRes request = mapper.readValue(
                Resources.toByteArray(getClass().getResource("valid_data_product_with_semantic_linking.json")),
                OdmValidatorPolicyEvaluationRequestRes.class
        );

        // Setup valid user
        BDShortUserRes owner = new BDShortUserRes();
        owner.setUsername("owner@default.blindata.io");
        owner.setFullName("owner@default.blindata.io");
        when(bdUserClient.getBlindataUser(any())).thenReturn(Optional.of(owner));

        // Make getRole return null (role not found)
        when(bdStewardshipClient.getRole(any())).thenReturn(null);

        // Call the validator endpoint
        ResponseEntity<OdmValidatorPolicyEvaluationResultRes> response = rest.postForEntity(
                "http://localhost:" + port + "/api/v1/up/validator/evaluate-policy",
                request,
                OdmValidatorPolicyEvaluationResultRes.class
        );

        // Verify bdUserClient was called and returned user
        verify(bdUserClient, atLeastOnce()).getBlindataUser(any());

        // Verify bdStewardshipClient was called and returned null
        verify(bdStewardshipClient, atLeastOnce()).getRole(any());

        // Verify semantic linking calls were never made since role was not found
        verify(bdSemanticLinkingClient, never()).getLogicalNamespaceByIdentifier(any());
        verify(bdSemanticLinkingClient, never()).getDataCategoryByNameAndNamespaceUuid(any(), any());
        verify(bdSemanticLinkingClient, never()).getSemanticLinkElements(any(), any());

        // Verify the response
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getPolicyEvaluationId()).isEqualTo(7L);
        Assertions.assertThat(response.getBody().getEvaluationResult()).isFalse();
        Assertions.assertThat(response.getBody().getOutputObject().getMessage()).contains("Blindata policy failed to validate data product");
        Assertions.assertThat(response.getBody().getOutputObject().getRawError().toString())
                .contains("role: test-role-uuid not found on Blindata");
    }

    @Test
    public void testValidateDataProductWithMissingQualityCheckFields() throws IOException {
        // Load test data from JSON file
        OdmValidatorPolicyEvaluationRequestRes request = mapper.readValue(
                Resources.toByteArray(getClass().getResource("invalid_data_product_missing_quality_fields.json")),
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

        when(odmRegistryClient.getApi(any()))
                .thenAnswer(invocation -> {
                    String identifier = invocation.getArgument(0);
                    return Optional.ofNullable(findObjectByFullyQualifiedName(
                            request.getObjectToEvaluate(), identifier));
                });

        // Return a data product like in other tests
        BDDataProductRes existingDataProduct = new BDDataProductRes();
        existingDataProduct.setUuid("dp-uuid");
        existingDataProduct.setName("test1");
        existingDataProduct.setIdentifier("urn:dpds:qualityDemo:dataproducts:test1:1");
        existingDataProduct.setVersion("1.0.0");
        existingDataProduct.setDomain("test");
        when(bdDataProductClient.getDataProduct(any())).thenReturn(Optional.of(existingDataProduct));

        // Call the validator endpoint
        ResponseEntity<OdmValidatorPolicyEvaluationResultRes> response = rest.postForEntity(
                "http://localhost:" + port + "/api/v1/up/validator/evaluate-policy",
                request,
                OdmValidatorPolicyEvaluationResultRes.class
        );

        // Verify the response
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getPolicyEvaluationId()).isEqualTo(8L);
        Assertions.assertThat(response.getBody().getEvaluationResult()).isFalse();
        Assertions.assertThat(response.getBody().getOutputObject().getMessage()).contains("Blindata policy failed to validate data product");
        Assertions.assertThat(response.getBody().getOutputObject().getRawError().toString())
                //Missing refName on a quality reference object
                .contains("Quality object inside datastoreApi is not valid: {\\\"customProperties\\\":{\\\"issuePolicies\\\":[]}}")
                //Missing name field on a quality object
                .contains("Quality object inside datastoreApi is not valid: {\\\"description\\\":\\\"PLACEHOLDER\\\",\\\"dimension\\\":\\\"Validity\\\",\\\"unit\\\":\\\"percent\\\",\\\"type\\\":\\\"custom\\\",\\\"engine\\\":\\\"greatExpectations\\\",\\\"customProperties\\\":{\\\"scoreStrategy\\\":\\\"PERCENTAGE\\\",\\\"scoreWarningThreshold\\\":95.0,\\\"scoreSuccessThreshold\\\":100.0,\\\"issuePolicies\\\":[{\\\"name\\\":\\\"ScambiMWh ExpectColumnValuesToBeBetween\\\",\\\"policyType\\\":\\\"RECURRENT_RESULT_SEMAPHORE\\\",\\\"semaphoreColor\\\":\\\"RED\\\",\\\"semaphoresNumber\\\":1,\\\"autoClose\\\":true,\\\"severity\\\":\\\"INFO\\\",\\\"blindataCustomProp-jiraAssigneeAccountID\\\":\\\"111111\\\",\\\"description\\\":\\\"PLACEHOLDER\\\",\\\"blindataCustomProp-jiraProjectKey\\\":\\\"ABCD\\\"}],\\\"checkEnabled\\\":true,\\\"isCheckEnabled\\\":true,\\\"assetKwargs\\\":{}},\\\"implementation\\\":{\\\"type\\\":\\\"ExpectColumnValuesToBeInSet\\\",\\\"kwargs\\\":{\\\"column\\\":\\\"Macrozona\\\",\\\"value_set\\\":[\\\"NORD\\\",\\\"SUD\\\"]}}}\",\"Quality object inside datastoreApi is not valid: {\\\"customProperties\\\":{\\\"issuePolicies\\\":[]}}\",\"Quality object inside datastoreApi is not valid: {\\\"description\\\":\\\"PLACEHOLDER\\\",\\\"dimension\\\":\\\"Validity\\\",\\\"unit\\\":\\\"percent\\\",\\\"type\\\":\\\"custom\\\",\\\"engine\\\":\\\"greatExpectations\\\",\\\"customProperties\\\":{\\\"scoreStrategy\\\":\\\"PERCENTAGE\\\",\\\"scoreWarningThreshold\\\":95.0,\\\"scoreSuccessThreshold\\\":100.0,\\\"issuePolicies\\\":[{\\\"name\\\":\\\"ScambiMWh ExpectColumnValuesToBeBetween\\\",\\\"policyType\\\":\\\"RECURRENT_RESULT_SEMAPHORE\\\",\\\"semaphoreColor\\\":\\\"RED\\\",\\\"semaphoresNumber\\\":1,\\\"autoClose\\\":true,\\\"severity\\\":\\\"INFO\\\",\\\"blindataCustomProp-jiraAssigneeAccountID\\\":\\\"111111\\\",\\\"description\\\":\\\"PLACEHOLDER\\\",\\\"blindataCustomProp-jiraProjectKey\\\":\\\"ABCD\\\"}],\\\"checkEnabled\\\":true,\\\"isCheckEnabled\\\":true,\\\"assetKwargs\\\":{}},\\\"implementation\\\":{\\\"type\\\":\\\"ExpectColumnValuesToBeInSet\\\",\\\"kwargs\\\":{\\\"column\\\":\\\"Macrozona\\\",\\\"value_set\\\":[\\\"NORD\\\",\\\"SUD\\\"]}}}");
    }

    @Test
    public void testValidateDataProductWithMissingQualityCheckIssuePolicyFields() throws IOException {
        // Load test data from JSON file
        OdmValidatorPolicyEvaluationRequestRes request = mapper.readValue(
                Resources.toByteArray(getClass().getResource("invalid_data_product_missing_quality_issue_policy_fields.json")),
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

        when(odmRegistryClient.getApi(any()))
                .thenAnswer(invocation -> {
                    String identifier = invocation.getArgument(0);
                    return Optional.ofNullable(findObjectByFullyQualifiedName(
                            request.getObjectToEvaluate(), identifier));
                });

        // Return a data product like in other tests
        BDDataProductRes existingDataProduct = new BDDataProductRes();
        existingDataProduct.setUuid("dp-uuid");
        existingDataProduct.setName("test1");
        existingDataProduct.setIdentifier("urn:dpds:qualityDemo:dataproducts:test1:1");
        existingDataProduct.setVersion("1.0.0");
        existingDataProduct.setDomain("test");
        when(bdDataProductClient.getDataProduct(any())).thenReturn(Optional.of(existingDataProduct));

        // Call the validator endpoint
        ResponseEntity<OdmValidatorPolicyEvaluationResultRes> response = rest.postForEntity(
                "http://localhost:" + port + "/api/v1/up/validator/evaluate-policy",
                request,
                OdmValidatorPolicyEvaluationResultRes.class
        );

        // Verify the response
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getPolicyEvaluationId()).isEqualTo(9L);
        Assertions.assertThat(response.getBody().getEvaluationResult()).isFalse();
        Assertions.assertThat(response.getBody().getOutputObject().getMessage()).contains("Blindata policy failed to validate data product");
        Assertions.assertThat(response.getBody().getOutputObject().getRawError().toString())
                .contains("Missing quality issue policy name for quality check: Macrozona ExpectColumnValuesToBeInSet");
    }

    private JsonNode findObjectByFullyQualifiedName(JsonNode root, String identifier) {
        if (root == null || root.isValueNode()) {
            return null;
        }

        // If this node is an object and contains the target fullyQualifiedName
        if (root.isObject() && root.has("id")) {
            if (identifier.equals(root.get("id").asText())) {
                return root;
            }
        }

        // Recursively search fields
        for (JsonNode child : root) {
            JsonNode result = findObjectByFullyQualifiedName(child, identifier);
            if (result != null) {
                return result;
            }
        }

        return null;
    }
} 