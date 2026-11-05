package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.devops.v2;

/**
 * Status of a DevOps 2.0 activity. Mirrors DevOps {@code ExecutionStatus}.
 */
public enum OdmActivityStatusV2 {
    PENDING,
    RUNNING,
    SUCCEEDED,
    FAILED,
    CANCELED
}
