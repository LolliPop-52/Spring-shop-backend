INSERT INTO products (id, title, description, price, image_url)
VALUES (nextval('product_seq'), 'MacBook Neo 2026', 'Макбук оригинальный', 54799, 'https://i.ibb.co/k2nr7JVq/9946756969.webp');


INSERT INTO categories  (id, title)
VALUES (1, 'Ноутбук'),
       (2, 'macbook');

INSERT INTO products_categories (product_id, category_id)
VALUES (currval('product_seq'), 1),
       (currval('product_seq'), 2);

