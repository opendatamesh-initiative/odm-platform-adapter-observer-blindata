package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDDataProductSearchOptions;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.BDProductPortAssetsRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.exceptions.BlindataClientException;
import org.opendatamesh.platform.up.metaservice.blindata.resources.exceptions.BlindataClientResourceMappingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BDDataProductClient {
    Optional<BDDataProductRes> getDataProduct(String identifier);

    BDDataProductRes createDataProduct(BDDataProductRes dataProduct) throws BlindataClientException, BlindataClientResourceMappingException;

    BDDataProductRes updateDataProduct(BDDataProductRes dataProduct) throws BlindataClientException, BlindataClientResourceMappingException;

    Page<BDDataProductRes> getDataProducts(Pageable pageable, BDDataProductSearchOptions filters) throws BlindataClientException, BlindataClientResourceMappingException;

    void deleteDataProduct(String dataProductIdentifier) throws BlindataClientException, BlindataClientResourceMappingException;

    BDProductPortAssetsRes createDataProductAssets(BDProductPortAssetsRes dataProductPortAssets) throws BlindataClientException, BlindataClientResourceMappingException;
}
