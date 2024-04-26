package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import java.util.List;

public interface OdmRegistryClient {

    List<Integer> getSchemasId(String portID);

    String getSchemaContent(Integer schemaID);
}
