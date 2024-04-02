package org.opendatamesh.platform.up.metaservice.blindata.resources.exceptions;

import org.opendatamesh.platform.up.metaservice.blindata.client.utils.ClientException;

public class BlindataClientException extends ClientException {
    public BlindataClientException(int statusCode, String responseBody) {
        super(statusCode, responseBody);
    }
}
