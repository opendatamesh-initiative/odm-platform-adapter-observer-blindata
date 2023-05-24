-- H2 2.1.214;
CREATE USER IF NOT EXISTS "SA" SALT '582a7e34517160c6' HASH '6b2d59d21c92ab85fb76c10fe4a0873b2745eeeae07aa2001a8c42a5577baf72' ADMIN;
CREATE SEQUENCE "PUBLIC"."HIBERNATE_SEQUENCE" START WITH 1;

CREATE TABLE notification(
    sequence_id bigint primary key auto_increment,
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
