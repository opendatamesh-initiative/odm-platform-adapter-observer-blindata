package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDSystemRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDSystemSearchOptions;

import java.util.Optional;

public interface BdSystemClient {

    Optional<BDSystemRes> getSystem(BDSystemSearchOptions searchOptions);
}
