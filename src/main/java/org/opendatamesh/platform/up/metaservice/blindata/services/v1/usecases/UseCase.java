package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases;

import org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.exceptions.UseCaseExecutionException;

public interface UseCase {
    void execute() throws UseCaseExecutionException;
}
