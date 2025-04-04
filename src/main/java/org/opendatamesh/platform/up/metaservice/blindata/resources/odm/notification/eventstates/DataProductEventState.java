package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.eventstates;

import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.OdmDataProductResource;

public class DataProductEventState {

    private OdmDataProductResource dataProduct;
    private DataProductVersionDPDS dataProductVersion;

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

    public DataProductVersionDPDS getDataProductVersion() {
        return dataProductVersion;
    }

    public void setDataProductVersion(DataProductVersionDPDS dataProductVersion) {
        this.dataProductVersion = dataProductVersion;
    }
}