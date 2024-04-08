package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class BDErrorRes {

    String status;

    Timestamp timestamp;

    String error;

    String message;

    String path;

    String errorName;

}
