package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_removal;

public class DataProductRemovalOdmOutputPortMock implements DataProductRemovalOdmOutputPort {

    private final DataProductRemovalInitialState initialState;

    public DataProductRemovalOdmOutputPortMock(DataProductRemovalInitialState initialState) {
        this.initialState = initialState;
    }

    @Override
    public String getDataProductFullyQualifiedName() {
        return initialState.getFqn();
    }
}
