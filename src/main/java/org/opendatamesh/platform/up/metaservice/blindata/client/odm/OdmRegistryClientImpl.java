package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.exceptions.client.ClientException;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.v2.OdmDataProductVersionResourceV2;
import org.opendatamesh.platform.up.metaservice.blindata.utils.client.RestUtils;

import java.util.Optional;

class OdmRegistryClientImpl implements OdmRegistryClient {
    private final String baseUrl;
    private final RestUtils restUtils;

    OdmRegistryClientImpl(RestUtils restUtils, String baseUrl) {
        this.restUtils = restUtils;
        this.baseUrl = baseUrl;
    }

    /**
     * TEMPORARY (BDMD-5558): see {@link OdmRegistryClient#getDataProductVersion(String)}.
     */
    @Override
    public Optional<OdmDataProductVersionResourceV2> getDataProductVersion(String dataProductVersionUuid) {
        try {
            OdmDataProductVersionResourceV2 version = restUtils.get(
                    baseUrl + "/api/v2/pp/registry/products-versions/{id}",
                    null,
                    dataProductVersionUuid,
                    OdmDataProductVersionResourceV2.class
            );
            return Optional.ofNullable(version);
        } catch (ClientException e) {
            if (isNotFound(e)) {
                return Optional.empty();
            }
            throw e;
        }
    }

    private boolean isNotFound(ClientException e) {
        return e.getCode() == 404;
    }
}
