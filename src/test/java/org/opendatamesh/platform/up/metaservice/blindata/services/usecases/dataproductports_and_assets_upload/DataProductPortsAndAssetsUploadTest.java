package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductports_and_assets_upload;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDAdditionalPropertiesRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductPortRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

        DataProductPortsAndAssetsUploadOdmOutboundPort odmOutboundPort =
                new DataProductPortsAndAssetsUploadOdmOutboundPortMock(initialState);
        DataProductPortsAndAssetsUploadBlindataOutboundPort blindataOutboundPort =
                spy(new DataProductPortsAndAssetsUploadBlindataOutboundPortMock(initialState));

        new DataProductPortsAndAssetsUpload(blindataOutboundPort, odmOutboundPort).execute();

        verify(blindataOutboundPort, times(1))
                .findDataProduct(initialState.getExistentDataProduct().getIdentifier());

        ArgumentCaptor<BDDataProductRes> prodCaptor = ArgumentCaptor.forClass(BDDataProductRes.class);
        verify(blindataOutboundPort, times(1))
                .updateDataProductPorts(prodCaptor.capture());
        verify(blindataOutboundPort, times(1))
                .createDataProductAssets(any());

        BDDataProductRes expectedDp = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("end_state_2_data_product.json")),
                BDDataProductRes.class
        );
        BDDataProductRes actualDp = prodCaptor.getValue();

        assertThat(actualDp)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedDp);
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

    @Test
    public void testDataProductVersionUploadPortAdditionalProperties() throws IOException, UseCaseExecutionException {
        DataProductVersionUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_3.json")),
                DataProductVersionUploadInitialState.class
        );

        DataProductPortsAndAssetsUploadOdmOutboundPort odmOutboundPort =
                new DataProductPortsAndAssetsUploadOdmOutboundPortMock(initialState);
        DataProductPortsAndAssetsUploadBlindataOutboundPort blindataOutboundPort =
                spy(new DataProductPortsAndAssetsUploadBlindataOutboundPortMock(initialState));

        new DataProductPortsAndAssetsUpload(blindataOutboundPort, odmOutboundPort).execute();

        verify(blindataOutboundPort, times(1))
                .findDataProduct(initialState.getExistentDataProduct().getIdentifier());

        ArgumentCaptor<BDDataProductRes> dataProductCaptor =
                ArgumentCaptor.forClass(BDDataProductRes.class);

        verify(blindataOutboundPort, times(1))
                .updateDataProductPorts(dataProductCaptor.capture());

        verify(blindataOutboundPort, times(1))
                .createDataProductAssets(any());

        List<BDDataProductPortRes> ports = dataProductCaptor.getValue().getPorts();
        assertThat(ports).isNotEmpty();

        List<BDAdditionalPropertiesRes> expectedPortProps = List.of(
                new BDAdditionalPropertiesRes("prop", "custom-prop-value")
        );
        ports.forEach(port -> {
            assertThat(port.getAdditionalProperties())
                    .usingRecursiveComparison()
                    .ignoringCollectionOrder()
                    .isEqualTo(expectedPortProps);
        });
    }

}
