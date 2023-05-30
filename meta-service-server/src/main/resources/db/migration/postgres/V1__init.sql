CREATE SCHEMA IF NOT EXISTS "PUBLIC";

CREATE TABLE "PUBLIC"."NOTIFICATION"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,

    "EVENT_ID" BIGINT
    "EVENT_ENTITY_ID" VARCHAR(255),
    "EVENT_BEFORE_STATE" VARCHAR,
    "EVENT_AFTER_STATE" VARCHAR,
    "EVENT_TIME" TIMESTAMP,

    "STATUS" VARCHAR(255),
    "PROCESSING_OUTPUT" VARCHAR,
    "RECEIVED_AT" TIMESTAMP,
    "PROCESSED_AT" TIMESTAMP
);
