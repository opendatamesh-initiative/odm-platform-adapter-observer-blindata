package org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product;

public enum BDPolicyEvaluationEvent {

    DATA_PRODUCT_CREATION,
    DATA_PRODUCT_UPDATE,
    DATA_PRODUCT_VERSION_CREATION,
    ACTIVITY_STAGE_TRANSITION,
    TASK_EXECUTION_RESULT,
    ACTIVITY_EXECUTION_RESULT,

    //Not supported in ODM
    PROVISIONING_RESULT,
    DOMAIN_CREATION,
    DOMAIN_UPDATE
}
