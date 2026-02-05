package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload;

import org.opendatamesh.dpds.model.info.Info;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.v1.OdmDataProductResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.v2.OdmDataProductResourceV2;

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

    public DataProductUploadOdmOutboundPortImpl(OdmDataProductResourceV2 odmDataProduct) {
        Info info = new Info();
        info.setDomain(odmDataProduct.getDomain());
        info.setFullyQualifiedName(odmDataProduct.getFqn());
        info.setDescription(odmDataProduct.getDescription());
        info.setName(odmDataProduct.getName());
        info.setDisplayName(odmDataProduct.getDisplayName());
        this.dataProductInfo = info;
    }

    @Override
    public Info getDataProductInfo() {
        return dataProductInfo;
    }
}
