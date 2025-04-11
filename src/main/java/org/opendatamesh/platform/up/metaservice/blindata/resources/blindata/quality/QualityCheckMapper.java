package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;

@Mapper(componentModel = "spring")
public interface QualityCheckMapper {
    BDQualityCheckRes toBlindataRes(QualityCheck internalRes);
}
