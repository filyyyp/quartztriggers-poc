DROP TABLE IF EXISTS product;

CREATE TABLE IF NOT EXISTS product
(
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    state           VARCHAR(255) NOT NULL,
    expiration_date TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

