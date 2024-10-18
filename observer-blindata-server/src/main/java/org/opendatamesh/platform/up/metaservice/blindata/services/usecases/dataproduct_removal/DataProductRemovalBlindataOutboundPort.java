package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_removal;

interface DataProductRemovalBlindataOutboundPort {
    void deleteDataProduct(String fullyQualifiedName);
}
