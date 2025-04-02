package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.eventstates;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.OdmDataProductResource;

public class DataProductEventState {

    private OdmDataProductResource dataProduct;

    public DataProductEventState() {
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