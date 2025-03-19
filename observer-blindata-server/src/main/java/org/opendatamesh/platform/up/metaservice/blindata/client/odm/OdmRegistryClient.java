package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.registry.api.resources.ExternalComponentResource;

import java.util.List;

public interface OdmRegistryClient {

    List<ExternalComponentResource> getApi(String apiName, String apiVersion);

    DataProductVersionDPDS getDataProductVersion(String dataProductId, String versionNumber);
}
