CREATE SEQUENCE IF NOT EXISTS bucket_items_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS bucket_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS category_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS order_details_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS order_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS pickup_point_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS product_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS user_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE bucket_items
(
    id         BIGINT  NOT NULL,
    product_id BIGINT  NOT NULL,
    bucket_id  BIGINT,
    amount     DECIMAL NOT NULL,
    CONSTRAINT pk_bucket_items PRIMARY KEY (id)
);

CREATE TABLE buckets
(
    id      BIGINT NOT NULL,
    user_id BIGINT,
    CONSTRAINT pk_buckets PRIMARY KEY (id)
);

CREATE TABLE categories
(
    id    BIGINT       NOT NULL,
    title VARCHAR(255) NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE TABLE order_details
(
    id              BIGINT  NOT NULL,
    order_id        BIGINT,
    product_id      BIGINT,
    amount          DECIMAL NOT NULL,
    total_price     DECIMAL,
    delivery_status VARCHAR(255),
    payment_type    VARCHAR(255),
    CONSTRAINT pk_order_details PRIMARY KEY (id)
);

CREATE TABLE orders
(
    id              BIGINT                      NOT NULL,
    user_id         BIGINT,
    pickup_point_id BIGINT                      NOT NULL,
    created_time    TIMESTAMP WITHOUT TIME ZONE,
    updated_time    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    total_price     DECIMAL,
    total_amount    DECIMAL,
    CONSTRAINT pk_orders PRIMARY KEY (id)
);

CREATE TABLE pickup_points
(
    id           BIGINT       NOT NULL,
    address      VARCHAR(255) NOT NULL,
    opening_time time WITHOUT TIME ZONE,
    closing_time time WITHOUT TIME ZONE,
    CONSTRAINT pk_pickup_points PRIMARY KEY (id)
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

ALTER TABLE categories
    ADD CONSTRAINT uc_categories_title UNIQUE (title);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE buckets
    ADD CONSTRAINT FK_BUCKETS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE bucket_items
    ADD CONSTRAINT FK_BUCKET_ITEMS_ON_BUCKET FOREIGN KEY (bucket_id) REFERENCES buckets (id);

ALTER TABLE bucket_items
    ADD CONSTRAINT FK_BUCKET_ITEMS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE orders
    ADD CONSTRAINT FK_ORDERS_ON_PICKUP_POINT FOREIGN KEY (pickup_point_id) REFERENCES pickup_points (id);

ALTER TABLE orders
    ADD CONSTRAINT FK_ORDERS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE order_details
    ADD CONSTRAINT FK_ORDER_DETAILS_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);

ALTER TABLE order_details
    ADD CONSTRAINT FK_ORDER_DETAILS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE products_categories
    ADD CONSTRAINT fk_procat_on_category FOREIGN KEY (category_id) REFERENCES categories (id);

ALTER TABLE products_categories
    ADD CONSTRAINT fk_procat_on_product FOREIGN KEY (product_id) REFERENCES products (id);