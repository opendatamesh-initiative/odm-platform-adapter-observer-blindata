package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_removal;

class DataProductRemovalOdmOutputPortImpl implements DataProductRemovalOdmOutputPort {

    private final String dataProductFqn;

    public DataProductRemovalOdmOutputPortImpl(String dataProductFqn) {
        this.dataProductFqn = dataProductFqn;
    }

    @Override
    public String getDataProductFullyQualifiedName() {
        return dataProductFqn;
    }
}
