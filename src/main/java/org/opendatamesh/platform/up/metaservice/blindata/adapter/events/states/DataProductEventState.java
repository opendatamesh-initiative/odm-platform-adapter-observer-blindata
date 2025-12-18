package org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.OdmDataProductResource;

public class DataProductEventState implements EventState {

    private OdmDataProductResource dataProduct;

    public DataProductEventState() {
        // default constructor
    }

    public DataProductEventState(OdmDataProductResource dataProduct) {
        this.dataProduct = dataProduct;
    }

    public OdmDataProductResource getDataProduct() {
        return dataProduct;
    }

    public void setDataProduct(OdmDataProductResource dataProduct) {
        this.dataProduct = dataProduct;
    }
}


