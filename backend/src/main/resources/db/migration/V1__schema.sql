CREATE TABLE storage_sources
(
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    type           VARCHAR(30)  NOT NULL,
    display_name   VARCHAR(255) NOT NULL,
    created_at     DATETIME(6)  NOT NULL,
    last_synced_at DATETIME(6)  NULL,

    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


CREATE TABLE files
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    name       VARCHAR(255) NOT NULL,
    mime_type  VARCHAR(255) NOT NULL,
    size       BIGINT       NOT NULL,
    created_at DATETIME(6)  NOT NULL,
    updated_at DATETIME(6)  NOT NULL,

    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


CREATE TABLE file_locations
(
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    file_id           BIGINT        NOT NULL,
    storage_source_id BIGINT        NOT NULL,
    external_id       VARCHAR(255)  NOT NULL,
    path              VARCHAR(2048) NULL,
    modified_at       DATETIME(6)   NULL,

    PRIMARY KEY (id),

    CONSTRAINT uk_file_locations_storage_source_external_id
        UNIQUE (storage_source_id, external_id),

    CONSTRAINT fk_file_locations_file
        FOREIGN KEY (file_id)
            REFERENCES files (id),

    CONSTRAINT fk_file_locations_storage_source
        FOREIGN KEY (storage_source_id)
            REFERENCES storage_sources (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;