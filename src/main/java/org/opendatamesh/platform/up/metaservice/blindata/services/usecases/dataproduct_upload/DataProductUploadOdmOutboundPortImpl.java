package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload;

import org.opendatamesh.dpds.model.info.InfoDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.OdmDataProductResource;

class DataProductUploadOdmOutboundPortImpl implements DataProductUploadOdmOutboundPort {

    private final InfoDPDS dataProductInfo;

    public DataProductUploadOdmOutboundPortImpl(InfoDPDS dataProductInfo) {
        this.dataProductInfo = dataProductInfo;
    }

    public DataProductUploadOdmOutboundPortImpl(OdmDataProductResource odmDataProduct) {
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
