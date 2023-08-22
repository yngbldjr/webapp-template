create table SAMPLE_SERVICE_TABLE (
    ID integer primary key,
    FIELD1 varchar(50),
    FIELD2 varchar(50),
    LONG_NAME varchar(500),
    JSON_DATA jsonb,
    CREATED_DATETIME timestamp without time zone
      DEFAULT (current_timestamp AT TIME ZONE 'UTC'),
    LAST_UPDATED_DATETIME timestamp without time zone
        DEFAULT (current_timestamp AT TIME ZONE 'UTC')
)
