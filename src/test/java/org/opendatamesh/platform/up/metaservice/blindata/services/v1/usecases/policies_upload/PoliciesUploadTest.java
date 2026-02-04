package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.policies_upload;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.exceptions.UseCaseExecutionException;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class PoliciesUploadTest {

    private ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Test
    public void testPoliciesUpload() throws IOException, UseCaseExecutionException {
        PoliciesUploadInitialState initialState = objectMapper.readValue(
                Resources.toByteArray(getClass().getResource("initial_state_1.json")),
                PoliciesUploadInitialState.class
        );

        PoliciesUploadOdmOutboundPort odmOutboundPort = spy(new PoliciesUploadOdmOutboundPortMock(initialState));
        PoliciesUploadBlindataOutboundPort blindataOutboundPort = spy(new PoliciesUploadBlindataOutboundPortMock(initialState));

        new PoliciesUpload(blindataOutboundPort, odmOutboundPort).execute();

        verify(odmOutboundPort, times(1)).getDataProductPoliciesEvaluationResults(any());

        verify(blindataOutboundPort, times(1)).findDataProduct(initialState.getDataProductInfo().getFullyQualifiedName());
        verify(blindataOutboundPort, times(1)).createPolicyEvaluationRecords(any());
    }
}
