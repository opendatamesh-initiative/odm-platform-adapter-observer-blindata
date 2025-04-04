package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_removal;

class DataProductRemovalOdmOutboundPortImpl implements DataProductRemovalOdmOutboundPort {

    private final String dataProductFqn;

    public DataProductRemovalOdmOutboundPortImpl(String dataProductFqn) {
        this.dataProductFqn = dataProductFqn;
    }

    @Override
    public String getDataProductFullyQualifiedName() {
        return dataProductFqn;
    }
}
