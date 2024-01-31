package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

import lombok.Data;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.PhysicalEntityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.SystemRes;

import java.util.List;
@Data
public class DataProductPortAssetSystemRes {

    private SystemRes system;
    private List<PhysicalEntityRes> physicalEntities;

}

