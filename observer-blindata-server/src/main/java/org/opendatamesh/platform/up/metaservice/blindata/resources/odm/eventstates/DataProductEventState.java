package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.eventstates;

import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;

public class DataProductEventState {

    private DataProductResource dataProduct;
    private DataProductVersionDPDS dataProductVersion;

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

    public DataProductVersionDPDS getDataProductVersion() {
        return dataProductVersion;
    }

    public void setDataProductVersion(DataProductVersionDPDS dataProductVersion) {
        this.dataProductVersion = dataProductVersion;
    }
}