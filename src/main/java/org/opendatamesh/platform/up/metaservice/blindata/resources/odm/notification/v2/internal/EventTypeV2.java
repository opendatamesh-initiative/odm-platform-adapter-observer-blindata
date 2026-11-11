package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.internal;

import java.util.Arrays;

public enum EventTypeV2 {
    DATA_PRODUCT_INITIALIZED,
    DATA_PRODUCT_DELETED,
    DATA_PRODUCT_VERSION_PUBLISHED,
    DATA_PRODUCT_VERSION_DELETED,
    // DevOps 2.0 activity outcomes (BDMD-5558)
    ACTIVITY_SUCCEEDED,
    ACTIVITY_FAILED;

    /**
     * Tells whether the given event type name is one the observer knows.
     * The match is exact and case-sensitive, as {@link #valueOf(String)} is.
     */
    public static boolean isKnown(String type) {
        return type != null && Arrays.stream(values()).anyMatch(value -> value.name().equals(type));
    }
}
