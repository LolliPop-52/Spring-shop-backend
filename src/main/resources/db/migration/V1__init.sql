CREATE SEQUENCE IF NOT EXISTS bucket_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS category_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS product_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS user_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE buckets
(
    id      BIGINT NOT NULL,
    user_id BIGINT,
    CONSTRAINT pk_buckets PRIMARY KEY (id)
);

CREATE TABLE buckets_products
(
    bucket_id  BIGINT NOT NULL,
    product_id BIGINT NOT NULL
);

CREATE TABLE categories
(
    id    BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL UNIQUE,
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE TABLE products
(
    id          BIGINT       NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(3000),
    price       DECIMAL      NOT NULL,
    image_url   VARCHAR(255) NOT NULL,
    CONSTRAINT pk_products PRIMARY KEY (id)
);

CREATE TABLE products_categories
(
    category_id BIGINT NOT NULL,
    product_id  BIGINT NOT NULL,
    CONSTRAINT pk_products_categories PRIMARY KEY (category_id, product_id)
);

CREATE TABLE users
(
    id       BIGINT       NOT NULL,
    name     VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL,
    role     VARCHAR(255),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE buckets
    ADD CONSTRAINT uc_buckets_user UNIQUE (user_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE buckets
    ADD CONSTRAINT FK_BUCKETS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE buckets_products
    ADD CONSTRAINT fk_bucpro_on_bucket FOREIGN KEY (bucket_id) REFERENCES buckets (id);

ALTER TABLE buckets_products
    ADD CONSTRAINT fk_bucpro_on_product FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE products_categories
    ADD CONSTRAINT fk_procat_on_category FOREIGN KEY (category_id) REFERENCES categories (id);

ALTER TABLE products_categories
    ADD CONSTRAINT fk_procat_on_product FOREIGN KEY (product_id) REFERENCES products (id);