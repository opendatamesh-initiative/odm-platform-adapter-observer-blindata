package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.dataproduct_removal;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;

import java.util.Optional;

interface DataProductRemovalBlindataOutboundPort {

    Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName);

    void deleteDataProduct(String dataProductUuid);
}
