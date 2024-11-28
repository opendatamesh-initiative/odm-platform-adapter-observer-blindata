package org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources;

public enum BDResourceType {
    /**
     * Logical Model
     */
    DATA_CATEGORY,
    LOGICAL_FIELD,
    LOGICAL_RELATION,
    LOGICAL_NAMESPACE,
    LOGICAL_PREDICATE,
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
    DATA_PRODUCT_PORT,
    DATA_PRODUCT_DOMAIN,
    /**
     * Policies
     */
    GOVERNANCE_POLICY_SUITE,
    GOVERNANCE_POLICY,

    /**
     * Profiling
     */
    PROFILING_MONITOR,
    PROFILING_METRIC_DEFINITION,
    PROFILING_METRIC_INCIDENT;
}
