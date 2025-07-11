package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDAdditionalPropertiesRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DataProductUploadTest {
    private ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Test
    public void testDataProductCreation() throws UseCaseExecutionException, IOException {
        DataProductUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_1.json")),
                DataProductUploadInitialState.class
        );

        DataProductUploadOdmOutboundPort odmOutboundPort = new DataProductUploadOdmOutboundPortMock(initialState);
        DataProductUploadBlindataOutboundPort blindataOutboundPort = spy(new DataProductUploadBlindataOutboundPortMock(initialState));

        new DataProductUpload(odmOutboundPort, blindataOutboundPort).execute();

        verify(blindataOutboundPort, times(1)).createDataProduct(any());
        verify(blindataOutboundPort, times(0)).updateDataProduct(any());
        verify(blindataOutboundPort, times(0)).createDataProductResponsibility(any(), any(), any());
    }

    @Test
    public void testDataProductAndResponsibilityCreation() throws UseCaseExecutionException, IOException {
        DataProductUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_2.json")),
                DataProductUploadInitialState.class
        );

        DataProductUploadOdmOutboundPort odmOutboundPort = new DataProductUploadOdmOutboundPortMock(initialState);
        DataProductUploadBlindataOutboundPort blindataOutboundPort = spy(new DataProductUploadBlindataOutboundPortMock(initialState));

        new DataProductUpload(odmOutboundPort, blindataOutboundPort).execute();

        verify(blindataOutboundPort, times(1)).createDataProduct(any());
        verify(blindataOutboundPort, times(0)).updateDataProduct(any());
        verify(blindataOutboundPort, times(1)).createDataProductResponsibility(any(), any(), any());
    }

    @Test
    public void testDataProductUpdateAndResponsibilityCreation() throws UseCaseExecutionException, IOException {
        DataProductUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_3.json")),
                DataProductUploadInitialState.class
        );

        DataProductUploadOdmOutboundPort odmOutboundPort = new DataProductUploadOdmOutboundPortMock(initialState);
        DataProductUploadBlindataOutboundPort blindataOutboundPort = spy(new DataProductUploadBlindataOutboundPortMock(initialState));

        new DataProductUpload(odmOutboundPort, blindataOutboundPort).execute();

        verify(blindataOutboundPort, times(0)).createDataProduct(any());
        verify(blindataOutboundPort, times(1)).updateDataProduct(any());
        verify(blindataOutboundPort, times(1)).createDataProductResponsibility(any(), any(), any());
    }

    @Test
    public void testDataProductCreationDryRun() throws IOException, UseCaseExecutionException {
        DataProductUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_1.json")),
                DataProductUploadInitialState.class
        );

        DataProductUploadOdmOutboundPort odmOutboundPort = new DataProductUploadOdmOutboundPortMock(initialState);
        DataProductUploadBlindataOutboundPort blindataOutboundPort = spy(new DataProductUploadBlindataOutboundPortMock(initialState));

        new DataProductUpload(odmOutboundPort, new DataProductUploadBlindataOutboundPortDryRunImpl(blindataOutboundPort)).execute();

        verify(blindataOutboundPort, times(0)).createDataProduct(any());
        verify(blindataOutboundPort, times(0)).updateDataProduct(any());
        verify(blindataOutboundPort, times(0)).createDataProductResponsibility(any(), any(), any());
    }

    @Test
    public void testDataProductAndResponsibilityCreationDryRun() throws IOException, UseCaseExecutionException {
        DataProductUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_2.json")),
                DataProductUploadInitialState.class
        );

        DataProductUploadOdmOutboundPort odmOutboundPort = new DataProductUploadOdmOutboundPortMock(initialState);
        DataProductUploadBlindataOutboundPort blindataOutboundPort = spy(new DataProductUploadBlindataOutboundPortMock(initialState));

        new DataProductUpload(odmOutboundPort, new DataProductUploadBlindataOutboundPortDryRunImpl(blindataOutboundPort)).execute();

        verify(blindataOutboundPort, times(0)).createDataProduct(any());
        verify(blindataOutboundPort, times(0)).updateDataProduct(any());
        verify(blindataOutboundPort, times(0)).createDataProductResponsibility(any(), any(), any());
    }

    @Test
    public void testDataProductUpdateAndResponsibilityCreationDryRun() throws IOException, UseCaseExecutionException {
        DataProductUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_3.json")),
                DataProductUploadInitialState.class
        );

        DataProductUploadOdmOutboundPort odmOutboundPort = new DataProductUploadOdmOutboundPortMock(initialState);
        DataProductUploadBlindataOutboundPort blindataOutboundPort = spy(new DataProductUploadBlindataOutboundPortMock(initialState));

        new DataProductUpload(odmOutboundPort, new DataProductUploadBlindataOutboundPortDryRunImpl(blindataOutboundPort)).execute();

        verify(blindataOutboundPort, times(0)).createDataProduct(any());
        verify(blindataOutboundPort, times(0)).updateDataProduct(any());
        verify(blindataOutboundPort, times(0)).createDataProductResponsibility(any(), any(), any());
    }

    @Test
    public void testDataProductUploadWithEmptyOwner() throws IOException, UseCaseExecutionException {
        DataProductUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_4.json")),
                DataProductUploadInitialState.class
        );

        DataProductUploadOdmOutboundPort odmOutboundPort = new DataProductUploadOdmOutboundPortMock(initialState);
        DataProductUploadBlindataOutboundPort blindataOutboundPort = spy(new DataProductUploadBlindataOutboundPortMock(initialState));

        new DataProductUpload(odmOutboundPort, new DataProductUploadBlindataOutboundPortDryRunImpl(blindataOutboundPort)).execute();

        verify(blindataOutboundPort, times(0)).createDataProduct(any());
        verify(blindataOutboundPort, times(0)).updateDataProduct(any());
        verify(blindataOutboundPort, times(0)).createDataProductResponsibility(any(), any(), any());
    }

    @Test
    public void testDataProductCreationWithAdditionalProperties() throws UseCaseExecutionException, IOException {
        // load initial state
        DataProductUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_5.json")),
                DataProductUploadInitialState.class
        );

        // prepare ports
        DataProductUploadOdmOutboundPort odmOutboundPort =
                new DataProductUploadOdmOutboundPortMock(initialState);
        DataProductUploadBlindataOutboundPort blindataOutboundPort =
                spy(new DataProductUploadBlindataOutboundPortMock(initialState));

        // execute use case
        new DataProductUpload(odmOutboundPort, blindataOutboundPort).execute();

        // capture arguments passed to createDataProduct and updateDataProduct
        ArgumentCaptor<BDDataProductRes> createCaptor = ArgumentCaptor.forClass(BDDataProductRes.class);
        ArgumentCaptor<BDDataProductRes> updateCaptor = ArgumentCaptor.forClass(BDDataProductRes.class);

        // verify create called once, update never called
        verify(blindataOutboundPort, times(1)).createDataProduct(createCaptor.capture());
        verify(blindataOutboundPort, never()).updateDataProduct(updateCaptor.capture());
        verify(blindataOutboundPort, times(0)).createDataProductResponsibility(any(), any(), any());

        // inspect the captured DataProduct for creation
        BDDataProductRes createdDp = createCaptor.getValue();
        assertThat(createdDp).isNotNull();

        List<BDAdditionalPropertiesRes> expectedProps =
                Lists.newArrayList(new BDAdditionalPropertiesRes("property", "propertyToBeImported"));

        assertThat(createdDp.getAdditionalProperties())
                .as("Check that additional properties include the custom prop")
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedProps);

        assertThat(createdDp.getProductType()).isEqualTo("source-aligned");

        // confirm that no update was captured
        assertThat(updateCaptor.getAllValues()).isEmpty();
    }

}
