package org.opendatamesh.platform.up.metaservice.blindata.old.v1.registryservice;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.v1.OdmExternalComponentResource;

import java.util.List;
import java.util.Optional;

/**
 * Client of the Registry V1 routes ({@code /api/v1/pp/registry/...}), which the Registry V2 server still serves.
 * <p>
 * <b>Temporary:</b> removed with the {@code old} package, once the observer no longer reads Registry V1 routes.
 * See {@code old/README.md}.
 */
public interface OdmRegistryClientV1 {

    List<OdmExternalComponentResource> getApis(String apiName, String apiVersion);

    Optional<JsonNode> getApi(String identifier);

    JsonNode getDataProductVersion(String dataProductId, String versionNumber);
}
