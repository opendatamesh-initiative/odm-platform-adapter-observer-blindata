package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.v2.OdmDataProductVersionResourceV2;

import java.util.Optional;

/**
 * Client of the Registry V2 routes ({@code /api/v2/pp/registry/...}).
 * <p>
 * The Registry V1 routes are read by {@code old.v1.registryservice.OdmRegistryClientV1}, which is removed with the
 * {@code old} package.
 */
public interface OdmRegistryClient {

    /**
     * Reads one published data product version ({@code GET /api/v2/pp/registry/products-versions/{uuid}}).
     * <p>
     * <b>TEMPORARY (BDMD-5558):</b> this method exists only because DevOps 2.0 events carry no descriptor, so the
     * observer has to read it from Registry. Remove it once the events are enriched upstream (likely by Notification).
     * It works only with {@code odm.productPlane.registryService.active: true} and {@code apiVersion: v2}; otherwise it
     * throws {@link IllegalStateException}.
     *
     * @param dataProductVersionUuid the Registry uuid of the data product version
     * @return the version, or empty when Registry V2 does not know it (HTTP 404)
     */
    Optional<OdmDataProductVersionResourceV2> getDataProductVersion(String dataProductVersionUuid);
}
