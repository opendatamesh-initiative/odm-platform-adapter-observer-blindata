package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproductversion_upload;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class DataProductVersionUploadTest {
    private ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Test
    public void testDataProductVersionUpload() throws IOException, UseCaseExecutionException {
        DataProductVersionUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_1.json")),
                DataProductVersionUploadInitialState.class
        );

        DataProductVersionUploadOdmOutputPort odmOutputPort = new DataProductVersionUploadOdmOutputPortMock(initialState);
        DataProductVersionUploadBlindataOutputPort blindataOutputPort = spy(new DataProductVersionUploadBlindataOutputPortMock(initialState));

        new DataProductVersionUpload(blindataOutputPort, odmOutputPort).execute();
        verify(blindataOutputPort, times(1)).findDataProduct(initialState.getExistentDataProduct().getIdentifier());
        verify(blindataOutputPort, times(1)).updateDataProductPorts(any());
        verify(blindataOutputPort, times(1)).createDataProductAssets(any());
        verify(blindataOutputPort, times(1)).uploadDataProductStages(initialState.getExistentDataProduct().getUuid(), initialState.getDataProductVersionStages());
    }
}
