package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload;

import org.opendatamesh.dpds.model.info.InfoDPDS;

public class DataProductUploadOdmOutputPortMock implements DataProductUploadOdmOutputPort {

    private final DataProductUploadInitialState initialState;

    DataProductUploadOdmOutputPortMock(DataProductUploadInitialState initialState) {
        this.initialState = initialState;
    }

    @Override
    public InfoDPDS getDataProductInfo() {
        return initialState.getDataProductInfo();
    }
}
