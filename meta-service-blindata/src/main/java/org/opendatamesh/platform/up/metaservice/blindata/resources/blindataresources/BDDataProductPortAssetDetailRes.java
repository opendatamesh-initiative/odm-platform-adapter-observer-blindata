package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

import lombok.Data;

import java.util.List;

@Data
public class BDDataProductPortAssetDetailRes {

    private String portIdentifier;
    private List<BDProductPortAssetSystemRes> assets;

}

