CREATE TABLE storage_credentials
(
    id                       BIGINT        NOT NULL AUTO_INCREMENT,
    storage_source_id        BIGINT        NOT NULL,

    credential_type          VARCHAR(50)   NOT NULL,
    credential_status        VARCHAR(50)   NOT NULL,

    external_account_id      VARCHAR(255)  NULL,
    scope                    TEXT          NULL,

    credential_data          TEXT          NOT NULL,
    credential_schema_version INT          NOT NULL DEFAULT 1,

    expires_at               DATETIME(6)   NULL,
    refresh_expires_at       DATETIME(6)   NULL,
    last_refreshed_at        DATETIME(6)   NULL,

    created_at               DATETIME(6)   NOT NULL,
    updated_at               DATETIME(6)   NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT uk_storage_credentials_storage_source
        UNIQUE (storage_source_id),

    CONSTRAINT fk_storage_credentials_storage_source
        FOREIGN KEY (storage_source_id)
            REFERENCES storage_sources (id)
            ON DELETE CASCADE
);