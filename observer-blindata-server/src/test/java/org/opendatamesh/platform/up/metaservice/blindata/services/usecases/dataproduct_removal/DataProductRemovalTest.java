package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_removal;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;

import static org.mockito.Mockito.*;

public class DataProductRemovalTest {
    @Test
    public void testDataProductRemoval() throws UseCaseExecutionException {
        DataProductRemovalInitialState initialState = new DataProductRemovalInitialState();
        initialState.setFqn("FQN");
        DataProductRemovalOdmOutputPort odmOutputPort = new DataProductRemovalOdmOutputPortMock(initialState);
        DataProductRemovalBlindataOutputPort blindataOutputPort = mock(DataProductRemovalBlindataOutputPort.class);

        new DataProductRemoval(odmOutputPort, blindataOutputPort).execute();

        verify(blindataOutputPort, times(1)).deleteDataProduct("FQN");
    }
}
