package org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states;

import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.OdmDataProductResource;

public class DataProductEventState implements EventState {

    private OdmDataProductResource dataProduct;
    private DataProductVersion dataProductVersion;

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

    public DataProductVersion getDataProductVersion() {
        return dataProductVersion;
    }

    public void setDataProductVersion(DataProductVersion dataProductVersion) {
        this.dataProductVersion = dataProductVersion;
    }
}