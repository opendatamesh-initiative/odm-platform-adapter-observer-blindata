package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload;

import org.opendatamesh.dpds.model.info.Info;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.OdmDataProductResource;

class DataProductUploadOdmOutboundPortImpl implements DataProductUploadOdmOutboundPort {

    private final Info dataProductInfo;

    public DataProductUploadOdmOutboundPortImpl(Info dataProductInfo) {
        this.dataProductInfo = dataProductInfo;
    }

    public DataProductUploadOdmOutboundPortImpl(OdmDataProductResource odmDataProduct) {
        Info info = new Info();
        info.setDomain(odmDataProduct.getDomain());
        info.setFullyQualifiedName(odmDataProduct.getFullyQualifiedName());
        info.setDescription(odmDataProduct.getDescription());
        this.dataProductInfo = info;
    }

    @Override
    public Info getDataProductInfo() {
        return dataProductInfo;
    }
}
