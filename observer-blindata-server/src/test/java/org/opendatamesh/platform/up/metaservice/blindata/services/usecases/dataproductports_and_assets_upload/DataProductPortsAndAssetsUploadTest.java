package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class DataProductPortsAndAssetsUploadTest {
    private ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Test
    public void testDataProductVersionUpload() throws IOException, UseCaseExecutionException {
        DataProductVersionUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_1.json")),
                DataProductVersionUploadInitialState.class
        );

        DataProductPortsAndAssetsUploadOdmOutboundPort odmOutboundPort = new DataProductPortsAndAssetsUploadOdmOutboundPortMock(initialState);
        DataProductPortsAndAssetsUploadBlindataOutboundPort blindataOutboundPort = spy(new DataProductPortsAndAssetsUploadBlindataOutboundPortMock(initialState));

        new DataProductPortsAndAssetsUpload(blindataOutboundPort, odmOutboundPort).execute();
        verify(blindataOutboundPort, times(1)).findDataProduct(initialState.getExistentDataProduct().getIdentifier());
        verify(blindataOutboundPort, times(1)).updateDataProductPorts(any());
        verify(blindataOutboundPort, times(1)).createDataProductAssets(any());
    }

    @Test
    public void testDataProductVersionUploadWithPortDependsOn() throws IOException, UseCaseExecutionException {
        DataProductVersionUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_2.json")),
                DataProductVersionUploadInitialState.class
        );

        DataProductPortsAndAssetsUploadOdmOutboundPort odmOutboundPort = new DataProductPortsAndAssetsUploadOdmOutboundPortMock(initialState);
        DataProductPortsAndAssetsUploadBlindataOutboundPort blindataOutboundPort = spy(new DataProductPortsAndAssetsUploadBlindataOutboundPortMock(initialState));

        new DataProductPortsAndAssetsUpload(blindataOutboundPort, odmOutboundPort).execute();
        verify(blindataOutboundPort, times(1)).findDataProduct(initialState.getExistentDataProduct().getIdentifier());
        BDDataProductRes generatedDataProduct = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("end_state_2_data_product.json")),
                BDDataProductRes.class
        );
        verify(blindataOutboundPort, times(1)).updateDataProductPorts(generatedDataProduct);
        verify(blindataOutboundPort, times(1)).createDataProductAssets(any());
    }


    @Test
    public void testDataProductVersionUploadDryRun() throws UseCaseExecutionException, IOException {
        DataProductVersionUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_1.json")),
                DataProductVersionUploadInitialState.class
        );

        DataProductPortsAndAssetsUploadOdmOutboundPort odmOutboundPort = new DataProductPortsAndAssetsUploadOdmOutboundPortMock(initialState);
        DataProductPortsAndAssetsUploadBlindataOutboundPort blindataOutboundPort = spy(new DataProductPortsAndAssetsUploadBlindataOutboundPortMock(initialState));

        new DataProductPortsAndAssetsUpload(new DataProductPortsAndAssetsUploadBlindataOutboundPortDryRunImpl(blindataOutboundPort), odmOutboundPort).execute();
        verify(blindataOutboundPort, times(1)).findDataProduct(initialState.getExistentDataProduct().getIdentifier());
        verify(blindataOutboundPort, times(0)).updateDataProductPorts(any());
        verify(blindataOutboundPort, times(0)).createDataProductAssets(any());
    }

    @Test
    public void testDataProductVersionUploadWithPortDependsOnDryRun() throws UseCaseExecutionException, IOException {
        DataProductVersionUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_2.json")),
                DataProductVersionUploadInitialState.class
        );

        DataProductPortsAndAssetsUploadOdmOutboundPort odmOutboundPort = new DataProductPortsAndAssetsUploadOdmOutboundPortMock(initialState);
        DataProductPortsAndAssetsUploadBlindataOutboundPort blindataOutboundPort = spy(new DataProductPortsAndAssetsUploadBlindataOutboundPortMock(initialState));

        new DataProductPortsAndAssetsUpload(new DataProductPortsAndAssetsUploadBlindataOutboundPortDryRunImpl(blindataOutboundPort), odmOutboundPort).execute();
        verify(blindataOutboundPort, times(1)).findDataProduct(initialState.getExistentDataProduct().getIdentifier());
        BDDataProductRes generatedDataProduct = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("end_state_2_data_product.json")),
                BDDataProductRes.class
        );
        verify(blindataOutboundPort, times(0)).updateDataProductPorts(generatedDataProduct);
        verify(blindataOutboundPort, times(0)).createDataProductAssets(any());
    }
}
