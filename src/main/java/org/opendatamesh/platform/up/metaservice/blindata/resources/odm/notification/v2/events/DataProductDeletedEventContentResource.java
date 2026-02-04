package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.events;

public class DataProductDeletedEventContentResource {
    private String dataProductUuid;
    private String dataProductFqn;

    public DataProductDeletedEventContentResource() {
    }

    public DataProductDeletedEventContentResource(String dataProductUuid, String dataProductFqn) {
        this.dataProductUuid = dataProductUuid;
        this.dataProductFqn = dataProductFqn;
    }

    public String getDataProductUuid() {
        return dataProductUuid;
    }

    public void setDataProductUuid(String dataProductUuid) {
        this.dataProductUuid = dataProductUuid;
    }

    public String getDataProductFqn() {
        return dataProductFqn;
    }

    public void setDataProductFqn(String dataProductFqn) {
        this.dataProductFqn = dataProductFqn;
    }
}
