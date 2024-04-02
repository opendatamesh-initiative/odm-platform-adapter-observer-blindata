package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.server.services.MetaServiceException;

import java.util.List;

public interface OdmRegistryClient {

    List<Integer> getSchemasId(String portID) throws MetaServiceException;

    String getSchemaContent(Integer schemaID) throws MetaServiceException;
}
