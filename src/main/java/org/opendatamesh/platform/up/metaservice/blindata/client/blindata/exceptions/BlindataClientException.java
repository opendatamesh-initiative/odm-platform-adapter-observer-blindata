package org.opendatamesh.platform.up.metaservice.blindata.client.blindata.exceptions;

import org.opendatamesh.platform.up.metaservice.blindata.client.utils.exceptions.ClientException;

public class BlindataClientException extends ClientException {
    public BlindataClientException(int statusCode, String responseBody) {
        super(statusCode, responseBody);
    }
}
