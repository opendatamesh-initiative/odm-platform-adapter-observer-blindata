package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;

import java.io.IOException;

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

        DataProductUploadOdmOutputPort odmOutputPort = new DataProductUploadOdmOutputPortMock(initialState);
        DataProductUploadBlindataOutputPort blindataOutputPort = spy(new DataProductUploadBlindataOutputPortMock(initialState));

        new DataProductUpload(odmOutputPort, blindataOutputPort).execute();

        verify(blindataOutputPort, times(1)).createDataProduct(any());
        verify(blindataOutputPort, times(0)).updateDataProduct(any());
        verify(blindataOutputPort, times(0)).createDataProductResponsibility(any(), any(), any());
    }

    @Test
    public void testDataProductAndResponsibilityCreation() throws UseCaseExecutionException, IOException {
        DataProductUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_2.json")),
                DataProductUploadInitialState.class
        );

        DataProductUploadOdmOutputPort odmOutputPort = new DataProductUploadOdmOutputPortMock(initialState);
        DataProductUploadBlindataOutputPort blindataOutputPort = spy(new DataProductUploadBlindataOutputPortMock(initialState));

        new DataProductUpload(odmOutputPort, blindataOutputPort).execute();

        verify(blindataOutputPort, times(1)).createDataProduct(any());
        verify(blindataOutputPort, times(0)).updateDataProduct(any());
        verify(blindataOutputPort, times(1)).createDataProductResponsibility(any(), any(), any());
    }


    @Test
    public void testDataProductUpdateAndResponsibilityCreation() throws UseCaseExecutionException, IOException {
        DataProductUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_3.json")),
                DataProductUploadInitialState.class
        );

        DataProductUploadOdmOutputPort odmOutputPort = new DataProductUploadOdmOutputPortMock(initialState);
        DataProductUploadBlindataOutputPort blindataOutputPort = spy(new DataProductUploadBlindataOutputPortMock(initialState));

        new DataProductUpload(odmOutputPort, blindataOutputPort).execute();

        verify(blindataOutputPort, times(0)).createDataProduct(any());
        verify(blindataOutputPort, times(1)).updateDataProduct(any());
        verify(blindataOutputPort, times(1)).createDataProductResponsibility(any(), any(), any());
    }
}
