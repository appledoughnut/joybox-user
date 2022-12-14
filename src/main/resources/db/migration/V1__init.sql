CREATE TABLE vendors
(
    id          SERIAL primary key,
    email       VARCHAR(127) NOT NULL UNIQUE,
    password    VARCHAR(127) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    homepage    VARCHAR(511),
    logo_url    VARCHAR(511),
    created_at  TIMESTAMP    NOT NULL,
    modified_at TIMESTAMP    NOT NULL
);