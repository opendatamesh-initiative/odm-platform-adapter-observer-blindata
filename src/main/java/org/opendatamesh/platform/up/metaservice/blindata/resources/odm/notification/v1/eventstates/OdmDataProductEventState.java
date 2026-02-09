package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.eventstates;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.v1.OdmDataProductResource;

public class OdmDataProductEventState {

    private OdmDataProductResource dataProduct;
    private JsonNode dataProductVersion;

    public OdmDataProductEventState() {
    }

    public OdmDataProductEventState(OdmDataProductResource dataProduct) {
        this.dataProduct = dataProduct;
    }

    public OdmDataProductResource getDataProduct() {
        return dataProduct;
    }

    public void setDataProduct(OdmDataProductResource dataProduct) {
        this.dataProduct = dataProduct;
    }

    public JsonNode getDataProductVersion() {
        return dataProductVersion;
    }

    public void setDataProductVersion(JsonNode dataProductVersion) {
        this.dataProductVersion = dataProductVersion;
    }
}