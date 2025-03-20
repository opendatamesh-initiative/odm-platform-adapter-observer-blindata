package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

import lombok.Data;

import java.util.Date;

@Data
public class BDErrorRes {

    String status;

    long timestamp = new Date().getTime();

    String error;

    String message;

    String path;

    String errorName;

}
