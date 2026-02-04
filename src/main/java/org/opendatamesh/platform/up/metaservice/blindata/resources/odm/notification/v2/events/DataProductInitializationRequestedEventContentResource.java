package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.events;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.v2.OdmDataProductResourceV2;

public class DataProductInitializationRequestedEventContentResource {
    private OdmDataProductResourceV2 dataProduct;

    DataProductInitializationRequestedEventContentResource() {
        //DO NOTHING
    }

    public OdmDataProductResourceV2 getDataProduct() {
        return dataProduct;
    }

    public void setDataProduct(OdmDataProductResourceV2 dataProduct) {
        this.dataProduct = dataProduct;
    }
}
