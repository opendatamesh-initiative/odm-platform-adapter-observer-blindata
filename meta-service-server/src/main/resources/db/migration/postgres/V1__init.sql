CREATE SCHEMA IF NOT EXISTS "PUBLIC";
CREATE SEQUENCE "PUBLIC".HIBERNATE_SEQUENCE START WITH 1;

CREATE TABLE "PUBLIC".notification(
    sequence_id serial primary key,
    status varchar(255),
    processing_output varchar(255),
    event_id bigint,
    notification_id bigint references notification(sequence_id),
    event_type varchar(255),
    event_entity_id varchar(255),
    event_before_state text,
    event_after_state text,
    event_time timestamp,
    received_at timestamp,
    processed_at timestamp
);
