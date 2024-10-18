package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload;

import org.opendatamesh.dpds.model.info.InfoDPDS;

public class DataProductUploadOdmOutboundPortMock implements DataProductUploadOdmOutboundPort {

    private final DataProductUploadInitialState initialState;

    DataProductUploadOdmOutboundPortMock(DataProductUploadInitialState initialState) {
        this.initialState = initialState;
    }

    @Override
    public InfoDPDS getDataProductInfo() {
        return initialState.getDataProductInfo();
    }
}
