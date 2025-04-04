package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_removal;

public class DataProductRemovalOdmOutboundPortMock implements DataProductRemovalOdmOutboundPort {

    private final DataProductRemovalInitialState initialState;

    public DataProductRemovalOdmOutboundPortMock(DataProductRemovalInitialState initialState) {
        this.initialState = initialState;
    }

    @Override
    public String getDataProductFullyQualifiedName() {
        return initialState.getFqn();
    }
}
