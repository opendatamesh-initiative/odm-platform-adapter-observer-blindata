package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductSearchOptions;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductStagesUploadRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDProductPortAssetsRes;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.exceptions.BlindataClientException;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.exceptions.BlindataClientResourceMappingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BdDataProductClient {
    Optional<BDDataProductRes> getDataProduct(String identifier);

    BDDataProductRes createDataProduct(BDDataProductRes dataProduct) throws BlindataClientException, BlindataClientResourceMappingException;

    BDDataProductRes putDataProduct(BDDataProductRes dataProduct) throws BlindataClientException, BlindataClientResourceMappingException;

    BDDataProductRes patchDataProduct(BDDataProductRes dataProduct) throws BlindataClientException, BlindataClientResourceMappingException;

    Page<BDDataProductRes> getDataProducts(Pageable pageable, BDDataProductSearchOptions filters) throws BlindataClientException, BlindataClientResourceMappingException;

    void deleteDataProduct(String dataProductUuid) throws BlindataClientException, BlindataClientResourceMappingException;

    BDProductPortAssetsRes createDataProductAssets(BDProductPortAssetsRes dataProductPortAssets) throws BlindataClientException, BlindataClientResourceMappingException;

    void uploadStages(BDDataProductStagesUploadRes stagesUploadRes);
}
