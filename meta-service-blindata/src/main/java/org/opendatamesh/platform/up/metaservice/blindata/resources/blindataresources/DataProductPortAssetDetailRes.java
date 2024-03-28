package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

import lombok.Data;

import java.util.List;

@Data
public class DataProductPortAssetDetailRes {

    private String portIdentifier;
    private List<DataProductPortAssetSystemRes> assets;

}

