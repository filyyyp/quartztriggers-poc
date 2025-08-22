DROP TABLE IF EXISTS product;

CREATE TABLE IF NOT EXISTS product
(
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255),
    expiration_date TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

