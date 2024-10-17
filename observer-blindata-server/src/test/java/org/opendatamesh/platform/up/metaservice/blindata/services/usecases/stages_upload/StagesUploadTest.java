package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.stages_upload;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class StagesUploadTest {
    private ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Test
    public void testDataProductVersionUpload() throws IOException, UseCaseExecutionException {
        StagesUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_1.json")),
                StagesUploadInitialState.class
        );

        StagesUploadOdmOutputPort odmOutputPort = new StagesUploadOdmOutputPortMock(initialState);
        StagesUploadBlindataOutputPort blindataOutputPort = spy(new StagesUploadBlindataOutputPortMock(initialState));

        new StagesUpload(blindataOutputPort, odmOutputPort).execute();
        verify(blindataOutputPort, times(1)).findDataProduct(initialState.getExistentDataProduct().getIdentifier());
        verify(blindataOutputPort, times(1)).uploadDataProductStages(initialState.getExistentDataProduct().getUuid(), initialState.getDataProductVersionStages());
    }
}
