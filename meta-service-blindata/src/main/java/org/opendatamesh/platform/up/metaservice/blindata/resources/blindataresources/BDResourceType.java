package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

public enum BDResourceType {

    DATA_CATEGORY,
    LOGICAL_FIELD,
    LOGICAL_RELATION,
    /**
     * Processing Registry
     */
    DATA_ACTOR,
    PROCESSING,
    TASK_ASSIGNMENT,
    TASK,
    /**
     * Physical Model
     */
    SYSTEM,
    PHYSICAL_ENTITY,
    PHYSICAL_FIELD,
    SYSTEM_ROUTINE,
    PHYSICAL_CONSTRAINT,
    /**
     * Data Quality
     */
    QUALITY_CHECK,
    QUALITY_SUITE,
    QUALITY_RESULT,
    QUALITY_PROBE_DEFINITION,
    QUALITY_PROBE_PROJECT,
    /**
     * Issue Management
     */
    ISSUE,
    /**
     * Query Parser
     */
    QUERY_BUCKET,
    QUERY_STATEMENT,
    /**
     * Consents
     */
    CONTRACT,
    /**
     * Data Mesh
     */
    DATA_PRODUCT,
    DATA_PRODUCT_PORT;
}
