package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload;

import org.opendatamesh.dpds.model.info.InfoDPDS;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;

public class DataProductUploadOdmOutputPortImpl implements DataProductUploadOdmOutputPort {

    private final InfoDPDS dataProductInfo;

    public DataProductUploadOdmOutputPortImpl(InfoDPDS dataProductInfo) {
        this.dataProductInfo = dataProductInfo;
    }

    public DataProductUploadOdmOutputPortImpl(DataProductResource odmDataProduct) {
        InfoDPDS infoDPDS = new InfoDPDS();
        infoDPDS.setDomain(odmDataProduct.getDomain());
        infoDPDS.setFullyQualifiedName(odmDataProduct.getFullyQualifiedName());
        infoDPDS.setDescription(odmDataProduct.getDescription());
        this.dataProductInfo = infoDPDS;
    }

    @Override
    public InfoDPDS getDataProductInfo() {
        return dataProductInfo;
    }
}
