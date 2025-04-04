package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_removal;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDDataProductRes;

import java.util.Optional;

interface DataProductRemovalBlindataOutboundPort {

    Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName);

    void deleteDataProduct(String dataProductUuid);
}
