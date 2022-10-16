CREATE TABLE vendors
(
    id          INT primary key,
    email       VARCHAR(127) NOT NULL,
    password    VARCHAR(127) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    homepage    VARCHAR(511) NOT NULL,
    logo_url    VARCHAR(511) NOT NULL,
    created_at  TIMESTAMP    NOT NULL
);