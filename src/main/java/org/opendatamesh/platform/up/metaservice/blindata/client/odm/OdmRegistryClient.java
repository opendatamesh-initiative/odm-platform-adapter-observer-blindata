package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.v1.OdmExternalComponentResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.v2.OdmDataProductVersionResourceV2;

import java.util.List;
import java.util.Optional;

public interface OdmRegistryClient {

    List<OdmExternalComponentResource> getApis(String apiName, String apiVersion);

    Optional<JsonNode> getApi(String identifier);

    JsonNode getDataProductVersion(String dataProductId, String versionNumber);

    /**
     * Reads one published data product version from Registry V2
     * ({@code GET /api/v2/pp/registry/products-versions/{uuid}}).
     * <p>
     * <b>TEMPORARY (BDMD-5558):</b> this method exists only because DevOps 2.0 events carry no descriptor, so the
     * observer has to read it from Registry. Remove it once the events are enriched upstream (likely by Notification).
     * It works only with {@code odm.productPlane.registryService.active: true} and {@code apiVersion: v2}; otherwise it
     * throws {@link IllegalStateException}.
     *
     * @param dataProductVersionUuid the Registry uuid of the data product version
     * @return the version, or empty when Registry V2 does not know it (HTTP 404)
     */
    Optional<OdmDataProductVersionResourceV2> getDataProductVersionV2(String dataProductVersionUuid);
}
