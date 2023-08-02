package org.opendatamesh.platform.up.metaservice.blindata.resources;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class BlindataException {

    String status;

    Timestamp timestamp;

    String error;

    String message;

    String path;

    String errorName;

}
