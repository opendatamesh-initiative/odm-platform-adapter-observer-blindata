package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

import lombok.Data;

import java.util.List;
@Data
public class BDProductPortAssetSystemRes {

    private BDSystemRes system;
    private List<BDPhysicalEntityRes> physicalEntities;

}

