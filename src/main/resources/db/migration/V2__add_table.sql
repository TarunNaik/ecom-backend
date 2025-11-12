--Cart table
CREATE TABLE CARTS (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE,
    CONSTRAINT fk_cart_buyer FOREIGN KEY (user_id) REFERENCES users(id)
);
--Cart Items table
CREATE TABLE CART_ITEMS (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT,
    product_id BIGINT,
    quantity INTEGER,
    CONSTRAINT fk_cart FOREIGN KEY (cart_id) REFERENCES carts(id),
    CONSTRAINT fk_cart_to_product FOREIGN KEY (product_id) REFERENCES products(id)
);