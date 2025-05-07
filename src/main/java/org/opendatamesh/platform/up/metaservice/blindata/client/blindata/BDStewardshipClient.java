package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDStewardshipResponsibilityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDStewardshipRoleRes;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.exceptions.BlindataClientException;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.exceptions.BlindataClientResourceMappingException;

import java.util.Optional;

public interface BDStewardshipClient {
    Optional<BDStewardshipResponsibilityRes> getActiveResponsibility(String userUuid, String resourceIdentifier) throws BlindataClientException, BlindataClientResourceMappingException;

    BDStewardshipResponsibilityRes createResponsibility(BDStewardshipResponsibilityRes responsibilityRes) throws BlindataClientException, BlindataClientResourceMappingException;

    BDStewardshipRoleRes getRole(String roleUuid) throws BlindataClientException, BlindataClientResourceMappingException;

}
