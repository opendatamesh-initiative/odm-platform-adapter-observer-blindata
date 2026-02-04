package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.dataproduct_removal;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.exceptions.UseCaseExecutionException;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class DataProductRemovalTest {
    @Test
    public void testDataProductRemovalExisting() throws UseCaseExecutionException {
        DataProductRemovalInitialState initialState = new DataProductRemovalInitialState();
        initialState.setFqn("FQN");
        BDDataProductRes dataProductBlindata = new BDDataProductRes();
        dataProductBlindata.setIdentifier(initialState.getFqn());
        dataProductBlindata.setUuid("uuid");

        DataProductRemovalOdmOutboundPort odmOutboundPort = new DataProductRemovalOdmOutboundPortMock(initialState);
        DataProductRemovalBlindataOutboundPort blindataOutboundPort = mock(DataProductRemovalBlindataOutboundPort.class);

        when(blindataOutboundPort.findDataProduct("FQN")).thenReturn(Optional.of(dataProductBlindata));

        new DataProductRemoval(odmOutboundPort, blindataOutboundPort).execute();

        verify(blindataOutboundPort, times(1)).deleteDataProduct(dataProductBlindata.getUuid());
    }

    @Test
    public void testDataProductRemovalAbsent() throws UseCaseExecutionException {
        DataProductRemovalInitialState initialState = new DataProductRemovalInitialState();
        initialState.setFqn("FQN");
        BDDataProductRes dataProductBlindata = new BDDataProductRes();
        dataProductBlindata.setIdentifier("FQN2");
        dataProductBlindata.setUuid("uuid");

        DataProductRemovalOdmOutboundPort odmOutboundPort = new DataProductRemovalOdmOutboundPortMock(initialState);
        DataProductRemovalBlindataOutboundPort blindataOutboundPort = mock(DataProductRemovalBlindataOutboundPort.class);

        when(blindataOutboundPort.findDataProduct("FQN")).thenReturn(Optional.empty());

        new DataProductRemoval(odmOutboundPort, blindataOutboundPort).execute();

        verify(blindataOutboundPort, times(0)).deleteDataProduct(any());
    }
}
