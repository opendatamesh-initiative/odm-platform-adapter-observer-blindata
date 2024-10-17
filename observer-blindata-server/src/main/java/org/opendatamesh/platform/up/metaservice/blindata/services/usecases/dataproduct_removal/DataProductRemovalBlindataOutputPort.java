package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_removal;

interface DataProductRemovalBlindataOutputPort {
    void deleteDataProduct(String fullyQualifiedName);
}
