package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.OdmExternalComponentResource;

import java.util.List;

public interface OdmRegistryClient {

    List<OdmExternalComponentResource> getApi(String apiName, String apiVersion);

    DataProductVersionDPDS getDataProductVersion(String dataProductId, String versionNumber);
}
