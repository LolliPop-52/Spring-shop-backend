CREATE TABLE buckets
(
    id      BIGINT NOT NULL,
    user_id BIGINT NULL,
    CONSTRAINT pk_buckets PRIMARY KEY (id)
);

CREATE TABLE buckets_products
(
    bucket_id  BIGINT NOT NULL,
    product_id BIGINT NOT NULL
);

CREATE TABLE categories
(
    id    BIGINT       NOT NULL,
    title VARCHAR(255) NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE TABLE orders
(
    id      BIGINT       NOT NULL,
    created TIMESTAMP     NULL,
    updated TIMESTAMP     NULL,
    user_id BIGINT       NULL,
    sum     DECIMAL      NULL,
    address VARCHAR(255) NULL,
    status  VARCHAR(255) NULL,
    CONSTRAINT pk_orders PRIMARY KEY (id)
);

CREATE TABLE orders_details
(
    id         BIGINT  NOT NULL,
    order_id   BIGINT  NULL,
    product_id BIGINT  NULL,
    amount     DECIMAL NULL,
    price      DECIMAL NULL,
    CONSTRAINT pk_orders_details PRIMARY KEY (id)
);

CREATE TABLE products
(
    id    BIGINT       NOT NULL,
    title VARCHAR(255) NULL,
    price DECIMAL      NULL,
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
    id        BIGINT       NOT NULL,
    name      VARCHAR(255) NULL,
    password  VARCHAR(255) NULL,
    email     VARCHAR(255) NULL,
    archive   BOOLEAN      NOT NULL,
    role    VARCHAR(255) NULL,
    bucket_id BIGINT       NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE buckets
    ADD CONSTRAINT uc_buckets_user UNIQUE (user_id);

ALTER TABLE buckets
    ADD CONSTRAINT FK_BUCKETS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE orders_details
    ADD CONSTRAINT FK_ORDERS_DETAILS_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);

ALTER TABLE orders_details
    ADD CONSTRAINT FK_ORDERS_DETAILS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE orders
    ADD CONSTRAINT FK_ORDERS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_BUCKET FOREIGN KEY (bucket_id) REFERENCES buckets (id);

ALTER TABLE buckets_products
    ADD CONSTRAINT fk_bucpro_on_bucket FOREIGN KEY (bucket_id) REFERENCES buckets (id);

ALTER TABLE buckets_products
    ADD CONSTRAINT fk_bucpro_on_product FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE products_categories
    ADD CONSTRAINT fk_procat_on_category FOREIGN KEY (category_id) REFERENCES categories (id);

ALTER TABLE products_categories
    ADD CONSTRAINT fk_procat_on_product FOREIGN KEY (product_id) REFERENCES products (id);