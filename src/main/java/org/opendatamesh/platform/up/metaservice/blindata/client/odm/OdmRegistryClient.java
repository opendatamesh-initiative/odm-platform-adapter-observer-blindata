package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.v1.OdmExternalComponentResource;

import java.util.List;
import java.util.Optional;

public interface OdmRegistryClient {

    List<OdmExternalComponentResource> getApis(String apiName, String apiVersion);

    Optional<JsonNode> getApi(String identifier);

    JsonNode getDataProductVersion(String dataProductId, String versionNumber);
}
