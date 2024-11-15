package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.eventstates;

import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;

public class DataProductEventState {

    private DataProductResource dataProduct;

    public DataProductEventState() {
    }

    public DataProductEventState(DataProductResource dataProduct) {
        this.dataProduct = dataProduct;
    }

    public DataProductResource getDataProduct() {
        return dataProduct;
    }

    public void setDataProduct(DataProductResource dataProduct) {
        this.dataProduct = dataProduct;
    }
}