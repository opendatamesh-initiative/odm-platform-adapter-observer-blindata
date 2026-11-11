package org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.internal;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Recognition of V2 event type names (BDMD-5558, REASONS Op 1: exact, case-sensitive match), from
 * {@code spdd/prompt/BDMD-5558-202611041232-[Feat]-service-devops-v2-events-handling.md}.
 */
class EventTypeV2Test {

    /**
     * Every constant of the enum, the DevOps 2.0 activity outcomes included, is a known event type name.
     */
    @Test
    void whenNameIsAnEnumConstantThenItIsKnown() {
        assertThat(EventTypeV2.isKnown("ACTIVITY_SUCCEEDED")).isTrue();
        assertThat(EventTypeV2.isKnown("ACTIVITY_FAILED")).isTrue();
        for (EventTypeV2 type : EventTypeV2.values()) {
            assertThat(EventTypeV2.isKnown(type.name())).as(type.name()).isTrue();
        }
    }

    /**
     * A name that is not an enum constant, such as a DevOps event the observer does not handle, is unknown.
     */
    @Test
    void whenNameIsNotAnEnumConstantThenItIsUnknown() {
        assertThat(EventTypeV2.isKnown("SOMETHING_NEW")).isFalse();
        assertThat(EventTypeV2.isKnown("ACTIVITY_CANCELED")).isFalse();
        assertThat(EventTypeV2.isKnown("")).isFalse();
    }

    /**
     * The match is case-sensitive, as {@link EventTypeV2#valueOf(String)} is: a lowercase name is unknown.
     */
    @Test
    void whenNameHasWrongCaseThenItIsUnknown() {
        assertThat(EventTypeV2.isKnown("activity_succeeded")).isFalse();
        assertThat(EventTypeV2.isKnown("Activity_Succeeded")).isFalse();
    }

    /**
     * A null name is unknown and does not throw.
     */
    @Test
    void whenNameIsNullThenItIsUnknown() {
        assertThat(EventTypeV2.isKnown(null)).isFalse();
    }
}
