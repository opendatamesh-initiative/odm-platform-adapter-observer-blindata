package org.opendatamesh.platform.up.metaservice.blindata.services.usecases;

import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseExecutionException;

public interface UseCase {
    void execute() throws UseCaseExecutionException;
}
